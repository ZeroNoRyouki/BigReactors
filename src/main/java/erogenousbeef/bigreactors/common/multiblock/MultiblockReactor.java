package erogenousbeef.bigreactors.common.multiblock;

import erogenousbeef.bigreactors.api.IHeatEntity;
import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.api.registry.ReactorInterior;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.data.RadiationData;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import erogenousbeef.bigreactors.common.interfaces.IReactorFuelInfo;
import erogenousbeef.bigreactors.common.multiblock.helpers.CoolantContainer;
import erogenousbeef.bigreactors.common.multiblock.helpers.FuelAssembly;
import erogenousbeef.bigreactors.common.multiblock.helpers.FuelContainer;
import erogenousbeef.bigreactors.common.multiblock.helpers.RadiationHelper;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.common.multiblock.tileentity.*;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import erogenousbeef.bigreactors.net.message.multiblock.ReactorUpdateMessage;
import erogenousbeef.bigreactors.net.message.multiblock.ReactorUpdateWasteEjectionMessage;
import erogenousbeef.bigreactors.utils.StaticUtils;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.block.ModTileEntity;
import it.zerono.mods.zerocore.util.CodeHelper;
import it.zerono.mods.zerocore.util.ItemHelper;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MultiblockReactor extends RectangularMultiblockControllerBase implements IPowerGenerator, IReactorFuelInfo,
		IActivateable, IDebuggable {

	public static final int FuelCapacityPerFuelRod = 4 * Reactants.standardSolidReactantAmount; // 4 ingots per rod

	private static final float passiveCoolingPowerEfficiency = 0.5f; // 50% power penalty, so this comes out as about 1/3 a basic water-cooled reactor
	private static final float passiveCoolingTransferEfficiency = 0.2f; // 20% of available heat transferred per tick when passively cooled
	private static final float reactorHeatLossConductivity = 0.001f; // circa 1RF per tick per external surface block
	
	// Game stuff - stored
	protected boolean active;
	private float reactorHeat;
	private float fuelHeat;
	private WasteEjectionSetting wasteEjection;

	private PowerSystem _powerSystem;
	private PartTier _partsTier;
	private float energyStored;


	protected FuelContainer fuelContainer;
	protected RadiationHelper radiationHelper;
	protected CoolantContainer coolantContainer;

	// Game stuff - derived at runtime
	protected float fuelToReactorHeatTransferCoefficient;
	protected float reactorToCoolantSystemHeatTransferCoefficient;
	protected float reactorHeatLossCoefficient;
	
	protected Iterator<TileEntityReactorFuelRod> currentFuelRod;
	int reactorVolume;

	// UI stuff
	private float energyGeneratedLastTick;
	private float fuelConsumedLastTick;
	
	public enum WasteEjectionSetting {
		kAutomatic,					// Full auto, always remove waste
		kManual, 					// Manual, only on button press
	}
	public static final WasteEjectionSetting[] s_EjectionSettings = WasteEjectionSetting.values();
	
	// Lists of connected parts
	private Set<TileEntityReactorPowerTap> attachedPowerTaps;
	private Set<ITickableMultiblockPart> attachedTickables;

	private Set<TileEntityReactorControlRod> attachedControlRods;
	private Set<TileEntityReactorAccessPort> attachedAccessPorts;
	private Set<TileEntityReactorController> attachedControllers;
	
	private Set<TileEntityReactorFuelRod> attachedFuelRods;
	private Set<TileEntityReactorCoolantPort> attachedCoolantPorts;
	
	private Set<TileEntityReactorGlass> attachedGlass;

	private FuelAssembly[] _fuelAssemblies;

	private boolean _legacyMode;

	// Updates
	private Set<EntityPlayer> updatePlayers;
	private int ticksSinceLastUpdate;
	private static final int ticksBetweenUpdates = 3;

	public MultiblockReactor(World world) {
		super(world);

		// Game stuff
		active = false;
		reactorHeat = 0f;
		fuelHeat = 0f;
		_powerSystem = PowerSystem.RedstoneFlux;
		energyStored = 0f;
		wasteEjection = WasteEjectionSetting.kAutomatic;
		this._partsTier = PartTier.Legacy;

		// Derived stats
		fuelToReactorHeatTransferCoefficient = 0f;
		reactorToCoolantSystemHeatTransferCoefficient = 0f;
		reactorHeatLossCoefficient = 0f;
		
		// UI and stats
		energyGeneratedLastTick = 0f;
		fuelConsumedLastTick = 0f;
		
		
		attachedPowerTaps = new HashSet<TileEntityReactorPowerTap>();
		attachedTickables = new HashSet<ITickableMultiblockPart>();
		attachedControlRods = new HashSet<TileEntityReactorControlRod>();
		attachedAccessPorts = new HashSet<TileEntityReactorAccessPort>();
		attachedControllers = new HashSet<TileEntityReactorController>();
		attachedFuelRods = new HashSet<TileEntityReactorFuelRod>();
		attachedCoolantPorts = new HashSet<TileEntityReactorCoolantPort>();
		attachedGlass = new HashSet<TileEntityReactorGlass>();
		this._fuelAssemblies = null;
		
		currentFuelRod = null;

		updatePlayers = new HashSet<EntityPlayer>();
		
		ticksSinceLastUpdate = 0;
		fuelContainer = new FuelContainer();
		radiationHelper = new RadiationHelper();
		coolantContainer = new CoolantContainer();
		
		reactorVolume = 0;

		this._coolantHandlers = new IFluidHandler[2];
		this._legacyMode = false;
	}
	
	public void beginUpdatingPlayer(EntityPlayer playerToUpdate) {
		updatePlayers.add(playerToUpdate);
		sendIndividualUpdate(playerToUpdate);
	}
	
	public void stopUpdatingPlayer(EntityPlayer playerToRemove) {
		updatePlayers.remove(playerToRemove);
	}
	
	@Override
	protected void onBlockAdded(IMultiblockPart part) {
		if(part instanceof TileEntityReactorAccessPort) {
			attachedAccessPorts.add((TileEntityReactorAccessPort)part);
		}
		
		if(part instanceof TileEntityReactorControlRod) {
			TileEntityReactorControlRod controlRod = (TileEntityReactorControlRod)part; 
			attachedControlRods.add(controlRod);
		}

		if(part instanceof TileEntityReactorPowerTap) {
			attachedPowerTaps.add((TileEntityReactorPowerTap)part);
		}

		if(part instanceof TileEntityReactorController) {
			attachedControllers.add((TileEntityReactorController)part);
		}

		if(part instanceof ITickableMultiblockPart) {
			attachedTickables.add((ITickableMultiblockPart)part);
		}
		
		if(part instanceof TileEntityReactorFuelRod) {
			TileEntityReactorFuelRod fuelRod = (TileEntityReactorFuelRod)part;
			attachedFuelRods.add(fuelRod);

			// Reset iterator
			currentFuelRod = attachedFuelRods.iterator();

			if(WORLD.isRemote) {
				WorldHelper.notifyBlockUpdate(WORLD, fuelRod.getPos(), null, null);
			}
		}
		
		if(part instanceof TileEntityReactorCoolantPort) {
			attachedCoolantPorts.add((TileEntityReactorCoolantPort) part);
		}
		
		if(part instanceof TileEntityReactorGlass) {
			attachedGlass.add((TileEntityReactorGlass)part);
		}
	}
	
	@Override
	protected void onBlockRemoved(IMultiblockPart part) {
		if(part instanceof TileEntityReactorAccessPort) {
			attachedAccessPorts.remove((TileEntityReactorAccessPort)part);
		}

		if(part instanceof TileEntityReactorControlRod) {
			attachedControlRods.remove((TileEntityReactorControlRod)part);
		}

		if(part instanceof TileEntityReactorPowerTap) {
			attachedPowerTaps.remove((TileEntityReactorPowerTap)part);
		}

		if(part instanceof TileEntityReactorController) {
			attachedControllers.remove(part);
		}

		if(part instanceof ITickableMultiblockPart) {
			attachedTickables.remove((ITickableMultiblockPart)part);
		}
		
		if(part instanceof TileEntityReactorFuelRod) {
			attachedFuelRods.remove(part);
			currentFuelRod = attachedFuelRods.iterator();
		}
		
		if(part instanceof TileEntityReactorCoolantPort) {
			attachedCoolantPorts.remove((TileEntityReactorCoolantPort)part);
		}
		
		if(part instanceof TileEntityReactorGlass) {
			attachedGlass.remove((TileEntityReactorGlass)part);
		}
	}

	@Override
	protected boolean isMachineWhole(IMultiblockValidator validatorCallback) {

		// Ensure that there is at least one controller and control rod attached.

		if (this.attachedControlRods.size() < 1) {

			validatorCallback.setLastError("multiblock.validation.reactor.too_few_rods");
			return false;
		}
		
		if (this.attachedControllers.size() < 1) {

			validatorCallback.setLastError("multiblock.validation.reactor.too_few_controllers");
			return false;
		}

		// ensure that control rods are placed only on one side of the reactor and that fuel rods follow the
		// orientation of control rods

		Set<TileEntity> validFuelRods = new HashSet<TileEntity>();
		EnumFacing rodsFacing = null;

		for (TileEntityReactorControlRod rod : this.attachedControlRods) {

			EnumFacing facing = rod.getOutwardFacingFromWorldPosition();

			if (null == rodsFacing)
				rodsFacing = facing;

			if (null == facing || (facing != rodsFacing)) {

				validatorCallback.setLastError("multiblock.validation.reactor.invalid_control_side");
				return false;
			}

			// - capture all the fuel rods behind this control rod

			BlockPos position = rod.getWorldPosition();
			TileEntity te;

			facing = facing.getOpposite();

			while (true) {

				position = position.offset(facing);
				te = this.WORLD.getTileEntity(position);

				if (te instanceof TileEntityReactorFuelRod)
					// found a valid fuel rod
					validFuelRods.add(te);

				else if (te instanceof TileEntityReactorPartBase) {

					// we hit some other reactor parts on the walls

					IBlockState state = this.WORLD.getBlockState(position);

					if (BrBlocks.reactorCasing != state.getBlock()) {

						// reactor casing is the only valid base for a fuel assembly

						validatorCallback.setLastError("multiblock.validation.reactor.invalid_base_for_fuel_assembly",
								position.getX(), position.getY(), position.getZ());
						return false;
					}

					break;

				} else {
					// found an invalid tile entity (or no tile entity at all)
					validatorCallback.setLastError("multiblock.validation.reactor.invalid_block_in_fuel_assembly",
							position.getX(), position.getY(), position.getZ());
					return false;
				}
			}
		}

		if (validFuelRods.size() != this.attachedFuelRods.size()) {

			validatorCallback.setLastError("multiblock.validation.reactor.invalid_fuel_rods");
			return false;
		}

		// check if the machine is single-tier

		PartTier candidateTier = null;

		for (IMultiblockPart part: this.connectedParts) {

			if (part instanceof TileEntityReactorPartBase) {

				PartTier tier = ((TileEntityReactorPartBase)part).getPartTier();

				if (null == candidateTier)
					candidateTier = tier;

				else if (candidateTier != tier) {

					validatorCallback.setLastError("multiblock.validation.reactor.mixed_tiers");
					return false;
				}
			}
		}

		// check if the machine has a single power system

		if (this.attachedPowerTaps.size() > 0) {

			int rf = 0, tesla = 0;

			for (TileEntityReactorPowerTap tap : this.attachedPowerTaps) {

				if (tap instanceof TileEntityReactorPowerTapRedstoneFlux)
					++rf;
				else if (tap instanceof TileEntityReactorPowerTapTesla)
					++tesla;
			}

			if (rf != 0 && tesla != 0) {

				validatorCallback.setLastError("multiblock.validation.reactor.mixed_power_systems");
				return false;
			}
		}
		
		return super.isMachineWhole(validatorCallback);
	}

	@Override
	public void updateClient() {
    }

	// Update loop. Only called when the machine is assembled.
	@Override
	public boolean updateServer() {
		if(Float.isNaN(this.getReactorHeat())) {
			this.setReactorHeat(0.0f);
		}
		
		float oldHeat = this.getReactorHeat();
		float oldEnergy = this.getEnergyStored();
		energyGeneratedLastTick = 0f;
		fuelConsumedLastTick = 0f;

		float newHeat = 0f;
		
		if(getActive()) {
			// Select a control rod to radiate from. Reset the iterator and select a new Y-level if needed.
			if(!currentFuelRod.hasNext()) {
				currentFuelRod = attachedFuelRods.iterator();
			}

			// Radiate from that control rod
			TileEntityReactorFuelRod source  = currentFuelRod.next();
			FuelAssembly fuelAssembly = source.getFuelAssembly();
			TileEntityReactorControlRod sourceControlRod = (null != fuelAssembly) ? fuelAssembly.getControlRod() : null;

			if(sourceControlRod != null)
			{
				RadiationData radData = radiationHelper.radiate(WORLD, fuelContainer, source, fuelAssembly, getFuelHeat(), getReactorHeat(), attachedControlRods.size());

				// Assimilate results of radiation
				if(radData != null) {
					addFuelHeat(radData.getFuelHeatChange(attachedFuelRods.size()));
					addReactorHeat(radData.getEnvironmentHeatChange(getReactorVolume()));
					fuelConsumedLastTick += radData.fuelUsage;
				}
			}
		}

		// Allow radiation to decay even when reactor is off.
		radiationHelper.tick(getActive());

		// If we can, poop out waste and inject new fuel.
		if(wasteEjection == WasteEjectionSetting.kAutomatic) {
			ejectWaste(false, null);
		}
		
		refuel();

		// Heat Transfer: Fuel Pool <> Reactor Environment
		float tempDiff = fuelHeat - reactorHeat;
		if(tempDiff > 0.01f) {
			float rfTransferred = tempDiff * fuelToReactorHeatTransferCoefficient;
			float fuelRf = StaticUtils.Energy.getRFFromVolumeAndTemp(attachedFuelRods.size(), fuelHeat);
			
			fuelRf -= rfTransferred;
			setFuelHeat(StaticUtils.Energy.getTempFromVolumeAndRF(attachedFuelRods.size(), fuelRf));

			// Now see how much the reactor's temp has increased
			float reactorRf = StaticUtils.Energy.getRFFromVolumeAndTemp(getReactorVolume(), getReactorHeat());
			reactorRf += rfTransferred;
			setReactorHeat(StaticUtils.Energy.getTempFromVolumeAndRF(getReactorVolume(), reactorRf));
		}

		// If we have a temperature differential between environment and coolant system, move heat between them.
		tempDiff = getReactorHeat() - getCoolantTemperature();
		if(tempDiff > 0.01f) {
			float rfTransferred = tempDiff * reactorToCoolantSystemHeatTransferCoefficient;
			float reactorRf = StaticUtils.Energy.getRFFromVolumeAndTemp(getReactorVolume(), getReactorHeat());

			if(isPassivelyCooled()) {
				rfTransferred *= passiveCoolingTransferEfficiency;
				generateEnergy(rfTransferred * passiveCoolingPowerEfficiency);
			}
			else {
				rfTransferred -= coolantContainer.onAbsorbHeat(rfTransferred);
				energyGeneratedLastTick = coolantContainer.getFluidVaporizedLastTick(); // Piggyback so we don't have useless stuff in the update packet
			}

			reactorRf -= rfTransferred;
			setReactorHeat(StaticUtils.Energy.getTempFromVolumeAndRF(getReactorVolume(), reactorRf));
		}

		// Do passive heat loss - this is always versus external environment
		tempDiff = getReactorHeat() - getPassiveCoolantTemperature();
		if(tempDiff > 0.000001f) {
			float rfLost = Math.max(1f, tempDiff * reactorHeatLossCoefficient); // Lose at least 1RF/t
			float reactorNewRf = Math.max(0f, StaticUtils.Energy.getRFFromVolumeAndTemp(getReactorVolume(), getReactorHeat()) - rfLost);
			setReactorHeat(StaticUtils.Energy.getTempFromVolumeAndRF(getReactorVolume(), reactorNewRf));
		}
		
		// Prevent cryogenics
		if(reactorHeat < 0f) { setReactorHeat(0f); }
		if(fuelHeat < 0f) { setFuelHeat(0f); }
		
		// Distribute available power
		long energyAvailable = getEnergyStored();
		long energyRemaining = energyAvailable;
		if(attachedPowerTaps.size() > 0 && energyRemaining > 0) {
			// First, try to distribute fairly
			long splitEnergy = energyRemaining / attachedPowerTaps.size();
			for(TileEntityReactorPowerTap powerTap : attachedPowerTaps) {
				if(energyRemaining <= 0) { break; }
				if(!powerTap.isConnected()) { continue; }

				energyRemaining -= splitEnergy - powerTap.onProvidePower(splitEnergy);
			}

			// Next, just hose out whatever we can, if we have any left
			if(energyRemaining > 0) {
				for(TileEntityReactorPowerTap powerTap : attachedPowerTaps) {
					if(energyRemaining <= 0) { break; }
					if(!powerTap.isConnected()) { continue; }

					energyRemaining = powerTap.onProvidePower(energyRemaining);
				}
			}
		}
		
		if(energyAvailable != energyRemaining) {
			reduceStoredEnergy((energyAvailable - energyRemaining));
		}

		// Send updates periodically
		ticksSinceLastUpdate++;
		if(ticksSinceLastUpdate >= ticksBetweenUpdates) {
			ticksSinceLastUpdate = 0;
            this.updateFuelAssembliesQuota();
			sendTickUpdate();
		}
		
		// TODO: Overload/overheat

		// Update any connected tickables
		for(ITickableMultiblockPart tickable : attachedTickables) {
			tickable.onMultiblockServerTick();
		}

		if(attachedGlass.size() > 0 && fuelContainer.shouldUpdate()) {
			markReferenceCoordForUpdate();
		}
		
		return (oldHeat != this.getReactorHeat() || oldEnergy != this.getEnergyStored());
	}
	
	public void setEnergyStored(float oldEnergy) {
		energyStored = oldEnergy;
		if(energyStored < 0.0 || Float.isNaN(energyStored)) {
			energyStored = 0.0f;
		}
		else if(energyStored > this._powerSystem.maxCapacity) {
			energyStored = this._powerSystem.maxCapacity;
		}
	}
	
	/**
	 * Generate energy, internally. Will be multiplied by the BR Setting powerProductionMultiplier
	 * @param newEnergy Base, unmultiplied energy to generate
	 */
	protected void generateEnergy(float newEnergy) {
		newEnergy = newEnergy * BigReactors.CONFIG.powerProductionMultiplier * BigReactors.CONFIG.reactorPowerProductionMultiplier;
		this.energyGeneratedLastTick += newEnergy;
		this.addStoredEnergy(newEnergy);
	}

	/**
	 * Add some energy to the internal storage buffer.
	 * Will not increase the buffer above the maximum or reduce it below 0.
	 * @param newEnergy
	 */
	protected void addStoredEnergy(float newEnergy) {

		if (Float.isNaN(newEnergy))
			return;

		this.energyStored += newEnergy;

		if (this.energyStored > this._powerSystem.maxCapacity) {
			this.energyStored = this._powerSystem.maxCapacity;
		}
		if (-0.00001f < this.energyStored && this.energyStored < 0.00001f) {
			// Clamp to zero
			this.energyStored = 0f;
		}
	}

	/**
	 * Remove some energy from the internal storage buffer.
	 * Will not reduce the buffer below 0.
	 * @param energy Amount by which the buffer should be reduced.
	 */
	protected void reduceStoredEnergy(float energy) {
		this.addStoredEnergy(-energy);
	}

	public void setActive(boolean act) {
		if(act == this.active) { return; }
		this.active = act;
		
		for(IMultiblockPart part : connectedParts) {
			if(this.active) { part.onMachineActivated(); }
			else { part.onMachineDeactivated(); }
		}
		
		if(WORLD.isRemote) {
			// Force controllers to re-render on client
			for(IMultiblockPart part : attachedControllers) {
				WorldHelper.notifyBlockUpdate(WORLD, part.getWorldPosition(), null, null);
			}
		}
		else {
			this.markReferenceCoordForUpdate();
		}
	}

	protected void addReactorHeat(float newCasingHeat) {
		if(Float.isNaN(newCasingHeat)) {
			return;
		}

		reactorHeat += newCasingHeat;
		// Clamp to zero to prevent floating point issues
		if(-0.00001f < reactorHeat && reactorHeat < 0.00001f) { reactorHeat = 0.0f; }
	}
	
	public float getReactorHeat() {
		return reactorHeat;
	}
	
	public void setReactorHeat(float newHeat) {
		if(Float.isNaN(newHeat)) {
			reactorHeat = 0.0f;
		}
		else {
			reactorHeat = newHeat;
		}
	}

	protected void addFuelHeat(float additionalHeat) {
		if(Float.isNaN(additionalHeat)) { return; }
		
		fuelHeat += additionalHeat;
		if(-0.00001f < fuelHeat & fuelHeat < 0.00001f) { fuelHeat = 0f; }
	}
	
	public float getFuelHeat() { return fuelHeat; }
	
	public void setFuelHeat(float newFuelHeat) {
		if(Float.isNaN(newFuelHeat)) { fuelHeat = 0f; }
		else { fuelHeat = newFuelHeat; }
	}
	
	public int getFuelRodCount() {
		return attachedControlRods.size();
	}

	// Static validation helpers
	// Water, air, and metal blocks
	@Override
	protected boolean isBlockGoodForInterior(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		BlockPos position = new BlockPos(x, y, z);

		if(world.isAirBlock(position)) { return true; } // Air is OK

		IBlockState blockState = this.WORLD.getBlockState(position);
		Block block = blockState.getBlock();

		Material material = block.getMaterial(blockState);
		if(material == MaterialLiquid.WATER ||
				block == Blocks.IRON_BLOCK || block == Blocks.GOLD_BLOCK ||
				block == Blocks.DIAMOND_BLOCK || block == Blocks.EMERALD_BLOCK) {
			return true;
		}
		
		// Permit registered moderator blocks

		ItemStack stack = ItemHelper.createItemStack(blockState, 1);

		if (null == stack) {

			BRLog.error("Got null ItemStack for blockstate %s", blockState);
			validatorCallback.setLastError("multiblock.validation.reactor.null_block_found", x, y, z);
			return false;
		}

		if(ReactorInterior.getBlockData(stack) != null) {
			return true;
		}

		// Permit TE fluids
		if(block != null) {
			if(block instanceof IFluidBlock) {
				Fluid fluid = ((IFluidBlock)block).getFluid();
				String fluidName = fluid.getName();
				if(ReactorInterior.getFluidData(fluidName) != null) { return true; }

				validatorCallback.setLastError("multiblock.validation.reactor.invalid_fluid_for_interior", x, y, z, fluidName);
				return false;
			}
			else {

				validatorCallback.setLastError("multiblock.validation.reactor.invalid_block_for_interior", x, y, z, block.getLocalizedName());
				return false;
			}
		}
		else {

			validatorCallback.setLastError("multiblock.validation.reactor.null_block_found", x, y, z);
			return false;
		}
	}

	@Override
	protected boolean isBlockGoodForFrame(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.WORLD.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected boolean isBlockGoodForTop(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.WORLD.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected boolean isBlockGoodForBottom(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.WORLD.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected boolean isBlockGoodForSides(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

		IBlockState blockState = this.WORLD.getBlockState(new BlockPos(x, y, z));
		Block block = blockState.getBlock();

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_block_for_exterior", x, y, z, block.getLocalizedName());
		return false;
	}

	@Override
	protected void syncDataFrom(NBTTagCompound data, ModTileEntity.SyncReason syncReason) {

		if (data.hasKey("reactorActive"))
			this.setActive(data.getBoolean("reactorActive"));

		if (data.hasKey("heat"))
			this.setReactorHeat(Math.max(getReactorHeat(), data.getFloat("heat")));

		if (data.hasKey("storedEnergy"))
			this.setEnergyStored(Math.max(getEnergyStored(), data.getFloat("storedEnergy")));

		if (data.hasKey("wasteEjection2"))
			this.wasteEjection = s_EjectionSettings[data.getInteger("wasteEjection2")];

		if (data.hasKey("fuelHeat"))
			this.setFuelHeat(data.getFloat("fuelHeat"));

		if (data.hasKey("fuelContainer"))
			this.fuelContainer.readFromNBT(data.getCompoundTag("fuelContainer"));

		if (data.hasKey("radiation"))
			this.radiationHelper.readFromNBT(data.getCompoundTag("radiation"));

		if (data.hasKey("coolantContainer"))
			this.coolantContainer.readFromNBT(data.getCompoundTag("coolantContainer"));

		if (ModTileEntity.SyncReason.NetworkUpdate == syncReason)
			this.onFuelStatusChanged();
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, ModTileEntity.SyncReason syncReason) {

		data.setBoolean("reactorActive", this.active);
		data.setFloat("heat", this.reactorHeat);
		data.setFloat("fuelHeat", fuelHeat);
		data.setFloat("storedEnergy", this.energyStored);
		data.setInteger("wasteEjection2", this.wasteEjection.ordinal());
		data.setTag("fuelContainer", fuelContainer.writeToNBT(new NBTTagCompound()));
		data.setTag("radiation", radiationHelper.writeToNBT(new NBTTagCompound()));
		data.setTag("coolantContainer", coolantContainer.writeToNBT(new NBTTagCompound()));
	}

	@Override
	protected int getMinimumNumberOfBlocksForAssembledMachine() {
		// Hollow cube.
		return 26;
	}

	// Network & Storage methods
	/*
	 * Serialize a reactor into a given Byte buffer
	 * @param buf The byte buffer to serialize into
	 */
	public void serialize(ByteBuf buf) {

		Fluid coolantType = this.coolantContainer.getCoolantType();
		Fluid vaporType = this.coolantContainer.getVaporType();
		String coolantName = null != coolantType ? coolantType.getName() : "";
		String vaporName = null != vaporType ? vaporType.getName() : "";

		// Basic data
		buf.writeBoolean(active);
		buf.writeFloat(reactorHeat);
		buf.writeFloat(fuelHeat);
		buf.writeFloat(energyStored);
		buf.writeFloat(radiationHelper.getFertility());
		
		// Statistics
		buf.writeFloat(energyGeneratedLastTick);
		buf.writeFloat(fuelConsumedLastTick);
		
		// Coolant data
		ByteBufUtils.writeUTF8String(buf, coolantName);
		buf.writeInt(coolantContainer.getCoolantAmount());
		ByteBufUtils.writeUTF8String(buf, vaporName);
		buf.writeInt(coolantContainer.getVaporAmount());
		
		fuelContainer.serialize(buf);
	}

	/*
	 * Deserialize a reactor's data from a given Byte buffer
	 * @param buf The byte buffer containing reactor data
	 */
	public void deserialize(ByteBuf buf) {
		// Basic data
		setActive(buf.readBoolean());
		setReactorHeat(buf.readFloat());
		setFuelHeat(buf.readFloat());
		setEnergyStored(buf.readFloat());
		radiationHelper.setFertility(buf.readFloat());
		
		// Statistics
		setEnergyGeneratedLastTick(buf.readFloat());
		setFuelConsumedLastTick(buf.readFloat());
		

		// Coolant data
		String coolantName = ByteBufUtils.readUTF8String(buf);
		int coolantAmt = buf.readInt();
		String vaporName = ByteBufUtils.readUTF8String(buf);
		int vaporAmt = buf.readInt();

		// Fuel & waste data
		fuelContainer.deserialize(buf);
		this.updateFuelAssembliesQuota();
		this.updateFuelAssembliesReactants();


		if (coolantName.isEmpty())
			coolantContainer.emptyCoolant();
		else
			coolantContainer.setCoolant(new FluidStack(FluidRegistry.getFluid(coolantName), coolantAmt));

		if (vaporName.isEmpty())
			coolantContainer.emptyVapor();
		else
			coolantContainer.setVapor(new FluidStack(FluidRegistry.getFluid(vaporName), vaporAmt));
	}
	
	protected IMessage getUpdatePacket() {
        return new ReactorUpdateMessage(this);
	}
	
	/**
	 * Sends a full state update to a player.
	 */
	protected void sendIndividualUpdate(EntityPlayer player) {
		if(this.WORLD.isRemote) { return; }

        CommonPacketHandler.INSTANCE.sendTo(getUpdatePacket(), (EntityPlayerMP)player);
	}
	
	/**
	 * Send an update to any clients with GUIs open
	 */
	protected void sendTickUpdate() {

		if (this.WORLD.isRemote || this.updatePlayers.size() <= 0)
			return;

		for(EntityPlayer player : this.updatePlayers) {
            CommonPacketHandler.INSTANCE.sendTo(getUpdatePacket(), (EntityPlayerMP)player);
		}
	}
	
	/**
	 * Attempt to distribute a stack of ingots to a given access port, sensitive to the amount and type of ingots already in it.
	 * @param port The port to which we're distributing ingots.
	 * @param itemsToDistribute The stack of ingots to distribute. Will be modified during the operation and may be returned with stack size 0.
	 * @param distributeToInputs Should we try to send ingots to input ports?
	 * @return The number of waste items distributed, i.e. the differential in stack size for wasteToDistribute.
	 */
	private int tryDistributeItems(TileEntityReactorAccessPort port, ItemStack itemsToDistribute, boolean distributeToInputs) {

        int initialWasteAmount = itemsToDistribute.stackSize;

        // Dump waste preferentially to outlets, unless we only have one access port
        if (!port.getDirection().isInput() || (distributeToInputs || this.attachedAccessPorts.size() < 2)) {

            itemsToDistribute = ItemHandlerHelper.insertItem(port.getItemStackHandler(false), itemsToDistribute, false);
            port.onItemsReceived();
        }

        return null != itemsToDistribute ? initialWasteAmount - itemsToDistribute.stackSize : initialWasteAmount;

        /*
		ItemStack existingStack = port.getStackInSlot(TileEntityReactorAccessPort.SLOT_OUTLET);
		int initialWasteAmount = itemsToDistribute.stackSize;
		if(!port.isInlet() || (distributeToInputs || attachedAccessPorts.size() < 2)) {
			// Dump waste preferentially to outlets, unless we only have one access port
			if(existingStack == null) {
				if(itemsToDistribute.stackSize > port.getInventoryStackLimit()) {
					ItemStack newStack = itemsToDistribute.splitStack(port.getInventoryStackLimit());
					port.setInventorySlotContents(TileEntityReactorAccessPort.SLOT_OUTLET, newStack);
				}
				else {
					port.setInventorySlotContents(TileEntityReactorAccessPort.SLOT_OUTLET, itemsToDistribute.copy());
					itemsToDistribute.stackSize = 0;
				}
			}
			else if(existingStack.isItemEqual(itemsToDistribute)) {
				if(existingStack.stackSize + itemsToDistribute.stackSize <= existingStack.getMaxStackSize()) {
					existingStack.stackSize += itemsToDistribute.stackSize;
					itemsToDistribute.stackSize = 0;
				}
				else {
					int amt = existingStack.getMaxStackSize() - existingStack.stackSize;
					itemsToDistribute.stackSize -= existingStack.getMaxStackSize() - existingStack.stackSize;
					existingStack.stackSize += amt;
				}
			}

			port.onItemsReceived();
		}
		
		return initialWasteAmount - itemsToDistribute.stackSize;
		*/
	}

	@Override
	protected void onAssimilated(MultiblockControllerBase otherMachine) {
		this.attachedPowerTaps.clear();
		this.attachedTickables.clear();
		this.attachedAccessPorts.clear();
		this.attachedControllers.clear();
		this.attachedControlRods.clear();
		currentFuelRod = null;
		this._fuelAssemblies = null;
	}
	
	@Override
	protected void onAssimilate(MultiblockControllerBase otherMachine) {
		if(!(otherMachine instanceof MultiblockReactor)) {
			BRLog.warning("[%s] Reactor @ %s is attempting to assimilate a non-Reactor machine! That machine's data will be lost!", WORLD.isRemote?"CLIENT":"SERVER", getReferenceCoord());
			return;
		}
		
		MultiblockReactor otherReactor = (MultiblockReactor)otherMachine;

		if(otherReactor.reactorHeat > this.reactorHeat) { setReactorHeat(otherReactor.reactorHeat); }
		if(otherReactor.fuelHeat > this.fuelHeat) { setFuelHeat(otherReactor.fuelHeat); }

		if(otherReactor.getEnergyStored() > this.getEnergyStored()) { this.setEnergyStored(otherReactor.getEnergyStored()); }

		fuelContainer.merge(otherReactor.fuelContainer);
		radiationHelper.merge(otherReactor.radiationHelper);
		coolantContainer.merge(otherReactor.coolantContainer);

		//this.rebuildFuelAssemblies();
	}
	
	@Override
	public void onAttachedPartWithMultiblockData(IMultiblockPart part, NBTTagCompound data) {
		this.syncDataFrom(data, ModTileEntity.SyncReason.FullSync);
	}
	
	/*public float getEnergyStored() {
		return energyStored;
	}*/

	/**
	 * Directly set the waste ejection setting. Will dispatch network updates
	 * from server to interested clients.
	 * @param newSetting The new waste ejection setting.
	 */
	public void setWasteEjection(WasteEjectionSetting newSetting) {
		if(this.wasteEjection != newSetting) {
			this.wasteEjection = newSetting;
			
			if(!this.WORLD.isRemote) {
				markReferenceCoordDirty();

				if(this.updatePlayers.size() > 0) {
					for(EntityPlayer player : updatePlayers) {
                        CommonPacketHandler.INSTANCE.sendTo(new ReactorUpdateWasteEjectionMessage(this), (EntityPlayerMP)player);
					}
				}
			}
		}
	}
	
	public WasteEjectionSetting getWasteEjection() {
		return this.wasteEjection;
	}

	protected void refuel() {
		// For now, we only need to check fuel ports when we have more space than can accomodate 1 ingot
		if(fuelContainer.getRemainingSpace() < Reactants.standardSolidReactantAmount) {
			return;
		}

		final boolean wasEmpty = this.fuelContainer.getFuelAmount() <= 0;
		int amtAdded = 0;
		
		// Loop: Consume input reactants from all ports
		for(TileEntityReactorAccessPort port : attachedAccessPorts)
		{
			if(fuelContainer.getRemainingSpace() <= 0) { break; }

			if(!port.isConnected())	{ continue; }

			// See what type of reactant the port contains; if none, skip it.
			String portReactantType = port.getInputReactantType();
			int portReactantAmount = port.getInputReactantAmount();
			if(portReactantType == null || portReactantAmount <= 0) { continue; }
			
			if(!Reactants.isFuel(portReactantType)) { continue; } // Skip nonfuels

			// HACK; TEMPORARY
			// Alias blutonium to yellorium temporarily, until mixed fuels are implemented
			if(portReactantType.equals(StandardReactants.blutonium)) {
				portReactantType = StandardReactants.yellorium;
			}
			
			// How much fuel can we actually add from this type of reactant?
			int amountToAdd = fuelContainer.addFuel(portReactantType, portReactantAmount, false);
			if(amountToAdd <= 0) { continue; }
			
			int portCanAdd = port.consumeReactantItem(amountToAdd);
			if(portCanAdd <= 0) { continue; }
			
			amtAdded = fuelContainer.addFuel(portReactantType, portReactantAmount, true);
		}
		
		if(amtAdded > 0) {

			if (wasEmpty)
				this.updateFuelAssembliesReactants();

			markReferenceCoordForUpdate();
			markReferenceCoordDirty();
		}
	}
	
	/**
	 * Attempt to eject waste contained in the reactor
	 * @param dumpAll If true, any waste remaining after ejection will be discarded.
	 * @param destination If set, waste will only be ejected to ports with coordinates matching this one.
	 */
	public void ejectWaste(boolean dumpAll, BlockPos destination)
	{
		// For now, we can optimize by only running this when we have enough waste to product an ingot
		int amtEjected = 0;

		String wasteReactantType = fuelContainer.getWasteType();
		if(wasteReactantType == null) { 
			return;
		}

		int minimumReactantAmount = Reactants.getMinimumReactantToProduceSolid(wasteReactantType);
		if(fuelContainer.getWasteAmount() >= minimumReactantAmount) {

			for(TileEntityReactorAccessPort port : attachedAccessPorts) {
				if(fuelContainer.getWasteAmount() < minimumReactantAmount) {
					continue;
				}
				
				if(!port.isConnected()) { continue; }
				if(destination != null && !destination.equals(port.getPos())) {
					continue;
				}
				
				// First time through, we eject only to outlet ports
				if(destination == null && !port.getDirection().isInput()) {
					int reactantEjected = port.emitReactant(wasteReactantType, fuelContainer.getWasteAmount());
					fuelContainer.dumpWaste(reactantEjected);
					amtEjected += reactantEjected;
				}
			}
			
			if(destination == null && fuelContainer.getWasteAmount() > minimumReactantAmount) {
				// Loop a second time when destination is null and we still have waste
				for(TileEntityReactorAccessPort port : attachedAccessPorts) {
					if(fuelContainer.getWasteAmount() < minimumReactantAmount) {
						continue;
					}
					
					if(!port.isConnected()) { continue; }
					int reactantEjected = port.emitReactant(wasteReactantType, fuelContainer.getWasteAmount());
					fuelContainer.dumpWaste(reactantEjected);
					amtEjected += reactantEjected;
				}
			}
		}

		if(dumpAll)
		{
			amtEjected += fuelContainer.getWasteAmount();
			fuelContainer.setWaste(null);
		}

		if(amtEjected > 0) {
			markReferenceCoordForUpdate();
			markReferenceCoordDirty();
		}
	}
	
	/**
	 * Eject fuel contained in the reactor.
	 * @param dumpAll If true, any remaining fuel will simply be lost.
	 * @param destination If not null, then fuel will only be distributed to a port matching these coordinates.
	 */
	public void ejectFuel(boolean dumpAll, BlockPos destination) {
		// For now, we can optimize by only running this when we have enough waste to product an ingot
		int amtEjected = 0;

		String fuelReactantType = fuelContainer.getFuelType();
		if(fuelReactantType == null) { 
			return;
		}

		int minimumReactantAmount = Reactants.getMinimumReactantToProduceSolid(fuelReactantType);
		if(fuelContainer.getFuelAmount() >= minimumReactantAmount) {

			for(TileEntityReactorAccessPort port : attachedAccessPorts) {
				if(fuelContainer.getFuelAmount() < minimumReactantAmount) {
					continue;
				}
				
				if(!port.isConnected()) { continue; }
				if(destination != null && !destination.equals(port.getPos())) {
					continue;
				}
				
				int reactantEjected = port.emitReactant(fuelReactantType, fuelContainer.getFuelAmount());
				fuelContainer.dumpFuel(reactantEjected);
				amtEjected += reactantEjected;
			}
		}

		if(dumpAll)
		{
			amtEjected += fuelContainer.getFuelAmount();
			fuelContainer.setFuel(null);
		}

		if(amtEjected > 0) {
			markReferenceCoordForUpdate();
			markReferenceCoordDirty();
		}
	}

	@Override
	protected void onMachineAssembled() {

		this.rebuildFuelAssemblies();
		this.recalculateDerivedValues();

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

			for (TileEntityReactorPowerTap tap : this.attachedPowerTaps) {

				if (tap instanceof TileEntityReactorPowerTapRedstoneFlux)
					++rf;
				else if (tap instanceof TileEntityReactorPowerTapTesla)
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
		this.active = false;
	}

	private void recalculateDerivedValues() {
		// Recalculate size of fuel/waste tank via fuel rods
		BlockPos minCoord, maxCoord;
		minCoord = getMinimumCoord();
		maxCoord = getMaximumCoord();
		
		fuelContainer.setCapacity(attachedFuelRods.size() * FuelCapacityPerFuelRod);

		// Calculate derived stats
		
		// Calculate heat transfer based on fuel rod environment
		fuelToReactorHeatTransferCoefficient = 0f;
		for(TileEntityReactorFuelRod fuelRod : attachedFuelRods) {
			fuelToReactorHeatTransferCoefficient += fuelRod.getHeatTransferRate();
		}

		// Calculate heat transfer to coolant system based on reactor interior surface area.
		// This is pretty simple to start with - surface area of the rectangular prism defining the interior.
		int xSize = maxCoord.getX() - minCoord.getX() - 1;
		int ySize = maxCoord.getY() - minCoord.getY() - 1;
		int zSize = maxCoord.getZ() - minCoord.getZ() - 1;
		
		int surfaceArea = 2 * (xSize * ySize + xSize * zSize + ySize * zSize);
		
		reactorToCoolantSystemHeatTransferCoefficient = IHeatEntity.conductivityIron * surfaceArea;

		// Calculate passive heat loss.
		// Get external surface area
		xSize += 2;
		ySize += 2;
		zSize += 2;
		
		surfaceArea = 2 * (xSize * ySize + xSize * zSize + ySize * zSize);
		reactorHeatLossCoefficient = reactorHeatLossConductivity * surfaceArea;
		
		if(WORLD.isRemote) {
			// Make sure our fuel rods re-render
			this.onFuelStatusChanged();
		}
		else {
			// Force an update of the client's multiblock information
			markReferenceCoordForUpdate();
		}
		
		calculateReactorVolume();
		
		if(attachedCoolantPorts.size() > 0) {
			int outerVolume = StaticUtils.ExtraMath.Volume(minCoord, maxCoord) - reactorVolume;
			coolantContainer.setCapacity(Math.max(0, Math.min(50000, outerVolume * 100)));
		}
		else {
			coolantContainer.setCapacity(0);
		}
	}

	@Override
	protected int getMaximumXSize() {
		return BigReactors.CONFIG.maxReactorSize;
	}

	@Override
	protected int getMaximumZSize() {
		return BigReactors.CONFIG.maxReactorSize;
	}

	@Override
	protected int getMaximumYSize() {
		return BigReactors.CONFIG.maxReactorHeight;
	}

	/**
	 * Used to update the UI
	 */
	public void setEnergyGeneratedLastTick(float energyGeneratedLastTick) {
		this.energyGeneratedLastTick = energyGeneratedLastTick;
	}

	/**
	 * UI Helper
	 */
	public float getEnergyGeneratedLastTick() {
		return this.energyGeneratedLastTick;
	}
	
	/**
	 * Used to update the UI
	 */
	public void setFuelConsumedLastTick(float fuelConsumed) {
		fuelConsumedLastTick = fuelConsumed;
	}
	
	/**
	 * UI Helper
	 */
	public float getFuelConsumedLastTick() {
		return fuelConsumedLastTick;
	}

	/**
	 * UI Helper
	 * @return Percentile fuel richness (fuel/fuel+waste), or 0 if all control rods are empty
	 */
	public float getFuelRichness() {
		int amtFuel, amtWaste;
		amtFuel = fuelContainer.getFuelAmount();
		amtWaste = fuelContainer.getWasteAmount();

		if(amtFuel + amtWaste <= 0f) { return 0f; }
		else { return (float)amtFuel / (float)(amtFuel+amtWaste); }
	}

	// Redstone helper
	public void setAllControlRodInsertionValues(int newValue) {
		if(this.assemblyState != AssemblyState.Assembled) { return; }
		
		for(TileEntityReactorControlRod cr : attachedControlRods) {
			if(cr.isConnected()) {
				cr.setControlRodInsertion((short)newValue);
			}
		}
	}
	
	public void changeAllControlRodInsertionValues(short delta) {
		if(this.assemblyState != AssemblyState.Assembled) { return; }
		
		for(TileEntityReactorControlRod cr : attachedControlRods) {
			if(cr.isConnected()) {
				cr.setControlRodInsertion( (short) (cr.getControlRodInsertion() + delta) );
			}
		}
	}

	public BlockPos[] getControlRodLocations() {
		BlockPos[] coords = new BlockPos[this.attachedControlRods.size()];
		int i = 0;
		for(TileEntityReactorControlRod cr : attachedControlRods) {
			coords[i++] = cr.getPos();
		}
		return coords;
	}

	public int getFuelAmount() {
		return fuelContainer.getFuelAmount();
	}

	public int getWasteAmount() {
		return fuelContainer.getWasteAmount();
	}
	
	public String getFuelType() {
		return fuelContainer.getFuelType();
	}
	
	public String getWasteType() {
		return fuelContainer.getWasteType();
	}

	public int getEnergyStoredPercentage() {
		return (int)(this.energyStored / (float)this._powerSystem.maxCapacity * 100f);
	}

	@Override
	public int getCapacity() {
		if(WORLD.isRemote && assemblyState != AssemblyState.Assembled) {
			// Estimate capacity
			return attachedFuelRods.size() * FuelCapacityPerFuelRod;
		}

		return fuelContainer.getCapacity();
	}
	
	public float getFuelFertility() {
		return radiationHelper.getFertilityModifier();
	}
	
	// Coolant subsystem
	public CoolantContainer getCoolantContainer() {
		return coolantContainer;
	}
	
	protected float getPassiveCoolantTemperature() {
		return IHeatEntity.ambientHeat;
	}

	protected float getCoolantTemperature() {
		if(isPassivelyCooled()) {
			return getPassiveCoolantTemperature();
		}
		else {
			return coolantContainer.getCoolantTemperature(getReactorHeat());
		}
	}
	
	public boolean isPassivelyCooled() {
		if(coolantContainer == null || coolantContainer.getCapacity() <= 0) { return true; }
		else { return false; }
	}
	
	protected int getReactorVolume() {
		return reactorVolume;
	}
	
	protected void calculateReactorVolume() {
		BlockPos minInteriorCoord = getMinimumCoord().add(1, 1, 1);
		BlockPos maxInteriorCoord = getMaximumCoord().add(-1, -1, -1);
		
		reactorVolume = StaticUtils.ExtraMath.Volume(minInteriorCoord, maxInteriorCoord);
	}

	// Client-only
	protected void onFuelStatusChanged() {
		if(WORLD.isRemote) {

            this.updateFuelAssembliesQuota();
			this.updateFuelAssembliesReactants();

			// On the client, re-render all the fuel rod blocks when the fuel status changes
			for(TileEntityReactorFuelRod fuelRod : attachedFuelRods) {
				WorldHelper.notifyBlockUpdate(this.WORLD, fuelRod.getPos(), null, null);
			}
		}
	}

	protected void markReferenceCoordForUpdate() {
		BlockPos rc = getReferenceCoord();
		if(WORLD != null && rc != null) {
			WorldHelper.notifyBlockUpdate(WORLD, rc, null, null);
		}
	}
	
	protected void markReferenceCoordDirty() {
		if(WORLD == null || WORLD.isRemote) { return; }

		BlockPos referenceCoord = getReferenceCoord();
		if(referenceCoord == null) { return; }

		TileEntity saveTe = WORLD.getTileEntity(referenceCoord);
		this.WORLD.markChunkDirty(referenceCoord, saveTe);
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	public PartTier getMachineTier() {
		return this._partsTier;
	}

	protected void switchPowerSystem(PowerSystem newPowerSystem) {

		this._powerSystem = newPowerSystem;

		if (this.energyStored > this._powerSystem.maxCapacity)
			this.energyStored = this._powerSystem.maxCapacity;
	}

	public int getFuelAssembliesCount() {
		return null != this._fuelAssemblies ? this._fuelAssemblies.length : 0;
	}

	private void rebuildFuelAssemblies() {

		int count = this.attachedControlRods.size();
		int idx = 0;

		this._fuelAssemblies = new FuelAssembly[count];

		for (TileEntityReactorControlRod controlRod : this.attachedControlRods)
			this._fuelAssemblies[idx++] = new FuelAssembly(controlRod);
	}

    private void updateFuelAssembliesQuota() {

		if (null == this._fuelAssemblies)
			return;

		int count = this._fuelAssemblies.length;
		int fuelRodsTotalCount = this.attachedFuelRods.size();

		for (int i = 0; i < count; ++i)
			this._fuelAssemblies[i].updateQuota(this.fuelContainer, fuelRodsTotalCount);
	}

	private void updateFuelAssembliesReactants() {

		if (null == this._fuelAssemblies)
			return;

		int count = this._fuelAssemblies.length;

		for (int i = 0; i < count; ++i)
			this._fuelAssemblies[i].updateReactants(this.fuelContainer);
	}

	/*
	 * Power exchange API (replacement for IEnergyProvider)
 	*/
	@Override
	public long getEnergyCapacity() {
		return this._powerSystem.maxCapacity;
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

	/*
	 * IFluidHandler capability support
	 */
	public IFluidHandler getFluidHandler(IInputOutputPort.Direction direction) {

		final int idx = IInputOutputPort.Direction.Input == direction ? CoolantContainer.COLD : CoolantContainer.HOT;
		IFluidHandler handler = this._coolantHandlers[idx];

		if (null == handler)
			this._coolantHandlers[idx] = handler = new CoolantFluidHandlerWrapper(this, direction);

		return handler;
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
			messages.add("debug.bigreactors.reactor.casingheat", this.getReactorHeat());
			messages.add("debug.bigreactors.reactor.fuelheat", this.getFuelHeat());

			messages.add("debug.bigreactors.reactor.reactanttanksIntro");
			this.fuelContainer.getDebugMessages(messages);

			final boolean passiveCooling = this.isPassivelyCooled();

			messages.add("debug.bigreactors.reactor.activelycooled", CodeHelper.i18nValue(!passiveCooling));

			if (!passiveCooling) {

				messages.add("debug.bigreactors.reactor.coolanttanksIntro");
				this.coolantContainer.getDebugMessages(messages);
			}
		}
	}

	private IFluidHandler[] _coolantHandlers;

	private class CoolantFluidHandlerWrapper implements IFluidHandler {

		public CoolantFluidHandlerWrapper(MultiblockReactor reactor, IInputOutputPort.Direction direction) {

			this._reactor = reactor;
			this._tankId = IInputOutputPort.Direction.Input == direction ? CoolantContainer.COLD : CoolantContainer.HOT;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return this._reactor.getCoolantContainer().getTankProperties(this._tankId);
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			return this._reactor.getCoolantContainer().fill(this._tankId, resource, doFill);
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			return this._reactor.getCoolantContainer().drain(this._tankId, resource, doDrain);
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			return this._reactor.getCoolantContainer().drain(this._tankId, maxDrain, doDrain);
		}

		private final MultiblockReactor _reactor;
		private final int _tankId;
	}
}