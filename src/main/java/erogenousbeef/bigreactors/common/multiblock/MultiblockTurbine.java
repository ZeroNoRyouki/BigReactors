package erogenousbeef.bigreactors.common.multiblock;

import erogenousbeef.bigreactors.api.data.CoilPartData;
import erogenousbeef.bigreactors.api.registry.TurbineCoil;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.block.BlockBRMetal;
import erogenousbeef.bigreactors.common.multiblock.block.ITurbineRotorPart;
import erogenousbeef.bigreactors.common.multiblock.helpers.FloatUpdateTracker;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.common.multiblock.tileentity.*;
import erogenousbeef.bigreactors.gui.container.ISlotlessUpdater;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrFluids;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import erogenousbeef.bigreactors.net.message.multiblock.TurbineUpdateMessage;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.block.ModTileEntity;
import it.zerono.mods.zerocore.lib.config.IConfigListener;
import it.zerono.mods.zerocore.util.CodeHelper;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;

public class MultiblockTurbine extends RectangularMultiblockControllerBase implements IPowerGenerator, ISlotlessUpdater,
		IActivateable, IConfigListener, IDebuggable {

	public enum VentStatus {
		VentOverflow,
		VentAll,
		DoNotVent
	}
	public static final VentStatus[] s_VentStatuses = VentStatus.values();
	
	// UI updates
	private Set<EntityPlayer> updatePlayers;
	private int ticksSinceLastUpdate;
	private static final int ticksBetweenUpdates = 3;

	// Fluid tanks. Input = Steam, Output = Water.
	//public static final int TANK_INPUT = 0;
	//public static final int TANK_OUTPUT = 1;
	//public static final int NUM_TANKS = 2;
	public static final int FLUID_NONE = -1;
	public static final int TANK_SIZE = 4000;
	public static final int MAX_PERMITTED_FLOW = 2000;
	public static final int BASE_FLUID_PER_BLADE = 25; // mB

	//private FluidTank[] tanks;
	private FluidTank _inputTank;
	private FluidTank _outputTank;
	
	static final float maxEnergyStored = 1000000f; // 1 MegaRF
	
	// Persistent game data
	float energyStored;
	private PowerSystem _powerSystem;
	private PartTier _partsTier;
	private boolean _legacyMode;
	private boolean active;
	float rotorEnergy;
	boolean inductorEngaged;

	// Player settings
	VentStatus ventStatus;
	int maxIntakeRate;
	
	// Derivable game data
	int bladeSurfaceArea; // # of blocks that are blades
	int rotorMass; // 10 = 1 standard block-weight
	int coilSize;  // number of blocks in the coils
	
	// Inductor dynamic constants - get from a table on assembly
	float inductorDragCoefficient = inductorBaseDragCoefficient;
	float inductionEfficiency = 0.5f; // Final energy rectification efficiency. Averaged based on coil material and shape. 0.25-0.5 = iron, 0.75-0.9 = diamond, 1 = perfect.
	float inductionEnergyExponentBonus = 1f; // Exponential bonus to energy generation. Use this for very rare materials or special constructs.

	// Rotor dynamic constants - calculate on assembly
	float rotorDragCoefficient = 0.01f; // RF/t lost to friction per unit of mass in the rotor.
	float bladeDrag			   = 0.00025f; // RF/t lost to friction, multiplied by rotor speed squared. 
	float frictionalDrag	   = 0f;

	// Penalize suboptimal shapes with worse drag (i.e. increased drag without increasing lift)
	// Suboptimal is defined as "not a christmas-tree shape". At worst, drag is increased 4x.
	
	// Game balance constants - some of these are modified by configs at startup
	public static int inputFluidPerBlade = BASE_FLUID_PER_BLADE; // mB
	private static float inductorBaseDragCoefficient = 0.1f; // RF/t extracted per coil block, multiplied by rotor speed squared.
	private static final float baseBladeDragCoefficient = 0.00025f; // RF/t base lost to aero drag per blade block. Includes a 50% reduction to factor in constant parts of the drag equation
	
	float energyGeneratedLastTick;
	int fluidConsumedLastTick;
	float rotorEfficiencyLastTick;
	
	private Set<IMultiblockPart> attachedControllers;
	private Set<TileEntityTurbineRotorBearing> attachedRotorBearings;
	
	private Set<TileEntityTurbinePowerTap> attachedPowerTaps;
	private Set<ITickableMultiblockPart> attachedTickables;
	
	private Set<TileEntityTurbineRotorShaft> attachedRotorShafts;
	private Set<TileEntityTurbineRotorBlade> attachedRotorBlades;
	
	private Set<TileEntityTurbinePartGlass> attachedGlass; 
	
	// Data caches for validation
	private Set<BlockPos> foundCoils;

	private FloatUpdateTracker rpmUpdateTracker;
	
	private static final EnumFacing[] RotorXBladeDirections = new EnumFacing[] { EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.DOWN, EnumFacing.NORTH };
	private static final EnumFacing[] RotorZBladeDirections = new EnumFacing[] { EnumFacing.UP, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.WEST };

	public MultiblockTurbine(World world) {
		super(world);

		updatePlayers = new HashSet<EntityPlayer>();
		
		ticksSinceLastUpdate = 0;
		/*
		tanks = new FluidTank[NUM_TANKS];
		for(int i = 0; i < NUM_TANKS; i++)
			tanks[i] = new FluidTank(TANK_SIZE);
		*/
		
		attachedControllers = new HashSet<IMultiblockPart>();
		attachedRotorBearings = new HashSet<TileEntityTurbineRotorBearing>();
		attachedPowerTaps = new HashSet<TileEntityTurbinePowerTap>();
		attachedTickables = new HashSet<ITickableMultiblockPart>();
		attachedRotorShafts = new HashSet<TileEntityTurbineRotorShaft>();
		attachedRotorBlades = new HashSet<TileEntityTurbineRotorBlade>();
		attachedGlass = new HashSet<TileEntityTurbinePartGlass>();
		
		energyStored = 0f;
		this._powerSystem = PowerSystem.RedstoneFlux;
		active = false;
		inductorEngaged = true;
		ventStatus = VentStatus.VentOverflow;
		rotorEnergy = 0f;
		maxIntakeRate = MAX_PERMITTED_FLOW;
		
		bladeSurfaceArea = 0;
		rotorMass = 0;
		coilSize = 0;
		energyGeneratedLastTick = 0f;
		fluidConsumedLastTick = 0;
		rotorEfficiencyLastTick = 1f;
		
		foundCoils = new HashSet<BlockPos>();
		
		rpmUpdateTracker = new FloatUpdateTracker(100, 5, 10f, 100f); // Minimum 10RPM difference for slow updates, if change > 100 RPM, update every 5 ticks

		this._inputTank = new FluidTank(TANK_SIZE) {

			@Override
			public int fill(FluidStack resource, boolean doFill) {

				// allow only steam in the input tank

				if (null == resource || BrFluids.fluidSteam != resource.getFluid())
					return 0;

				return super.fill(resource, doFill);
			}
		};

		this._inputTank.setCanDrain(false);

		this._outputTank = new FluidTank(TANK_SIZE);
		this._outputTank.setCanFill(false);

		this._partsTier = PartTier.Legacy;
		this._legacyMode = false;
	}

	@Override
	public void onConfigChanged() {

		// energy/t extracted per coil block, multiplied by rotor speed squared.
		MultiblockTurbine.inductorBaseDragCoefficient = 0.1f * BigReactors.CONFIG.turbineCoilDragMultiplier;
		MultiblockTurbine.inputFluidPerBlade = (int) Math.floor(BASE_FLUID_PER_BLADE * BigReactors.CONFIG.turbineFluidPerBladeMultiplier);

		this.recalculateDerivedStatistics();
	}

	/**
	 * Sends a full state update to a player.
	 */
	protected void sendIndividualUpdate(EntityPlayer player) {
		if(this.WORLD.isRemote) { return; }

        CommonPacketHandler.INSTANCE.sendTo(getUpdatePacket(), (EntityPlayerMP)player);
	}

	protected IMessage getUpdatePacket() {
        return new TurbineUpdateMessage(this);
	}

	/**
	 * Send an update to any clients with GUIs open
	 */
	protected void sendTickUpdate() {
		if(this.updatePlayers.size() <= 0) { return; }

		for(EntityPlayer player : updatePlayers) {
            CommonPacketHandler.INSTANCE.sendTo(getUpdatePacket(), (EntityPlayerMP)player);
		}
	}

	// MultiblockControllerBase overrides

	@Override
	public void onAttachedPartWithMultiblockData(IMultiblockPart part, NBTTagCompound data) {
		this.syncDataFrom(data, ModTileEntity.SyncReason.FullSync);
	}

	@Override
	protected void onBlockAdded(IMultiblockPart newPart) {
		if(newPart instanceof TileEntityTurbineRotorBearing) {
			this.attachedRotorBearings.add((TileEntityTurbineRotorBearing)newPart);
		}
		
		if(newPart instanceof TileEntityTurbinePowerTap) {
			attachedPowerTaps.add((TileEntityTurbinePowerTap)newPart);
		}
		
		if(newPart instanceof ITickableMultiblockPart) {
			attachedTickables.add((ITickableMultiblockPart)newPart);
		}
		
		if (newPart instanceof TileEntityTurbineRotorShaft)
			this.attachedRotorShafts.add((TileEntityTurbineRotorShaft)newPart);

		if (newPart instanceof TileEntityTurbineRotorBlade)
			this.attachedRotorBlades.add((TileEntityTurbineRotorBlade)newPart);
		
		if(newPart instanceof TileEntityTurbinePartGlass) {
			attachedGlass.add((TileEntityTurbinePartGlass)newPart);
		}
	}

	@Override
	protected void onBlockRemoved(IMultiblockPart oldPart) {
		if(oldPart instanceof TileEntityTurbineRotorBearing) {
			this.attachedRotorBearings.remove(oldPart);
		}
		
		if(oldPart instanceof TileEntityTurbinePowerTap) {
			attachedPowerTaps.remove(oldPart);
		}

		if(oldPart instanceof ITickableMultiblockPart) {
			attachedTickables.remove(oldPart);
		}

		if (oldPart instanceof TileEntityTurbineRotorShaft)
			this.attachedRotorShafts.remove(oldPart);

		if (oldPart instanceof TileEntityTurbineRotorBlade)
			this.attachedRotorBlades.remove(oldPart);
		
		if(oldPart instanceof TileEntityTurbinePartGlass) {
			attachedGlass.remove(oldPart);
		}
	}

	@Override
	protected void onMachineAssembled() {

		this.recalculateDerivedStatistics();

		// determine machine tier

		PartTier candidateTier = null;

		for (IMultiblockPart part: this.connectedParts) {

			if (part instanceof TileEntityReactorPartBase) {

				PartTier tier = ((TileEntityReactorPartBase)part).getPartTier();

				if (null == candidateTier)
					candidateTier = tier;

				else if (candidateTier != tier) {

					// this should never happen but ...
					throw new IllegalStateException("Found block of a different tier while assembling the machine!");
				}
			}
		}

		this._partsTier = candidateTier;
		this._legacyMode = PartTier.Legacy == candidateTier;

		// determine machine power system

		PowerSystem candidatePowerSystem = PowerSystem.RedstoneFlux;

		if (this.attachedPowerTaps.size() > 0) {

			int rf = 0, tesla = 0;

			for (TileEntityTurbinePowerTap tap : this.attachedPowerTaps) {

				if (tap instanceof TileEntityTurbinePowerTapRedstoneFlux)
					++rf;
				else if (tap instanceof TileEntityTurbinePowerTapTesla)
					++tesla;
			}

			if (rf != 0 && tesla != 0) {

				// this should never happen but ...
				throw new IllegalStateException("Found different power taps while assembling the machine!");
			}

			candidatePowerSystem = tesla > 0 ? PowerSystem.Tesla : PowerSystem.RedstoneFlux;
		}

		this.switchPowerSystem(candidatePowerSystem);
	}

	@Override
	protected void onMachineRestored() {
		this.onMachineAssembled();
	}

	@Override
	protected void onMachinePaused() {
	}

	@Override
	protected void onMachineDisassembled() {
		rotorMass = 0;
		bladeSurfaceArea = 0;
		coilSize = 0;
		
		rotorEnergy = 0f; // Kill energy when machines get broken by players/explosions
		rpmUpdateTracker.setValue(0f);
	}

	// Validation code
	@Override
	protected boolean isMachineWhole(IMultiblockValidator validatorCallback) {

		if(attachedRotorBearings.size() != 1) {

			validatorCallback.setLastError("multiblock.validation.turbine.invalid_rotor_count");
			return false;
		}
		
		// Set up validation caches
		foundCoils.clear();
		
		if (!super.isMachineWhole(validatorCallback))
			return false;
		
		// Now do additional validation based on the coils/blades/rotors that were found
		
		// Check that we have a rotor that goes all the way up the bearing
		final TileEntityTurbinePartBase rotorBearing = attachedRotorBearings.iterator().next();
		
		// Rotor bearing must calculate outwards dir, as this is normally only calculated in onMachineAssembled().
		rotorBearing.recalculateOutwardsDirection(getMinimumCoord(), getMaximumCoord());

		// Find out which way the rotor runs. Obv, this is inwards from the bearing.
		EnumFacing rotorDir = rotorBearing.getOutwardFacing();
		final EnumFacing.Axis rotatedDirAxis = rotorDir.getAxis();

		if (null != rotorDir) {

			rotorDir = rotorDir.getOpposite();

		} else {

			validatorCallback.setLastError("multiblock.validation.turbine.incomplete");
			return false;
		}

		// Figure out where the rotor ends and which directions are normal to the rotor's 4 faces (this is where blades emit from)

		BlockPos turbineMinCoord = this.getMinimumCoord();
		BlockPos turbineMaxCoord = this.getMaximumCoord();
		int turbineLength;

		switch (rotorDir.getAxis()) {

			case X:
				turbineLength = turbineMaxCoord.getX() - turbineMinCoord.getX();
				break;

			default:
			case Y:
				turbineLength = turbineMaxCoord.getY() - turbineMinCoord.getY();
				break;

			case Z:
				turbineLength = turbineMaxCoord.getZ() - turbineMinCoord.getZ();
				break;
		}

		turbineLength = Math.abs(turbineLength) - 1;

		BlockPos rotorCoord = rotorBearing.getWorldPosition();
		BlockPos endRotorCoord = rotorCoord.offset(rotorDir, turbineLength);
		final EnumFacing[] bladeDirections = RotorShaftState.getBladesDirections(rotorDir.getAxis());
		Set<BlockPos> rotorShafts = new HashSet<BlockPos>(attachedRotorShafts.size());
		Set<BlockPos> rotorBlades = new HashSet<BlockPos>(attachedRotorBlades.size());
		
		for(TileEntityTurbineRotorShaft part : attachedRotorShafts) {
			rotorShafts.add(part.getWorldPosition());
		}

		for(TileEntityTurbineRotorBlade part : attachedRotorBlades) {
			rotorBlades.add(part.getWorldPosition());
		}

		// Move along the length of the rotor, 1 block at a time
		boolean encounteredCoils = false;

		while (!rotorShafts.isEmpty() && !rotorCoord.equals(endRotorCoord)) {

			rotorCoord = rotorCoord.offset(rotorDir);
			
			// Ensure we find a rotor block along the length of the entire rotor
			if(!rotorShafts.remove(rotorCoord)) {
				validatorCallback.setLastError("multiblock.validation.turbine.block_must_be_rotor", rotorCoord);
				return false;
			}
			
			// Now move out in the 4 rotor normals, looking for blades and coils
			BlockPos checkCoord;
			boolean encounteredBlades = false;

			for(EnumFacing bladeDir : bladeDirections) {

				boolean foundABlade = false;

				checkCoord = rotorCoord.offset(bladeDir);
				
				// If we find 1 blade, we can keep moving along the normal to find more blades
				while(rotorBlades.remove(checkCoord)) {
					// We found a coil already?! NOT ALLOWED.
					if(encounteredCoils) {
						validatorCallback.setLastError("multiblock.validation.turbine.blades_too_far", checkCoord);
						return false;
					}
					foundABlade = encounteredBlades = true;
					checkCoord = checkCoord.offset(bladeDir);
				}

				// If this block wasn't a blade, check to see if it was a coil
				if(!foundABlade) {
					if(foundCoils.remove(checkCoord)) {
						encounteredCoils = true;

						// We cannot have blades and coils intermix. This prevents intermixing, depending on eval order.
						if(encounteredBlades) {
							validatorCallback.setLastError("multiblock.validation.turbine.metal_too_near", checkCoord);
							return false;
						}
						
						// Check the two coil spots in the 'corners', which are permitted if they're connected to the main rotor coil somehow

						BlockPos coilCheck;
						EnumFacing rotatedDir;

						rotatedDir = bladeDir.rotateAround(rotatedDirAxis);
						coilCheck = checkCoord.offset(rotatedDir);
						foundCoils.remove(coilCheck);

						rotatedDir = rotatedDir.rotateAround(rotatedDirAxis).rotateAround(rotatedDirAxis);
						coilCheck = checkCoord.offset(rotatedDir);
						foundCoils.remove(coilCheck);
					}
					// Else: It must have been air.
				}
			}
		}

		if (!rotorCoord.equals(endRotorCoord)) {

			validatorCallback.setLastError("multiblock.validation.turbine.shaft_too_short");
			return false;
		}
		
		// Ensure that we encountered all the rotor, blade and coil blocks. If not, there's loose stuff inside the turbine.
		if (!rotorShafts.isEmpty()) {

			validatorCallback.setLastError("multiblock.validation.turbine.found_loose_rotor_blocks", rotorShafts.size());
			return false;
		}

		if (!rotorBlades.isEmpty()) {

			validatorCallback.setLastError("multiblock.validation.turbine.found_loose_rotor_blades", rotorBlades.size());
			return false;
		}
		
		if (!foundCoils.isEmpty()) {

			validatorCallback.setLastError("multiblock.validation.turbine.invalid_metals_shape", foundCoils.size());
			return false;
		}

		final TileEntity te = this.WORLD.getTileEntity(rotorCoord.offset(rotorDir));
		final boolean rotorEndValid = (te instanceof TileEntityTurbinePart) && BrBlocks.turbineHousing == te.getBlockType();

		if (!rotorEndValid) {

			validatorCallback.setLastError("multiblock.validation.turbine.invalid_rotor_end");
			return false;
		}

		// check if the machine is single-tier

		PartTier candidateTier = null;

		for (IMultiblockPart part: this.connectedParts) {

			if (part instanceof TileEntityTurbinePartBase) {

				PartTier tier = ((TileEntityTurbinePartBase)part).getPartTier();

				if (null == candidateTier)
					candidateTier = tier;

				else if (candidateTier != tier) {

					validatorCallback.setLastError("multiblock.validation.turbine.mixed_tiers");
					return false;
				}
			}
		}

		// check if the machine has a single power system

		if (this.attachedPowerTaps.size() > 0) {

			int rf = 0, tesla = 0;

			for (TileEntityTurbinePowerTap tap : this.attachedPowerTaps) {

				if (tap instanceof TileEntityTurbinePowerTapRedstoneFlux)
					++rf;
				else if (tap instanceof TileEntityTurbinePowerTapTesla)
					++tesla;
			}

			if (rf != 0 && tesla != 0) {

				validatorCallback.setLastError("multiblock.validation.turbine.mixed_power_systems");
				return false;
			}
		}

		// A-OK!
		return true;
	}

	@Override
	protected boolean isBlockGoodForInterior(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {
		// We only allow air and functional parts in turbines.
		BlockPos position = new BlockPos(x, y, z);

		// Air is ok
		if(world.isAirBlock(position)) { return true; }

		// Coil windings below here:
		if (this.getCoilPartData(world.getBlockState(position)) != null) {

			foundCoils.add(position);
			return true;
		}

		// Everything else, gtfo
		validatorCallback.setLastError("multiblock.validation.turbine.invalid_block_for_interior", x, y, z);
		return false;
	}

	@Override
	protected boolean isBlockGoodForFrame(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.WORLD.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected boolean isBlockGoodForTop(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.WORLD.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected boolean isBlockGoodForBottom(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.WORLD.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected boolean isBlockGoodForSides(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.WORLD.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected int getMinimumNumberOfBlocksForAssembledMachine() {
		// Hollow 5x5x4 cube (100 - 18), interior minimum is 3x3x2
		return 82;
	}

	@Override
	protected int getMaximumXSize() {
		return BigReactors.CONFIG.maxTurbineSize;
	}

	@Override
	protected int getMaximumZSize() {
		return BigReactors.CONFIG.maxTurbineSize;
	}

	@Override
	protected int getMaximumYSize() {
		return BigReactors.CONFIG.maxTurbineHeight;
	}
	
	@Override
	protected int getMinimumXSize() { return 5; }

	@Override
	protected int getMinimumYSize() { return 4; }

	@Override
	protected int getMinimumZSize() { return 5; }
	
	
	@Override
	protected void onAssimilate(MultiblockControllerBase otherMachine) {
		if(!(otherMachine instanceof MultiblockTurbine)) {
			BRLog.warning("[%s] Turbine @ %s is attempting to assimilate a non-Turbine machine! That machine's data will be lost!", WORLD.isRemote?"CLIENT":"SERVER", getReferenceCoord());
			return;
		}
		
		MultiblockTurbine otherTurbine = (MultiblockTurbine)otherMachine;
		
		setRotorEnergy(Math.max(rotorEnergy, otherTurbine.rotorEnergy));
	}

	@Override
	protected void onAssimilated(MultiblockControllerBase assimilator) {
		attachedControllers.clear();
		attachedRotorBearings.clear();
		attachedTickables.clear();
		attachedPowerTaps.clear();
	}

	@Override
	protected boolean updateServer() {
		energyGeneratedLastTick = 0f;
		fluidConsumedLastTick = 0;
		rotorEfficiencyLastTick = 1f;
		
		// Generate energy based on steam
		int steamIn = 0; // mB. Based on water, actually. Probably higher for steam. Measure it.

		if(getActive()) {
			// Spin up via steam inputs, convert some steam back into water.
			// Use at most the user-configured max, or the amount in the tank, whichever is less.
			steamIn = Math.min(maxIntakeRate, this._inputTank.getFluidAmount());
			
			if(ventStatus == VentStatus.DoNotVent) {
				// Cap steam used to available space, if not venting
				int availableSpace = this._outputTank.getCapacity() - this._outputTank.getFluidAmount();
				steamIn = Math.min(steamIn, availableSpace);
			}
		}
		
		if(steamIn > 0 || rotorEnergy > 0) {
			float rotorSpeed = getRotorSpeed();

			// RFs lost to aerodynamic drag.
			float aerodynamicDragTorque = (float)rotorSpeed * bladeDrag;

			float liftTorque = 0f;
			if(steamIn > 0) {
				// TODO: Lookup fluid parameters from a table
				float fluidEnergyDensity = 10f; // RF per mB

				// Cap amount of steam we can fully extract energy from based on blade size
				int steamToProcess = bladeSurfaceArea * inputFluidPerBlade;
				steamToProcess = Math.min(steamToProcess, steamIn);
				liftTorque = steamToProcess * fluidEnergyDensity;

				// Did we have excess steam for our blade size?
				if(steamToProcess < steamIn) {
					// Extract some percentage of the remaining steam's energy, based on how many blades are missing
					steamToProcess = steamIn - steamToProcess;
					float bladeEfficiency = 1f;
					int neededBlades = steamIn / inputFluidPerBlade; // round in the player's favor
					int missingBlades = neededBlades - bladeSurfaceArea;
					bladeEfficiency = 1f - (float)missingBlades / (float)neededBlades;
					liftTorque += steamToProcess * fluidEnergyDensity * bladeEfficiency;

					rotorEfficiencyLastTick = liftTorque / (steamIn * fluidEnergyDensity);
				}
			}

			// Yay for derivation. We're assuming delta-Time is always 1, as we're always calculating for 1 tick.
			// RFs available to coils
			float inductionTorque = inductorEngaged ? rotorSpeed * inductorDragCoefficient * coilSize : 0f;
			float energyToGenerate = (float)Math.pow(inductionTorque, inductionEnergyExponentBonus) * inductionEfficiency;
			if(energyToGenerate > 0f) {
				// Efficiency curve. Rotors are 50% less efficient when not near 900/1800 RPMs.
				float efficiency = (float)(0.25*Math.cos(rotorSpeed/(45.5*Math.PI))) + 0.75f;
				if(rotorSpeed < 500) {
					efficiency = Math.min(0.5f, efficiency);
				}

				generateEnergy(energyToGenerate * efficiency);
			}

			rotorEnergy += liftTorque + -1f*inductionTorque + -1f*aerodynamicDragTorque + -1f*frictionalDrag;
			if(rotorEnergy < 0f) { rotorEnergy = 0f; }
			
			// And create some water
			if(steamIn > 0) {

				this.fluidConsumedLastTick = steamIn;
				this._inputTank.drainInternal(steamIn, true);
				
				if(ventStatus != VentStatus.VentAll) {

					Fluid effluent = FluidRegistry.WATER;
					FluidStack effluentStack = new FluidStack(effluent, steamIn);

					this._outputTank.fillInternal(effluentStack, true);
				}
			}
		}
		
		int energyAvailable = (int)getEnergyStored();
		int energyRemaining = energyAvailable;
		if(energyStored > 0 && attachedPowerTaps.size() > 0) {
			// First, try to distribute fairly
			int splitEnergy = energyRemaining / attachedPowerTaps.size();
			for(TileEntityTurbinePowerTap powerTap : attachedPowerTaps) {
				if(energyRemaining <= 0) { break; }
				if(powerTap == null || !powerTap.isConnected()) { continue; }

				energyRemaining -= splitEnergy - powerTap.onProvidePower(splitEnergy);
			}

			// Next, just hose out whatever we can, if we have any left
			if(energyRemaining > 0) {
				for(TileEntityTurbinePowerTap powerTap : attachedPowerTaps) {
					if(energyRemaining <= 0) { break; }
					if(powerTap == null || !powerTap.isConnected()) { continue; }

					energyRemaining = (int)powerTap.onProvidePower(energyRemaining);
				}
			}
		}
		
		if(energyAvailable != energyRemaining) {
			reduceStoredEnergy((energyAvailable - energyRemaining));
		}
		
		for(ITickableMultiblockPart part : attachedTickables) {
			part.onMultiblockServerTick();
		}
		
		ticksSinceLastUpdate++;
		if(ticksSinceLastUpdate >= ticksBetweenUpdates) {
			sendTickUpdate();
			ticksSinceLastUpdate = 0;
		}
		
		if(rpmUpdateTracker.shouldUpdate(getRotorSpeed())) {
			markReferenceCoordDirty();
		}

		return energyGeneratedLastTick > 0 || fluidConsumedLastTick > 0;
	}

	@Override
	protected void updateClient() {
	}

	@Override
	protected void syncDataFrom(NBTTagCompound data, ModTileEntity.SyncReason syncReason) {

		if(data.hasKey("inputTank")) {
			this._inputTank.readFromNBT(data.getCompoundTag("inputTank"));
		}

		if(data.hasKey("outputTank")) {
			this._outputTank.readFromNBT(data.getCompoundTag("outputTank"));
		}

		if(data.hasKey("active")) {
			setActive(data.getBoolean("active"));
		}

		if(data.hasKey("energy")) {
			setEnergyStored(data.getFloat("energy"));
		}

		if(data.hasKey("ventStatus")) {
			setVentStatus(VentStatus.values()[data.getInteger("ventStatus")], false);
		}

		if(data.hasKey("rotorEnergy")) {
			setRotorEnergy(data.getFloat("rotorEnergy"));

			if(!WORLD.isRemote) {
				rpmUpdateTracker.setValue(getRotorSpeed());
			}
		}

		if(data.hasKey("maxIntakeRate")) {
			maxIntakeRate = data.getInteger("maxIntakeRate");
		}

		if(data.hasKey("inductorEngaged")) {
			setInductorEngaged(data.getBoolean("inductorEngaged"), false);
		}
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, ModTileEntity.SyncReason syncReason) {

		data.setTag("inputTank", this._inputTank.writeToNBT(new NBTTagCompound()));
		data.setTag("outputTank", this._outputTank.writeToNBT(new NBTTagCompound()));
		data.setBoolean("active", active);
		data.setFloat("energy", energyStored);
		data.setInteger("ventStatus", ventStatus.ordinal());
		data.setFloat("rotorEnergy", rotorEnergy);
		data.setInteger("maxIntakeRate", maxIntakeRate);
		data.setBoolean("inductorEngaged", inductorEngaged);
	}

	/*
	@Override
	public void writeToNBT(NBTTagCompound data) {
		data.setTag("inputTank", tanks[TANK_INPUT].writeToNBT(new NBTTagCompound()));
		data.setTag("outputTank", tanks[TANK_OUTPUT].writeToNBT(new NBTTagCompound()));
		data.setBoolean("active", active);
		data.setFloat("energy", energyStored);
		data.setInteger("ventStatus", ventStatus.ordinal());
		data.setFloat("rotorEnergy", rotorEnergy);
		data.setInteger("maxIntakeRate", maxIntakeRate);
		data.setBoolean("inductorEngaged", inductorEngaged);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		if(data.hasKey("inputTank")) {
			tanks[TANK_INPUT].readFromNBT(data.getCompoundTag("inputTank"));
		}
		
		if(data.hasKey("outputTank")) {
			tanks[TANK_OUTPUT].readFromNBT(data.getCompoundTag("outputTank"));
		}
		
		if(data.hasKey("active")) {
			setActive(data.getBoolean("active"));
		}
		
		if(data.hasKey("energy")) {
			setEnergyStored(data.getFloat("energy"));
		}
		
		if(data.hasKey("ventStatus")) {
			setVentStatus(VentStatus.values()[data.getInteger("ventStatus")], false);
		}
		
		if(data.hasKey("rotorEnergy")) {
			setRotorEnergy(data.getFloat("rotorEnergy"));
			
			if(!WORLD.isRemote) {
				rpmUpdateTracker.setValue(getRotorSpeed());
			}
		}
		
		if(data.hasKey("maxIntakeRate")) {
			maxIntakeRate = data.getInteger("maxIntakeRate");
		}
		
		if(data.hasKey("inductorEngaged")) {
			setInductorEngaged(data.getBoolean("inductorEngaged"), false);
		}
	}*/
	/*
	@Override
	public void formatDescriptionPacket(NBTTagCompound data) {
		writeToNBT(data);
	}

	@Override
	public void decodeDescriptionPacket(NBTTagCompound data) {
		readFromNBT(data);
	}
	*/

	// Network Serialization
	/**
	 * Used when dispatching update packets from the server.
	 * @param buf ByteBuf into which the turbine's full status should be written
	 */
	public void serialize(ByteBuf buf) {
		// Capture compacted fluid data first
		String inputFluidID, outputFluidID;
		int inputFluidAmt, outputFluidAmt;
		{
			FluidStack inputFluid, outputFluid;
			inputFluid = this._inputTank.getFluid();
			outputFluid = this._outputTank.getFluid();
			
			if(inputFluid == null || inputFluid.amount <= 0) {
				inputFluidID = "";
				inputFluidAmt = 0;
			}
			else {
				inputFluidID = inputFluid.getFluid().getName();
				inputFluidAmt = inputFluid.amount;
			}
			
			if(outputFluid == null || outputFluid.amount <= 0) {
				outputFluidID = "";
				outputFluidAmt = 0;
			}
			else {
				outputFluidID = outputFluid.getFluid().getName();
				outputFluidAmt = outputFluid.amount;
			}
		}

		// User settings
		buf.writeBoolean(active);
		buf.writeBoolean(inductorEngaged);
		buf.writeInt(ventStatus.ordinal());
		buf.writeInt(maxIntakeRate);

		// Basic stats
		buf.writeFloat(energyStored);
		buf.writeFloat(rotorEnergy);

		// Reportage statistics
		buf.writeFloat(energyGeneratedLastTick);
		buf.writeInt(fluidConsumedLastTick);
		buf.writeFloat(rotorEfficiencyLastTick);
		
		// Fluid data
		ByteBufUtils.writeUTF8String(buf, inputFluidID);
		buf.writeInt(inputFluidAmt);
		ByteBufUtils.writeUTF8String(buf, outputFluidID);
		buf.writeInt(outputFluidAmt);
	}
	
	/**
	 * Used when a status packet arrives on the client.
	 * @param buf ByteBuf containing serialized turbine data
	 */
	public void deserialize(ByteBuf buf) {
		// User settings
		setActive(buf.readBoolean());
		setInductorEngaged(buf.readBoolean(), false);
		setVentStatus(s_VentStatuses[buf.readInt()], false);
		setMaxIntakeRate(buf.readInt());
		
		// Basic data
		setEnergyStored(buf.readFloat());
		setRotorEnergy(buf.readFloat());
		
		// Reportage
		energyGeneratedLastTick = buf.readFloat();
		fluidConsumedLastTick = buf.readInt();
		rotorEfficiencyLastTick = buf.readFloat();
	
		// Fluid data
		String inputFluidID = ByteBufUtils.readUTF8String(buf);
		int inputFluidAmt = buf.readInt();
		String outputFluidID = ByteBufUtils.readUTF8String(buf);
		int outputFluidAmt = buf.readInt();

		if(inputFluidID.isEmpty() || inputFluidAmt <= 0) {
			this._inputTank.setFluid(null);
		} else {
			Fluid fluid = FluidRegistry.getFluid(inputFluidID);
			if(fluid == null) {
				BRLog.warning("[CLIENT] Multiblock Turbine received an unknown fluid of type %d, setting input tank to empty", inputFluidID);
				this._inputTank.setFluid(null);
			}
			else {
				this._inputTank.setFluid(new FluidStack(fluid, inputFluidAmt));
			}
		}

		if(outputFluidID.isEmpty() || outputFluidAmt <= 0) {
			this._outputTank.setFluid(null);
		}
		else {
			Fluid fluid = FluidRegistry.getFluid(outputFluidID);
			if(fluid == null) {
				BRLog.warning("[CLIENT] Multiblock Turbine received an unknown fluid of type %d, setting output tank to empty", outputFluidID);
				this._outputTank.setFluid(null);
			}
			else {
				this._outputTank.setFluid(new FluidStack(fluid, outputFluidAmt));
			}
		}
	}

	/*

	// Nondirectional FluidHandler implementation, similar to IFluidHandler
	public int fill(int tank, FluidStack resource, boolean doFill) {
		if(!canFill(tank, resource.getFluid())) {
			return 0;
		}
		
		return tanks[tank].fill(resource, doFill);
	}

	public FluidStack drain(int tank, FluidStack resource, boolean doDrain) {
		if(canDrain(tank, resource.getFluid())) {
			return tanks[tank].drain(resource.amount, doDrain);
		}
		
		return null;
	}

	public FluidStack drain(int tank, int maxDrain, boolean doDrain) {
		if(tank < 0 || tank >= NUM_TANKS) { return null; }
		
		return tanks[tank].drain(maxDrain, doDrain);
	}

	public boolean canFill(int tank, Fluid fluid) {
		if(tank < 0 || tank >= NUM_TANKS) { return false; }
		
		FluidStack fluidStack = tanks[tank].getFluid();
		if(fluidStack != null) {
			return fluidStack.getFluid() == fluid;
		}
		else if(tank == TANK_INPUT) {
			// TODO: Input tank can only be filled with compatible fluids from a registry
			return fluid.getName().equals("steam");
		}
		else {
			// Output tank can be filled with anything. Don't be a dumb.
			return true;
		}
	}

	public boolean canDrain(int tank, Fluid fluid) {
		if(tank < 0 || tank >= NUM_TANKS) { return false; }
		FluidStack fluidStack = tanks[tank].getFluid();
		if(fluidStack == null) {
			return false;
		}
		
		return fluidStack.getFluid() == fluid;
	}

	public FluidTankInfo[] getTankInfo() {
		FluidTankInfo[] infos = new FluidTankInfo[NUM_TANKS];
		for(int i = 0; i < NUM_TANKS; i++) {
			infos[i] = tanks[i].getInfo();
		}

		return infos;
	}
	
	public FluidTankInfo getTankInfo(int tankIdx) {
		return tanks[tankIdx].getInfo();
	}


	*/

	/*
	// IEnergyProvider

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		int energyExtracted = Math.min((int)energyStored, maxExtract);
		
		if(!simulate) {
			energyStored -= energyExtracted;
		}
		
		return energyExtracted;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return (int)energyStored;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return (int)maxEnergyStored;
	}
	*/

	private void setEnergyStored(float newEnergy) {
		if(Float.isInfinite(newEnergy) || Float.isNaN(newEnergy)) { return; }

		energyStored = Math.max(0f, Math.min(maxEnergyStored, newEnergy));
	}

	/**
	 * Remove some energy from the internal storage buffer.
	 * Will not reduce the buffer below 0.
	 * @param energy Amount by which the buffer should be reduced.
	 */
	protected void reduceStoredEnergy(float energy) {
		addStoredEnergy(-1f * energy);
	}

	/**
	 * Add some energy to the internal storage buffer.
	 * Will not increase the buffer above the maximum or reduce it below 0.
	 * @param newEnergy
	 */
	protected void addStoredEnergy(float newEnergy) {
		if(Float.isNaN(newEnergy)) { return; }

		energyStored += newEnergy;
		if(energyStored > maxEnergyStored) {
			energyStored = maxEnergyStored;
		}
		if(-0.00001f < energyStored && energyStored < 0.00001f) {
			// Clamp to zero
			energyStored = 0f;
		}
	}

	public void setStoredEnergy(float oldEnergy) {
		energyStored = oldEnergy;
		if(energyStored < 0.0 || Float.isNaN(energyStored)) {
			energyStored = 0.0f;
		}
		else if(energyStored > maxEnergyStored) {
			energyStored = maxEnergyStored;
		}
	}
	
	/**
	 * Generate energy, internally. Will be multiplied by the BR Setting powerProductionMultiplier
	 * @param newEnergy Base, unmultiplied energy to generate
	 */
	protected void generateEnergy(float newEnergy) {
		newEnergy = newEnergy * BigReactors.CONFIG.powerProductionMultiplier * BigReactors.CONFIG.turbinePowerProductionMultiplier;
		energyGeneratedLastTick += newEnergy;
		addStoredEnergy(newEnergy);
	}
	
	// Activity state
	public boolean getActive() {
		return active;
	}

	public void setActive(boolean newValue) {

		if (newValue == active)
			return;

		//if(newValue != active) {
			this.active = newValue;
			for(IMultiblockPart part : connectedParts) {
				if(this.active) { part.onMachineActivated(); }
				else { part.onMachineDeactivated(); }
			}

			WorldHelper.notifyBlockUpdate(WORLD, this.getReferenceCoord(), null, null);
			markReferenceCoordDirty();
		//}

		if (WorldHelper.calledByLogicalClient(this.WORLD)) {

			// Force controllers to re-render on client

			for (IMultiblockPart part : this.attachedControllers)
				WorldHelper.notifyBlockUpdate(this.WORLD, part.getWorldPosition(), null, null);

			for (TileEntityTurbineRotorBlade part : this.attachedRotorBlades)
				WorldHelper.notifyBlockUpdate(this.WORLD, part.getWorldPosition(), null, null);

			for (TileEntityTurbineRotorShaft part : this.attachedRotorShafts)
				WorldHelper.notifyBlockUpdate(this.WORLD, part.getWorldPosition(), null, null);

			for (TileEntityTurbineRotorBearing part : this.attachedRotorBearings)
				part.resetRotorInfo();
		}
	}

	// Governor
	public int getMaxIntakeRate() { return maxIntakeRate; }

	public void setMaxIntakeRate(int newRate) {
		maxIntakeRate = Math.min(MAX_PERMITTED_FLOW, Math.max(0, newRate));
		markReferenceCoordDirty();
	}
	
	// for GUI use
	public int getMaxIntakeRateMax() { return MAX_PERMITTED_FLOW; }
	
	// ISlotlessUpdater
	@Override
	public void beginUpdatingPlayer(EntityPlayer playerToUpdate) {
		updatePlayers.add(playerToUpdate);
		sendIndividualUpdate(playerToUpdate);
	}
	
	@Override
	public void stopUpdatingPlayer(EntityPlayer playerToRemove) {
		updatePlayers.remove(playerToRemove);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	private CoilPartData getCoilPartData(IBlockState coilState) {

		Block block = coilState.getBlock();
		String oreName;

		// allow vanilla iron ...
		if (Blocks.IRON_BLOCK == block)
			oreName ="blockIron";

		// ... and gold blocks
		else if (Blocks.GOLD_BLOCK == block)
			oreName = "blockGold";

		// is it Ludicrite?
		else if (BlockBRMetal.isMetal(coilState, MetalType.Ludicrite))
			oreName = "blockLudicrite";

		// fall back to the Ore Dictionary
		else
			oreName = OreDictionaryHelper.getFirstOreName(coilState);

		return TurbineCoil.getBlockData(oreName);
	}

	/**
	 * Recalculate rotor and coil parameters
	 */
	private void recalculateDerivedStatistics() {

		BlockPos minInterior = this.getMinimumCoord().add(1, 1, 1);
		BlockPos maxInterior = this.getMaximumCoord().add(-1, -1, -1);
		
		rotorMass = 0;
		bladeSurfaceArea = 0;
		coilSize = 0;

		float coilEfficiency = 0f;
		float coilBonus = 0f;
		float coilDragCoefficient = 0f;

		// Loop over interior space. Calculate mass and blade area of rotor and size of coils

		int maxX = maxInterior.getX();
		int maxY = maxInterior.getY();
		int maxZ = maxInterior.getZ();

		for (int x = minInterior.getX(); x <= maxX; ++x) {
			for (int y = minInterior.getY(); y <= maxY; ++y) {
				for (int z = minInterior.getZ(); z <= maxZ; ++z) {

					IBlockState state = this.WORLD.getBlockState(new BlockPos(x, y, z));
					Block block = state.getBlock();

					if (block instanceof ITurbineRotorPart) {

						ITurbineRotorPart rotorPart = (ITurbineRotorPart)block;

						rotorMass += rotorPart.getMass(state);

						if (rotorPart.isBlade())
							bladeSurfaceArea += 1;
					}

					CoilPartData coilData = this.getCoilPartData(state);

					if (coilData != null) {

						coilEfficiency += coilData.efficiency;
						coilBonus += coilData.bonus;
						coilDragCoefficient += coilData.energyExtractionRate;
						coilSize += 1;
					}
				} // end z
			} // end y
		} // end x loop - looping over interior
		
		// Precalculate some stuff now that we know how big the rotor and blades are
		frictionalDrag = rotorMass * rotorDragCoefficient * BigReactors.CONFIG.turbineMassDragMultiplier;
		bladeDrag = baseBladeDragCoefficient * bladeSurfaceArea * BigReactors.CONFIG.turbineAeroDragMultiplier;

		if(coilSize <= 0)
		{
			// Uh. No coil? Fine.
			inductionEfficiency = 0f;
			inductionEnergyExponentBonus = 1f;
			inductorDragCoefficient = 0f;
		}
		else
		{
			inductionEfficiency = (coilEfficiency * 0.33f) / coilSize;
			inductionEnergyExponentBonus = Math.max(1f, (coilBonus / coilSize));
			inductorDragCoefficient = (coilDragCoefficient / coilSize) * inductorBaseDragCoefficient;
		}
	}
	
	public float getRotorSpeed() {
		if(attachedRotorBlades.size() <= 0 || rotorMass <= 0) { return 0f; }
		return rotorEnergy / (attachedRotorBlades.size() * rotorMass);
	}

	public float getEnergyGeneratedLastTick() { return energyGeneratedLastTick; }
	public int   getFluidConsumedLastTick() { return fluidConsumedLastTick; }
	public int	 getNumRotorBlades() { return attachedRotorBlades.size(); }
	public float getRotorEfficiencyLastTick() { return rotorEfficiencyLastTick; }

	public float getMaxRotorSpeed() {
		return 2000f;
	}
	
	public int getRotorMass() {
		return rotorMass;
	}
	
	public VentStatus getVentSetting() {
		return ventStatus;
	}
	
	public void setVentStatus(VentStatus newStatus, boolean markReferenceCoordDirty) {
		ventStatus = newStatus;
		if(markReferenceCoordDirty)
			markReferenceCoordDirty();
	}
	
	public boolean getInductorEngaged() {
		return inductorEngaged;
	}
	
	public void setInductorEngaged(boolean engaged, boolean markReferenceCoordDirty) {
		inductorEngaged = engaged;
		if(markReferenceCoordDirty)
			markReferenceCoordDirty();
	}

	private void setRotorEnergy(float newEnergy) {
		if(Float.isNaN(newEnergy) || Float.isInfinite(newEnergy)) { return; }
		rotorEnergy = Math.max(0f, newEnergy);
	}

	protected void markReferenceCoordDirty() {
		if(WORLD == null || WORLD.isRemote) { return; }

		BlockPos referenceCoord = getReferenceCoord();
		if(referenceCoord == null) { return; }

		rpmUpdateTracker.onExternalUpdate();

		TileEntity saveTe = WORLD.getTileEntity(referenceCoord);

		this.WORLD.markChunkDirty(referenceCoord, saveTe);
		WorldHelper.notifyBlockUpdate(WORLD, referenceCoord, null, null);
	}

	public boolean hasGlass() { return attachedGlass.size() > 0; }

	@SideOnly(Side.CLIENT)
	public void resetCachedRotors() {
		for(TileEntityTurbineRotorBearing bearing: attachedRotorBearings) {
			bearing.clearDisplayList();
		}
	}

	/*
	 * Power exchange API (replacement for IEnergyProvider)
 	*/
	@Override
	public long getEnergyCapacity() {
		return Math.min((long)maxEnergyStored, this._powerSystem.maxCapacity);
	}

	@Override
	public long getEnergyStored() {
		return (long)this.energyStored;
	}

	@Override
	public long extractEnergy(long maxEnergy, boolean simulate) {

		long removed = (long)Math.min(maxEnergy, this.energyStored);

		if (!simulate)
			this.reduceStoredEnergy(removed);

		return removed;
	}

	@Override
	public PowerSystem getPowerSystem() {
		return this._powerSystem;
	}

	public PartTier getMachineTier() {
		return this._partsTier;
	}

	protected void switchPowerSystem(PowerSystem newPowerSystem) {

		this._powerSystem = newPowerSystem;

		if (this.energyStored > this._powerSystem.maxCapacity)
			this.energyStored = this._powerSystem.maxCapacity;
	}

	/*
	 * IFluidHandler capability support
	 */

	public IFluidHandler getFluidHandler(IInputOutputPort.Direction direction) {
		return IInputOutputPort.Direction.Input == direction ? this._inputTank : this._outputTank;
	}

	// IDebuggable

	@Override
	public void getDebugMessages(IDebugMessages messages) {

		final boolean assembled = this.isAssembled();

		messages.add("debug.bigreactors.assembled", CodeHelper.i18nValue(assembled));
		messages.add("debug.bigreactors.attached", Integer.toString(this.connectedParts.size()));

		ValidationError lastError = this.getLastError();

		if (null != lastError)
			messages.add("debug.bigreactors.lastvalidationerror", lastError.getChatMessage());

		if (assembled) {

			messages.add("debug.bigreactors.active", CodeHelper.i18nValue(this.getActive()));
			messages.add("debug.bigreactors.storedenergy", this.getEnergyStored(), this.getPowerSystem().unitOfMeasure);

			messages.add("debug.bigreactors.turbine.rotorenergy", this.rotorEnergy);
			messages.add("debug.bigreactors.turbine.rotorspeed", this.getRotorSpeed());
			messages.add("debug.bigreactors.turbine.inductorengaged", CodeHelper.i18nValue(this.inductorEngaged));
			messages.add("debug.bigreactors.turbine.ventstatus", this.ventStatus.toString());
			messages.add("debug.bigreactors.turbine.maxintakerate", this.maxIntakeRate);
			messages.add("debug.bigreactors.turbine.coilsize", this.coilSize);
			messages.add("debug.bigreactors.turbine.rotormass", this.rotorMass);
			messages.add("debug.bigreactors.turbine.bladearea", this.bladeSurfaceArea);
			messages.add("debug.bigreactors.turbine.rotorblades", this.attachedRotorBlades.size());
			messages.add("debug.bigreactors.turbine.rotorshafts", this.attachedRotorShafts.size());
			messages.add("debug.bigreactors.turbine.rotordragcoeff", this.rotorDragCoefficient);
			messages.add("debug.bigreactors.turbine.bladedrag", this.bladeDrag);
			messages.add("debug.bigreactors.turbine.frictdrag", this.frictionalDrag);

			messages.add("debug.bigreactors.turbine.fluidtanksInfo");
			this.getTankDebugMessages(true, this._inputTank, messages);
			this.getTankDebugMessages(false, this._outputTank, messages);
		}
	}

	private void getTankDebugMessages(final boolean isInput, final FluidTank tank, final IDebugMessages messages) {

		FluidStack stack;

		if (null == tank || null == (stack = tank.getFluid()))
			messages.add(isInput ? "debug.bigreactors.turbine.inputempty" : "debug.bigreactors.turbine.outputempty");
		else
			messages.add(isInput ? "debug.bigreactors.turbine.input" : "debug.bigreactors.turbine.output",
					stack.getFluid().getName(), stack.amount);
	}
}