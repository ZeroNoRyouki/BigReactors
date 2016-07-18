package erogenousbeef.bigreactors.common.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import cofh.api.energy.IEnergyProvider;
//import cofh.lib.util.helpers.ItemHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.interfaces.IMultipleFluidHandler;
import erogenousbeef.bigreactors.common.multiblock.helpers.FloatUpdateTracker;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartGlass;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePowerTap;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorBearing;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorPart;
import erogenousbeef.bigreactors.gui.container.ISlotlessUpdater;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import erogenousbeef.bigreactors.net.message.multiblock.TurbineUpdateMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zero.mods.zerocore.api.multiblock.IMultiblockPart;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockControllerBase;
import zero.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import zero.mods.zerocore.api.multiblock.validation.ValidationError;
import zero.mods.zerocore.lib.block.ModTileEntity;
import zero.mods.zerocore.util.WorldHelper;

public class MultiblockTurbine extends RectangularMultiblockControllerBase implements IPowerGenerator, IEnergyProvider, IMultipleFluidHandler, ISlotlessUpdater, IActivateable {

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
	public static final int TANK_INPUT = 0;
	public static final int TANK_OUTPUT = 1;
	public static final int NUM_TANKS = 2;
	public static final int FLUID_NONE = -1;
	public static final int TANK_SIZE = 4000;
	public static final int MAX_PERMITTED_FLOW = 2000;

	private FluidTank[] tanks;
	
	static final float maxEnergyStored = 1000000f; // 1 MegaRF
	
	// Persistent game data
	float energyStored;
	private PowerSystem powerSystem;
	boolean active;
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
	public static int inputFluidPerBlade = 25; // mB
	public static float inductorBaseDragCoefficient = 0.1f; // RF/t extracted per coil block, multiplied by rotor speed squared.
	public static final float baseBladeDragCoefficient = 0.00025f; // RF/t base lost to aero drag per blade block. Includes a 50% reduction to factor in constant parts of the drag equation
	
	float energyGeneratedLastTick;
	int fluidConsumedLastTick;
	float rotorEfficiencyLastTick;
	
	private Set<IMultiblockPart> attachedControllers;
	private Set<TileEntityTurbineRotorBearing> attachedRotorBearings;
	
	private Set<TileEntityTurbinePowerTap> attachedPowerTaps;
	private Set<ITickableMultiblockPart> attachedTickables;
	
	private Set<TileEntityTurbineRotorPart> attachedRotorShafts;
	private Set<TileEntityTurbineRotorPart> attachedRotorBlades;
	
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
		
		tanks = new FluidTank[NUM_TANKS];
		for(int i = 0; i < NUM_TANKS; i++)
			tanks[i] = new FluidTank(TANK_SIZE);
		
		attachedControllers = new HashSet<IMultiblockPart>();
		attachedRotorBearings = new HashSet<TileEntityTurbineRotorBearing>();
		attachedPowerTaps = new HashSet<TileEntityTurbinePowerTap>();
		attachedTickables = new HashSet<ITickableMultiblockPart>();
		attachedRotorShafts = new HashSet<TileEntityTurbineRotorPart>();
		attachedRotorBlades = new HashSet<TileEntityTurbineRotorPart>();
		attachedGlass = new HashSet<TileEntityTurbinePartGlass>();
		
