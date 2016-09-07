package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.client.gui.GuiReactorRedstonePort;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.CircuitType;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.gui.container.ContainerBasic;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityReactorRedstonePort extends TileEntityReactorPartBase implements ITickableMultiblockPart {

	protected CircuitType circuitType;
	protected int outputLevel;
	protected boolean activeOnPulse;
	protected boolean greaterThan; // if false, less than
	
	protected int ticksSinceLastUpdate;
	
	// These are local-only and used for handy state calculations
	protected boolean isExternallyPowered;
	
	public TileEntityReactorRedstonePort() {

		super();
		this.circuitType = circuitType.DISABLED;
		this.isExternallyPowered = false;
		this.ticksSinceLastUpdate = 0;
		this._isLit = false;
	}

	@Override
	public boolean canOpenGui(World world, BlockPos posistion, IBlockState state) {
		return true;
	}

	// Redstone methods
	protected boolean isRedstoneActive() {
		if(!this.isConnected()) { return false; }

		MultiblockReactor reactor = this.getReactorController();

		switch(circuitType) {

			case outputFuelTemperature:
				return checkVariable((int)reactor.getFuelHeat());

			case outputCasingTemperature:
				return checkVariable((int)reactor.getReactorHeat());

			case outputFuelMix:
				return checkVariable((int)(reactor.getFuelRichness()*100));

			case outputFuelAmount:
				return checkVariable(reactor.getFuelAmount());

			case outputWasteAmount:
				return checkVariable(reactor.getWasteAmount());

			case outputEnergyAmount:
				return checkVariable(reactor.getEnergyStoredPercentage());

			case DISABLED:
				return false;

			default:
				return this.isExternallyPowered;
		}
	}
	
	public boolean isInput() {
		return this.circuitType.isInput();
	}
	
	public boolean isOutput() {
		return this.circuitType.isOutput();
	}

	protected boolean checkVariable(int value) {
		if(this.greaterThan) {
			return value > getOutputLevel();
		}
		else {
			return value < getOutputLevel();
		}
	}

	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {

		if (!this.isConnected())
			return;

		if (this.isInput()) {

			EnumFacing out = this.getOutwardFacing();
			boolean nowPowered = (null != out) && isReceivingRedstonePowerFrom(worldObj, this.getWorldPosition().offset(out), out);

			if (this.isExternallyPowered != nowPowered) {

				this.isExternallyPowered = nowPowered;
				this.onRedstoneInputUpdated();
				this.markDirty();
				this.updateRedstoneStateAndNotify();
			}
		}
		else {
			this.isExternallyPowered = false;
		}
	}

	// Called to do business logic when the redstone value has changed
	protected void onRedstoneInputUpdated() {
		if(!this.isConnected()) { return; }

		MultiblockReactor reactor = this.getReactorController();
		switch(this.circuitType) {
		case inputActive:
			if(this.isInputActiveOnPulse()) {
				if(this.isExternallyPowered) {
					reactor.setActive(!reactor.getActive());
				}
			}
			else {
				reactor.setActive(this.isExternallyPowered);
			}
			break;
		case inputSetControlRod:
			// On/off only
			if(this.isInputActiveOnPulse()) {
				if(this.isExternallyPowered) {
					if(this.shouldSetControlRodsInsteadOfChange()) {
						reactor.setAllControlRodInsertionValues(this.outputLevel);
					}
					else {
						reactor.changeAllControlRodInsertionValues((short)this.outputLevel); // Can be negative, don't want to mask.
					}
				}
			}
			else {
				if(this.isExternallyPowered) {
					reactor.setAllControlRodInsertionValues(getControlRodLevelWhileOn());
				}
				else {
					reactor.setAllControlRodInsertionValues(getControlRodLevelWhileOff());
				}
			}
			break;
		case inputEjectWaste:
			// Pulse only
			if(this.isExternallyPowered) {
				reactor.ejectWaste(false, null);
			}
			break;
		default:
			break;
		}
	}
	
	public int getOutputLevel() { return outputLevel; }
	public int getControlRodLevelWhileOff() { return ((outputLevel & 0xFF00) >> 8) & 0xFF; }
	public int getControlRodLevelWhileOn () { return outputLevel & 0xFF; }
	
	public static int packControlRodLevels(byte off, byte on) {
		return (((int)off << 8) & 0xFF00) | (on & 0xFF);
	}

	public static int unpackControlRodLevelOn(int level) {
		return level & 0xFF;
	}
	
	public static int unpackControlRodLevelOff(int level) {
		return ((level & 0xFF00) >> 8) & 0xFF;
	}

	public boolean isInputActiveOnPulse() {
		return this.activeOnPulse;
	}

	/** Handle settings update made from GUI - always called on the server thread
	 * @param newType The type of the new circuit.
	 * @param outputLevel For input/control rods, the level(s) to change or set. For outputs, the numerical value
	 * @param greaterThan For outputs, whether to activate when greater than or less than the outputLevel value. For input/control rods, whether to set (true) or change (false) the values.
	 */
	public void onReceiveUpdatePacket(int newType, int outputLevel, boolean greaterThan, boolean activeOnPulse) {

		BlockPos position = this.getWorldPosition();

		this.circuitType = CircuitType.values()[newType];
		this.outputLevel = outputLevel;
		this.greaterThan = greaterThan;
		this.activeOnPulse = activeOnPulse;
		this.updateLitState();

		if(isAlwaysActiveOnPulse(circuitType)) { this.activeOnPulse = true; }
		else if (this.circuitType.isOutput()) { this.activeOnPulse = false; }
		
		// Do updates
		if(this.isInput()) {
			// Update inputs so we don't pulse/change automatically
			EnumFacing out = this.getOutwardFacing();
			this.isExternallyPowered = (null != out) && this.isReceivingRedstonePowerFrom(worldObj, position.offset(out), out);
			if(!this.isInputActiveOnPulse()) {
				onRedstoneInputUpdated();
			}
		}
		else {
			this.isExternallyPowered = false;
		}

		this.nofityTileEntityUpdate();

		EnumFacing outward = this.getOutwardFacing();

		if (null != outward)
			this.worldObj.notifyBlockOfStateChange(this.getWorldPosition().offset(outward), BrBlocks.reactorRedstonePort);
	}
	
	@SideOnly(Side.CLIENT)
	public boolean getGreaterThan() { return this.greaterThan; }

	public CircuitType getCircuitType() { return this.circuitType; }

	private boolean shouldSetControlRodsInsteadOfChange() { return !greaterThan; }

	// TODO Removing support for ComputerCraft and MineFactory Reloaded until they are updated to 1.9.x
	/*
	public void onRedNetUpdate(int powerLevel) {
		if(this.isInput()) {
			boolean wasPowered = this.isExternallyPowered;
			this.isExternallyPowered = powerLevel > 0;
			if(wasPowered != this.isExternallyPowered) {
				this.onRedstoneInputUpdated();
				this.updateRedstoneStateAndNotify();
			}
		}
	}
	*/
	
	/**
	 * Call with the coordinates of the block to check and the direction
	 * towards that block from your block.
	 * If the block towards which this block is emitting power lies north,
	 * then pass in south.
	 */
	private boolean isReceivingRedstonePowerFrom(World world, BlockPos position, EnumFacing facing) {

		return world.isBlockIndirectlyGettingPowered(position) > 0 ||
				world.getRedstonePower(position, facing) > 0;
		/*
		// This is because of bugs in vanilla redstone wires
		Block block = world.getBlock(x, y, z);
		return isReceivingRedstonePowerFrom(world, x, y, z, facing, block);
		*/
	}

	/**
	 * Call with the coordinates of the block to check and the direction
	 * towards that block from your block.
	 * If the block towards which this block is emitting power lies north,
	 * then pass in south.
	 */
	/*
	private boolean isReceivingRedstonePowerFrom(World world, BlockPos position, EnumFacing facing, Block neighborBlock) {

		if (neighborBlock == Blocks.redstone_wire) {
			// Use metadata because of vanilla redstone wire bugs
			return world.getBlockMetadata(x, y, z) > 0;
		}
		else {
			return world.getIndirectPowerOutput(x, y, z, dir.ordinal()) || world.isBlockProvidingPowerTo(x, y, z, dir.ordinal()) > 0;
		}
	}
	*/
	
	// TileEntity overrides

	// Only refresh if we're switching functionality
	// Warning: dragonz!
	/*
	@Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		/ *
		if(oldID != newID) {
			return true;
		}
		* /
	
		// All redstone ports are the same, we just use metadata to easily signal changes.
		return false;
    }*/

	// IReactorTickable
	/**
	 * Updates the redstone block's status, if it's an output network, if there is one.
	 * Will only send one update per N ticks, where N is a configurable setting.
	 */
	public void onMultiblockServerTick() {

		if (!this.isConnected() || (this.ticksSinceLastUpdate++ < BigReactors.CONFIG.ticksPerRedstoneUpdate))
			return;

		this.updateRedstoneStateAndNotify();
		this.ticksSinceLastUpdate = 0;
	}

	/*
	// MultiblockTileEntityBase methods
	private void readData(NBTTagCompound data) {

		if(data.hasKey("circuitType")) {
			this.circuitType = circuitType.values()[data.getInteger("circuitType")];
		}

		if(data.hasKey("outputLevel")) {
			this.outputLevel = data.getInteger("outputLevel");
		}
		
		if(data.hasKey("greaterThan")) {
			this.greaterThan = data.getBoolean("greaterThan");
		}
		
		if(data.hasKey("activeOnPulse")) {
			this.activeOnPulse = data.getBoolean("activeOnPulse");
		}
	}
	
	private void writeData(NBTTagCompound data) {

		data.setInteger("circuitType", this.circuitType.ordinal());
		data.setInteger("outputLevel", this.outputLevel);
		data.setBoolean("greaterThan", this.greaterThan);
		data.setBoolean("activeOnPulse", this.activeOnPulse);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.readData(data);
		this.updateRedstoneStateAndNotify();
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		this.writeData(data);
	}
	
	@Override
	public void decodeDescriptionPacket(NBTTagCompound data) {
		super.decodeDescriptionPacket(data);
		this.readData(data);
		WorldHelper.notifyBlockUpdate(this.WORLD, this.getWorldPosition(), null, null);
	}

	@Override
	public void encodeDescriptionPacket(NBTTagCompound data) {
		super.encodeDescriptionPacket(data);
		this.writeData(data);
	}*/

	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataFrom(data, syncReason);

		if (data.hasKey("circuitType"))
			this.circuitType = circuitType.values()[data.getInteger("circuitType")];

		if (data.hasKey("outputLevel"))
			this.outputLevel = data.getInteger("outputLevel");

		if (data.hasKey("greaterThan"))
			this.greaterThan = data.getBoolean("greaterThan");

		if (data.hasKey("activeOnPulse"))
			this.activeOnPulse = data.getBoolean("activeOnPulse");

		if (data.hasKey("lit"))
			this._isLit = data.getBoolean("lit");

		if (SyncReason.FullSync == syncReason) {
			this.updateRedstoneStateAndNotify();
		} else {
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getWorldPosition(), null, null);
		}
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataTo(data, syncReason);

		data.setInteger("circuitType", this.circuitType.ordinal());
		data.setInteger("outputLevel", this.outputLevel);
		data.setBoolean("greaterThan", this.greaterThan);
		data.setBoolean("activeOnPulse", this.activeOnPulse);
		data.setBoolean("lit", this._isLit);
	}

	@Override
	public boolean isGoodForFrame(IMultiblockValidator validatorCallback) {

		BlockPos position = this.getPos();

		validatorCallback.setLastError("multiblock.validation.reactor.redstoneport_invalid_on_frame", position.getX(), position.getY(), position.getZ());
		return false;
	}

	@Override
	public boolean isGoodForSides(IMultiblockValidator validatorCallback) {
		return true;
	}

	@Override
	public boolean isGoodForTop(IMultiblockValidator validatorCallback) {

		BlockPos position = this.getPos();

		validatorCallback.setLastError("multiblock.validation.reactor.redstoneport_invalid_on_top", position.getX(), position.getY(), position.getZ());
		return false;
	}

	@Override
	public boolean isGoodForBottom(IMultiblockValidator validatorCallback) {

		BlockPos position = this.getPos();

		validatorCallback.setLastError("multiblock.validation.reactor.redstoneport_invalid_on_bottom", position.getX(), position.getY(), position.getZ());
		return false;
	}

	@Override
	public boolean isGoodForInterior(IMultiblockValidator validatorCallback) {

		BlockPos position = this.getPos();

		validatorCallback.setLastError("multiblock.validation.reactor.redstoneport_invalid_on_interior", position.getX(), position.getY(), position.getZ());
		return false;
	}

	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {
		super.onMachineAssembled(controller);
		this.updateRedstoneStateAndNotify();
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();
		this.updateRedstoneStateAndNotify();
	}

	@Override
	public void onMachineActivated() {
		this.updateRedstoneStateAndNotify();
	}

	@Override
	public void onMachineDeactivated() {
		this.updateRedstoneStateAndNotify();
	}

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player) {
		return new ContainerBasic();
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player) {
		return new GuiReactorRedstonePort(new ContainerBasic(), this);
	}

	public static boolean isAlwaysActiveOnPulse(CircuitType circuitType) {
		return circuitType == CircuitType.inputEjectWaste;
	}

	/**
	 * @return the level of power emitted by this port
     */
	public int getWeakPower() {
		return this.isOutput() && this.isRedstoneActive() ? 15 /* strong power */ : 0 /* no power */;
	}

	/**
	 * @return true if the port is receiving or emitting a redstone signal, false otherwise
     */
	public boolean isLit() {
		return this._isLit;
	}

	/**
	 * update the "lit" state and return it
	 * @return the lit state
     */
	protected boolean updateLitState() {
		return this._isLit = (this.isOutput() && this.isRedstoneActive()) || this.isExternallyPowered;
	}

	private void updateRedstoneStateAndNotify() {

		if ((null != this.worldObj) && WorldHelper.calledByLogicalServer(this.worldObj)) {

			boolean oldLitState = this._isLit;

			if (oldLitState != this.updateLitState()) {

				this.nofityTileEntityUpdate();

				EnumFacing outward = this.getOutwardFacing();

				if (null != outward)
					this.worldObj.notifyBlockOfStateChange(this.getWorldPosition().offset(outward), BrBlocks.reactorRedstonePort);
			}
		}
	}

	private boolean _isLit;
}