		energyStored = 0f;
		this.powerSystem = PowerSystem.RedstoneFlux;
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
	}

	/**
	 * Sends a full state update to a player.
	 */
	protected void sendIndividualUpdate(EntityPlayer player) {
		if(this.worldObj.isRemote) { return; }

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
		this.syncDataFromServer(data, ModTileEntity.SyncReason.FullSync);
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
		
		if(newPart instanceof TileEntityTurbineRotorPart) {
			TileEntityTurbineRotorPart turbinePart = (TileEntityTurbineRotorPart)newPart;
			if(turbinePart.isRotorShaft()) {
				attachedRotorShafts.add(turbinePart);
			}
			
			if(turbinePart.isRotorBlade()) {
				attachedRotorBlades.add(turbinePart);
			}
		}
		
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
			attachedPowerTaps.remove((TileEntityTurbinePowerTap)oldPart);
		}

		if(oldPart instanceof ITickableMultiblockPart) {
			attachedTickables.remove((ITickableMultiblockPart)oldPart);
		}
		
		if(oldPart instanceof TileEntityTurbineRotorPart) {
			TileEntityTurbineRotorPart turbinePart = (TileEntityTurbineRotorPart)oldPart;
			if(turbinePart.isRotorShaft()) {
				attachedRotorShafts.remove(turbinePart);
			}
			
			if(turbinePart.isRotorBlade()) {
				attachedRotorBlades.remove(turbinePart);
			}
		}
		
		if(oldPart instanceof TileEntityTurbinePartGlass) {
			attachedGlass.remove((TileEntityTurbinePartGlass)oldPart);
		}
	}

	@Override
	protected void onMachineAssembled() {
		recalculateDerivedStatistics();
	}

	@Override
	protected void onMachineRestored() {
		recalculateDerivedStatistics();
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
		TileEntityTurbinePartBase rotorPart = attachedRotorBearings.iterator().next();
		
		// Rotor bearing must calculate outwards dir, as this is normally only calculated in onMachineAssembled().
		rotorPart.recalculateOutwardsDirection(getMinimumCoord(), getMaximumCoord());

		// TODO Commented temporarily to allow this thing to compile...
		/*

		// Find out which way the rotor runs. Obv, this is inwards from the bearing.
		ForgeDirection rotorDir = rotorPart.getOutwardsDir().getOpposite();
		*/
		BlockPos rotorCoord = rotorPart.getPos();

		BlockPos minRotorCoord = getMinimumCoord();
		BlockPos maxRotorCoord = getMaximumCoord();
		/*
		// Constrain min/max rotor coords to where the rotor bearing is and the block opposite it
		if(rotorDir.offsetX == 0) {
			minRotorCoord.x = maxRotorCoord.x = rotorCoord.x;
		}
		if(rotorDir.offsetY == 0) {
			minRotorCoord.y = maxRotorCoord.y = rotorCoord.y;
		}
		if(rotorDir.offsetZ == 0) {
			minRotorCoord.z = maxRotorCoord.z = rotorCoord.z;
		}
		*/


		// Figure out where the rotor ends and which directions are normal to the rotor's 4 faces (this is where blades emit from)
		BlockPos endRotorCoord = rotorCoord.equals(minRotorCoord) ? maxRotorCoord : minRotorCoord;
		/*
		endRotorCoord.translate(rotorDir.getOpposite());

		EnumFacing[] bladeDirections;
		if(rotorDir.offsetY != 0) { 
			bladeDirections = StaticUtils.CardinalDirections;
		}
		else if(rotorDir.offsetX != 0) {
			bladeDirections = RotorXBladeDirections;
		}
		else {
			bladeDirections = RotorZBladeDirections;
		}

		*/

		Set<BlockPos> rotorShafts = new HashSet<BlockPos>(attachedRotorShafts.size());
		Set<BlockPos> rotorBlades = new HashSet<BlockPos>(attachedRotorBlades.size());
		
		for(TileEntityTurbineRotorPart part : attachedRotorShafts) {
			rotorShafts.add(part.getPos());
		}

		for(TileEntityTurbineRotorPart part : attachedRotorBlades) {
			rotorBlades.add(part.getPos());
		}

		// TODO Commented temporarily to allow this thing to compile...
		/*


		// Move along the length of the rotor, 1 block at a time
		boolean encounteredCoils = false;
		while(!rotorShafts.isEmpty() && !rotorCoord.equals(endRotorCoord)) {
			rotorCoord.translate(rotorDir);
			
			// Ensure we find a rotor block along the length of the entire rotor
			if(!rotorShafts.remove(rotorCoord)) {
				validatorCallback.setLastError("multiblock.validation.turbine.block_must_be_rotor", rotorCoord);
				return false;
			}
			
			// Now move out in the 4 rotor normals, looking for blades and coils
			BlockPos checkCoord = rotorCoord.copy(); // TODO is a copy really needed here?
			boolean encounteredBlades = false;
			for(ForgeDirection bladeDir : bladeDirections) {
				checkCoord.copy(rotorCoord);
				boolean foundABlade = false;
				checkCoord.translate(bladeDir);
				
				// If we find 1 blade, we can keep moving along the normal to find more blades
				while(rotorBlades.remove(checkCoord)) {
					// We found a coil already?! NOT ALLOWED.
					if(encounteredCoils) {
						validatorCallback.setLastError("multiblock.validation.turbine.blades_too_far", checkCoord);
						return false;
					}
					foundABlade = encounteredBlades = true;
					checkCoord.translate(bladeDir);
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
						BlockPos coilCheck = checkCoord.copy(); // TODO is a copy really needed here?
						coilCheck.translate(bladeDir.getRotation(rotorDir));
						foundCoils.remove(coilCheck);
						coilCheck.copy(checkCoord);
						coilCheck.translate(bladeDir.getRotation(rotorDir.getOpposite()));
						foundCoils.remove(coilCheck);
					}
					// Else: It must have been air.
				}
			}
		}

		*/
		
		if(!rotorCoord.equals(endRotorCoord)) {
			validatorCallback.setLastError("multiblock.validation.turbine.shaft_too_short");
			return false;
		}
		
		// Ensure that we encountered all the rotor, blade and coil blocks. If not, there's loose stuff inside the turbine.
		if(!rotorShafts.isEmpty()) {
			validatorCallback.setLastError("multiblock.validation.turbine.found_loose_rotor_blocks", rotorShafts.size());
			return false;
		}

		if(!rotorBlades.isEmpty()) {
			validatorCallback.setLastError("multiblock.validation.turbine.found_loose_rotor_blades", rotorBlades.size());
			return false;
		}
		
		if(!foundCoils.isEmpty()) {
			validatorCallback.setLastError("multiblock.validation.turbine.invalid_metals_shape", foundCoils.size());
			return false;
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

		// TODO Commented temporarily to allow this thing to compile...
		/*
		Block block = world.getBlock(x, y, z);
		int metadata = world.getBlockMetadata(x,y,z);

		// Coil windings below here:
		if(getCoilPartData(x, y, z, block, metadata) != null) {
			foundCoils.add(position);
			return true;
		}
		*/

		// Everything else, gtfo
		validatorCallback.setLastError("multiblock.validation.turbine.invalid_block_for_interior", x, y, z);
		return false;
	}


	@Override
	protected boolean isBlockGoodForFrame(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.worldObj.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected boolean isBlockGoodForTop(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.worldObj.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected boolean isBlockGoodForBottom(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.worldObj.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected boolean isBlockGoodForSides(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.worldObj.getBlockState(new BlockPos(x, y, z));
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
		return BigReactors.maximumTurbineSize;
	}

	@Override
	protected int getMaximumZSize() {
		return BigReactors.maximumTurbineSize;
	}

	@Override
	protected int getMaximumYSize() {
		return BigReactors.maximumTurbineHeight;
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
			BRLog.warning("[%s] Turbine @ %s is attempting to assimilate a non-Turbine machine! That machine's data will be lost!", worldObj.isRemote?"CLIENT":"SERVER", getReferenceCoord());
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
			steamIn = Math.min(maxIntakeRate, tanks[TANK_INPUT].getFluidAmount());
			
			if(ventStatus == VentStatus.DoNotVent) {
				// Cap steam used to available space, if not venting
				int availableSpace = tanks[TANK_OUTPUT].getCapacity() - tanks[TANK_OUTPUT].getFluidAmount();
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
				fluidConsumedLastTick = steamIn;
				drain(TANK_INPUT, steamIn, true);
				
				if(ventStatus != VentStatus.VentAll) {
					Fluid effluent = FluidRegistry.WATER;
					FluidStack effluentStack = new FluidStack(effluent, steamIn);
					fill(TANK_OUTPUT, effluentStack, true);
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

					energyRemaining = powerTap.onProvidePower(energyRemaining);
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
	protected void syncDataFromServer(NBTTagCompound data, ModTileEntity.SyncReason syncReason) {

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

			if(!worldObj.isRemote) {
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
	protected void syncDataToClient(NBTTagCompound data, ModTileEntity.SyncReason syncReason) {

		data.setTag("inputTank", tanks[TANK_INPUT].writeToNBT(new NBTTagCompound()));
		data.setTag("outputTank", tanks[TANK_OUTPUT].writeToNBT(new NBTTagCompound()));
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
			
			if(!worldObj.isRemote) {
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
		int inputFluidID, inputFluidAmt, outputFluidID, outputFluidAmt;
		{
			FluidStack inputFluid, outputFluid;
			inputFluid = tanks[TANK_INPUT].getFluid();
			outputFluid = tanks[TANK_OUTPUT].getFluid();
			
			if(inputFluid == null || inputFluid.amount <= 0) {
				inputFluidID = FLUID_NONE;
				inputFluidAmt = 0;
			}
			else {
				// TODO Commented temporarily to allow this thing to compile...
				inputFluidID = -1/*inputFluid.getFluid().getID()*/;
				inputFluidAmt = inputFluid.amount;
			}
			
			if(outputFluid == null || outputFluid.amount <= 0) {
				outputFluidID = FLUID_NONE;
				outputFluidAmt = 0;
			}
			else {
				// TODO Commented temporarily to allow this thing to compile...
				outputFluidID = -1/*outputFluid.getFluid().getID()*/;
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
		buf.writeInt(inputFluidID);
		buf.writeInt(inputFluidAmt);
		buf.writeInt(outputFluidID);
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
		int inputFluidID = buf.readInt();
		int inputFluidAmt = buf.readInt();
		int outputFluidID = buf.readInt();
		int outputFluidAmt = buf.readInt();

		if(inputFluidID == FLUID_NONE || inputFluidAmt <= 0) {
			tanks[TANK_INPUT].setFluid(null);
		}
		else {
			Fluid fluid = FluidRegistry.getFluid(inputFluidID);
			if(fluid == null) {
				BRLog.warning("[CLIENT] Multiblock Turbine received an unknown fluid of type %d, setting input tank to empty", inputFluidID);
				tanks[TANK_INPUT].setFluid(null);
			}
			else {
				tanks[TANK_INPUT].setFluid(new FluidStack(fluid, inputFluidAmt));
			}
		}

		if(outputFluidID == FLUID_NONE || outputFluidAmt <= 0) {
			tanks[TANK_OUTPUT].setFluid(null);
		}
		else {
			Fluid fluid = FluidRegistry.getFluid(outputFluidID);
			if(fluid == null) {
				BRLog.warning("[CLIENT] Multiblock Turbine received an unknown fluid of type %d, setting output tank to empty", outputFluidID);
				tanks[TANK_OUTPUT].setFluid(null);
			}
			else {
				tanks[TANK_OUTPUT].setFluid(new FluidStack(fluid, outputFluidAmt));
			}
		}
	}

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

	private void setEnergyStored(float newEnergy) {
		if(Float.isInfinite(newEnergy) || Float.isNaN(newEnergy)) { return; }

		energyStored = Math.max(0f, Math.min(maxEnergyStored, newEnergy));
	}
	
	// Energy Helpers
	/*public float getEnergyStored() {
		return energyStored;
	}*/
	
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
		newEnergy = newEnergy * BigReactors.powerProductionMultiplier * BigReactors.turbinePowerProductionMultiplier;
		energyGeneratedLastTick += newEnergy;
		addStoredEnergy(newEnergy);
	}
	
	// Activity state
	public boolean getActive() {
		return active;
	}

	public void setActive(boolean newValue) {
		if(newValue != active) {
			this.active = newValue;
			for(IMultiblockPart part : connectedParts) {
				if(this.active) { part.onMachineActivated(); }
				else { part.onMachineDeactivated(); }
			}

			WorldHelper.notifyBlockUpdate(worldObj, this.getReferenceCoord(), null, null);
			markReferenceCoordDirty();
		}
		
		if(worldObj.isRemote) {
			// Force controllers to re-render on client
			for(IMultiblockPart part : attachedControllers) {
				WorldHelper.notifyBlockUpdate(worldObj, part.getWorldPosition(), null, null);
			}
			
			for(TileEntityTurbineRotorPart part : attachedRotorBlades) {
				WorldHelper.notifyBlockUpdate(worldObj, part.getPos(), null, null);
			}
			
			for(TileEntityTurbineRotorPart part : attachedRotorShafts) {
				WorldHelper.notifyBlockUpdate(worldObj, part.getPos(), null, null);
			}
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

	// TODO Commented temporarily to allow this thing to compile...
	/*
	private CoilPartData getCoilPartData(int x, int y, int z, Block block, int metadata) {
		// Allow vanilla iron and gold blocks
		if(block == Blocks.iron_block) { return TurbineCoil.getBlockData("blockIron"); }
		if(block == Blocks.gold_block) { return TurbineCoil.getBlockData("blockGold"); }
		
		if(block == BrBlocks.blockMetals && metadata == BlockBRMetal.METADATA_LUDICRITE) { return TurbineCoil.getBlockData("blockLudicrite"); }
		
		// Check the oredict to see if it's copper, or a funky kind of gold/iron block
		// TODO Commented temporarily to allow this thing to compile...
		String oreName = null;//ItemHelper.oreProxy.getOreName(new ItemStack(block, 1, metadata));
		return TurbineCoil.getBlockData(oreName);
	}
	*/
	
	/**
	 * Recalculate rotor and coil parameters
	 */
	private void recalculateDerivedStatistics() {
		BlockPos minInterior, maxInterior;
		minInterior = getMinimumCoord().add(1, 1, 1);
		maxInterior = getMaximumCoord().add(-1, -1, -1);
		
		rotorMass = 0;
		bladeSurfaceArea = 0;
		coilSize = 0;
		float coilEfficiency = 0f;
		float coilBonus = 0f;
		float coilDragCoefficient = 0f;

		// TODO Commented temporarily to allow this thing to compile...
		/*
		// Loop over interior space. Calculate mass and blade area of rotor and size of coils
		for(int x = minInterior.x; x <= maxInterior.x; x++) {
			for(int y = minInterior.y; y <= maxInterior.y; y++) {
				for(int z = minInterior.z; z <= maxInterior.z; z++) {
					Block block = worldObj.getBlock(x, y, z);
					int metadata = worldObj.getBlockMetadata(x, y, z);
					CoilPartData coilData = null;

					if(block == BigReactors.blockTurbineRotorPart) {
						rotorMass += BigReactors.blockTurbineRotorPart.getRotorMass(block, metadata);
						if(BlockTurbineRotorPart.isRotorBlade(metadata)) {
							bladeSurfaceArea += 1;
						}
					}
					
					coilData = getCoilPartData(x, y, z, block, metadata);
					if(coilData != null) {
						coilEfficiency += coilData.efficiency;
						coilBonus += coilData.bonus;
						coilDragCoefficient += coilData.energyExtractionRate;
						coilSize += 1;
					}
				} // end z
			} // end y
		} // end x loop - looping over interior
		*/
		
		// Precalculate some stuff now that we know how big the rotor and blades are
		frictionalDrag = rotorMass * rotorDragCoefficient * BigReactors.turbineMassDragMultiplier;
		bladeDrag = baseBladeDragCoefficient * bladeSurfaceArea * BigReactors.turbineAeroDragMultiplier;

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
		if(worldObj == null || worldObj.isRemote) { return; }

		BlockPos referenceCoord = getReferenceCoord();
		if(referenceCoord == null) { return; }

		rpmUpdateTracker.onExternalUpdate();

		// TODO Commented temporarily to allow this thing to compile...
		/*
		TileEntity saveTe = worldObj.getTileEntity(referenceCoord.x, referenceCoord.y, referenceCoord.z);
		worldObj.markTileEntityChunkModified(referenceCoord.x, referenceCoord.y, referenceCoord.z, saveTe);
		*/
		WorldHelper.notifyBlockUpdate(worldObj, referenceCoord, null, null);
	}

	// TODO Commented temporarily to allow this thing to compile...
	/*
	// For client usage only
	public ForgeDirection getRotorDirection() {
		if(attachedRotorBearings.size() < 1) {
			return ForgeDirection.UNKNOWN;
		}
		
		if(!this.isAssembled()) {
			return ForgeDirection.UNKNOWN;
		}
		
		TileEntityTurbineRotorBearing rotorBearing = attachedRotorBearings.iterator().next();
		return rotorBearing.getOutwardsDir().getOpposite();
	}
	*/

	public boolean hasGlass() { return attachedGlass.size() > 0; }
	
	public String getDebugInfo() {
		StringBuilder sb = new StringBuilder();
		ValidationError lastError = this.getLastError();
		sb.append("Assembled: ").append(Boolean.toString(isAssembled())).append("\n");
		sb.append("Attached Blocks: ").append(Integer.toString(connectedParts.size())).append("\n");

		// TODO Commented temporarily to allow this thing to compile...
		/*
		if(lastError != null) {
			sb.append("Validation Exception:\n").append(getLastValidationException().getMessage()).append("\n");
		}
		*/
		
		if(isAssembled()) {
			sb.append("\nActive: ").append(Boolean.toString(getActive()));
			sb.append("\nStored Energy: ").append(Float.toString(getEnergyStored()));
			sb.append("\nRotor Energy: ").append(Float.toString(rotorEnergy));
			sb.append("\nRotor Speed: ").append(Float.toString(getRotorSpeed())).append(" rpm");
			sb.append("\nInductor Engaged: ").append(Boolean.toString(inductorEngaged));
			sb.append("\nVent Status: ").append(ventStatus.toString());
			sb.append("\nMax Intake Rate: ").append(Integer.toString(maxIntakeRate));
			sb.append("\nCoil Size: ").append(Integer.toString(coilSize));
			sb.append("\nRotor Mass: ").append(Integer.toString(rotorMass));
			sb.append("\nBlade SurfArea: ").append(Integer.toString(bladeSurfaceArea));
			sb.append("\n# Blades: ").append(Integer.toString(attachedRotorBlades.size()));
			sb.append("\n# Shafts: ").append(Integer.toString(attachedRotorShafts.size()));
			sb.append("\nRotor Drag CoEff: ").append(Float.toString(rotorDragCoefficient));
			sb.append("\nBlade Drag: ").append(Float.toString(bladeDrag));
			sb.append("\nFrict Drag: ").append(Float.toString(frictionalDrag));
			sb.append("\n\nFluid Tanks:\n");
			for(int i = 0; i < tanks.length; i++) {
				sb.append(String.format("[%d] %s ", i, i == TANK_OUTPUT ? "outlet":"inlet"));
				if(tanks[i] == null || tanks[i].getFluid() == null) {
					sb.append("empty");
				}
				else {
					FluidStack stack = tanks[i].getFluid();
					sb.append(String.format("%s, %d mB", stack.getFluid().getName(), stack.amount));
				}
				sb.append("\n");
			}
		}

		return sb.toString();
	}

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
		return this.powerSystem.maxCapacity;
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
		return this.powerSystem;
	}
}
