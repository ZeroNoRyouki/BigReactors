package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.client.gui.GuiReactorRedNetPort;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.CircuitType;
import erogenousbeef.bigreactors.common.compat.IdReference;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.gui.container.ContainerBasic;
import erogenousbeef.bigreactors.net.helpers.RedNetChange;
import it.zerono.mods.zerocore.lib.BlockFacings;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
		@Optional.Interface(iface = "erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart",
				modid = IdReference.MODID_MINEFACTORYRELOADED),
})
public class TileEntityReactorRedNetPort extends TileEntityReactorPart implements ITickableMultiblockPart /*, INeighborUpdatableEntity*/ {

	protected final static int minInputEnumValue = CircuitType.inputActive.ordinal();
	protected final static int maxInputEnumValue = CircuitType.inputEjectWaste.ordinal();
	protected final static int minOutputEnumValue = CircuitType.outputFuelTemperature.ordinal();
	protected final static int maxOutputEnumValue = CircuitType.outputEnergyAmount.ordinal();

	protected CircuitType[] channelCircuitTypes;
	protected BlockPos[] coordMappings;
	protected boolean[] inputActivatesOnPulse;
	protected int[] oldValue;

	public final static int CHANNELS_COUNT = 16;

	private int ticksSinceLastUpdate;
	
	public TileEntityReactorRedNetPort() {
		
		channelCircuitTypes = new CircuitType[CHANNELS_COUNT];
		coordMappings = new BlockPos[CHANNELS_COUNT];
		inputActivatesOnPulse = new boolean[CHANNELS_COUNT];
		oldValue = new int[CHANNELS_COUNT];

		for(int i = 0; i < CHANNELS_COUNT; i++) {
			channelCircuitTypes[i] = CircuitType.DISABLED;
			coordMappings[i] = null;
			inputActivatesOnPulse[i] = false;
			oldValue[i] = 0;
		}

		ticksSinceLastUpdate = 0;
	}
	
	/*// IMultiblockPart
	@Override
	public void onAttached(MultiblockControllerBase newController) {
		super.onAttached(newController);

		if(this.worldObj.isRemote) { return; } 
		
		checkForConnections(this.worldObj, this.getPos());
	}
	
	@Override
	public void onMachineAssembled(MultiblockControllerBase multiblockController) {
		super.onMachineAssembled(multiblockController);

		if(this.worldObj.isRemote) { return; } 
		
		checkForConnections(this.worldObj, this.getPos());
	}*/

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player) {
		return new ContainerBasic();
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player) {
		return new GuiReactorRedNetPort(new ContainerBasic(), this);
	}

	@Override
	public boolean canOpenGui(World world, BlockPos posistion, IBlockState state) {
		return true;
	}

	// RedNet API

	public int[] getOutputValues() {
		int[] outputs = new int[CHANNELS_COUNT];
		for(int i = 0; i < CHANNELS_COUNT; i++) {
			outputs[i] = getValueForChannel(i);
		}
		
		return outputs;
	}
	
	public int getValueForChannel(int channel) {
		if(channel < 0 || channel >= CHANNELS_COUNT) { return 0; }
		
		if(!this.isConnected()) { return 0; }
		
		//TileEntity te = null;
		
		switch(channelCircuitTypes[channel]) {
		case outputFuelTemperature:
			return (int)Math.floor(getReactorController().getFuelHeat());
		case outputCasingTemperature:
			return (int)Math.floor(getReactorController().getReactorHeat());
		case outputFuelMix:
			MultiblockReactor controller = getReactorController();
			return (int)Math.floor(((float)controller.getFuelAmount() / (float)controller.getCapacity())*100.0f);
		case outputFuelAmount:
			return getReactorController().getFuelAmount();
		case outputWasteAmount:
			return getReactorController().getWasteAmount();
		case outputEnergyAmount:
			//int energyStored, energyTotal;
			MultiblockReactor reactor = this.getReactorController();
			if(reactor != null) {
				return reactor.getEnergyStoredPercentage();
			}
			return 0;
		default:
			return 0;
		}
	}
	
	public void onInputValuesChanged(int[] newValues) {
		for(int i = 0; i < newValues.length; i++) {
			onInputValueChanged(i, newValues[i]);
		}
	}
	
	public void onInputValueChanged(int channel, int newValue) {
		if(channel < 0 || channel >= CHANNELS_COUNT) { return; }
		CircuitType type = channelCircuitTypes[channel];
		if(!isInput(type)) { return; }
		if(!this.isConnected()) { return; }
		
		if(newValue == oldValue[channel]) { return; }
		boolean isPulse = (oldValue[channel] == 0 && newValue != 0);
		
		MultiblockReactor reactor = null;
		switch(type) {
		case inputActive:
			reactor = getReactorController();
			if(inputActivatesOnPulse[channel]) {
				if(isPulse) {
					reactor.setActive(!reactor.getActive());
				}
			}
			else {
				boolean newActive = newValue != 0;
				if(newActive != reactor.getActive()) {
					reactor.setActive(newActive);
				}
			}
			break;
		case inputSetControlRod:
			// This doesn't make sense for pulsing
			newValue = Math.min(100, Math.max(0, newValue)); // Clamp to 0..100
			if(newValue == oldValue[channel]) { return; }

			if(coordMappings[channel] != null) {
				setControlRodInsertion(channel, coordMappings[channel], newValue);
			}
			else {
				reactor = getReactorController();
				reactor.setAllControlRodInsertionValues(newValue);
			}
			break;
		case inputEjectWaste:
			// This only makes sense for pulsing
			if(isPulse) {
				reactor = getReactorController();
				reactor.ejectWaste(false, null);
			}
		default:
			break;
		}
		
		oldValue[channel] = newValue;
	}

	/*
	// Public RedNet helpers for GUI & updates
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
		checkForConnections(world, x, y, z);
	}
	
	@Override
	public void onNeighborTileChange(IBlockAccess world, int x, int y, int z, int neighborX, int neighborY, int neighborZ) {
		checkForConnections(world, x, y, z);
	}*/
	
	/**
	 * Updates the connected RedNet network, if there is one.
	 * Will only send one update per N ticks, where N is a configurable setting.
	 */
	@Override
	@Optional.Method(modid = IdReference.MODID_MINEFACTORYRELOADED)
	public void onMultiblockServerTick() {
		/* TODO: re-add when Minefactory Reloaded is out for 1.11.2
		if (!this.isConnected())
			return;

		if (this.ticksSinceLastUpdate++ < BigReactors.CONFIG.ticksPerRedstoneUpdate)
			return;

		final BlockFacings facings = this.getOutwardsDir();
		final World world = this.getWorld();
		final BlockPos partPosition = this.getWorldPosition();

		for (EnumFacing facing : EnumFacing.VALUES) {

			if (facings.isSet(facing)) {

				final BlockPos neighborPosition = partPosition.offset(facing);
				final IBlockState blockState = world.isBlockLoaded(neighborPosition) ? world.getBlockState(neighborPosition) : null;

				if (null == blockState)
					continue;

				final Block neighborBlock = blockState.getBlock();

				if (neighborBlock instanceof IRedNetNetworkContainer)
					((IRedNetNetworkContainer)neighborBlock).updateNetwork(world, neighborPosition, facing.getOpposite());

				if (neighborBlock instanceof IRedNetInputNode)
					((IRedNetInputNode)neighborBlock).onInputsChanged(world, neighborPosition, facing.getOpposite(),
							this.getOutputValues());
			}
		}

		this.ticksSinceLastUpdate = 0;
		*/
	}

	public CircuitType getChannelCircuitType(int channel) {
		if(channel < 0 || channel >= CHANNELS_COUNT) { return CircuitType.DISABLED; }
		return channelCircuitTypes[channel];
	}

	public BlockPos getMappedCoord(int channel) {
		return this.coordMappings[channel];
	}
	
	public boolean isInputActivatedOnPulse(int channel) {
		return this.inputActivatesOnPulse[channel];
	}

	// RedNet helper methods
	protected void clearChannel(int channel) {
		channelCircuitTypes[channel] = CircuitType.DISABLED;
		coordMappings[channel] = null;
		inputActivatesOnPulse[channel] = false;
		oldValue[channel] = 0;
	}

	protected TileEntity getMappedTileEntity(int channel) {
		if(channel < 0 || channel >= CHANNELS_COUNT) { return null; }

		BlockPos coord = coordMappings[channel];

		if (coord == null || !WorldHelper.blockChunkExists(this.getWorld().getChunkProvider(), coord))
			return null;
		
		return this.getWorld().getTileEntity(coord);
	}
	
	protected void setControlRodInsertion(int channel, BlockPos position, int newValue) {
		if(!this.isConnected()) { return; }


		if(!WorldHelper.blockChunkExists(this.getWorld().getChunkProvider(), position))
			return;
		
		TileEntity te = this.getWorld().getTileEntity(position);
		if(te instanceof TileEntityReactorControlRod) {
			((TileEntityReactorControlRod)te).setControlRodInsertion((short)newValue);
		}
		else {
			clearChannel(channel);
		}
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataTo(data, syncReason);

		final NBTTagList tagArray = new NBTTagList();

		for (int i = 0; i < CHANNELS_COUNT; ++i)
			tagArray.appendTag(this.encodeChannelSetting(i));

		data.setTag("redNetConfig", tagArray);
	}

	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataFrom(data, syncReason);

		final NBTTagList tagArray = data.getTagList("redNetConfig", 10);

		for (int i = 0; i < tagArray.tagCount(); ++i)
			this.decodeChannelSetting(tagArray.getCompoundTagAt(i) );
	}

	private NBTTagCompound encodeChannelSetting(int channel) {
		NBTTagCompound entry = new NBTTagCompound();
		
		entry.setInteger("channel", channel);
		entry.setInteger("setting", this.channelCircuitTypes[channel].ordinal());
		if(isInput(this.channelCircuitTypes[channel]) && CircuitType.canBeToggledBetweenPulseAndNormal(this.channelCircuitTypes[channel])) {
			entry.setBoolean("pulse", this.inputActivatesOnPulse[channel]);
		}
		if( CircuitType.hasCoordinate(this.channelCircuitTypes[channel]) ) {
			BlockPos coord = this.coordMappings[channel];
			if(coord != null) {
				entry.setInteger("x", coord.getX());
				entry.setInteger("y", coord.getY());
				entry.setInteger("z", coord.getZ());
			}
		}
		
		return entry;
	}

	private void decodeChannelSetting(NBTTagCompound settingTag) {
		int channel = settingTag.getInteger("channel");
		int settingIdx = settingTag.getInteger("setting");
		
		clearChannel(channel);
		
		channelCircuitTypes[channel] = CircuitType.values()[settingIdx];
		
		if(isInput(this.channelCircuitTypes[channel]) && CircuitType.canBeToggledBetweenPulseAndNormal(this.channelCircuitTypes[channel])) {
			inputActivatesOnPulse[channel] = settingTag.getBoolean("pulse");
		}

		if (CircuitType.hasCoordinate(channelCircuitTypes[channel]) &&
				settingTag.hasKey("x") && settingTag.hasKey("y") && settingTag.hasKey("z"))
			coordMappings[channel] = new BlockPos(settingTag.getInteger("x"), settingTag.getInteger("y"), settingTag.getInteger("z"));
	}

	// Receives settings from a client via an update packet
	public void onCircuitUpdate(RedNetChange[] changes) {
		if(changes == null || changes.length < 1) { return; }
		
		for(int i = 0; i < changes.length; i++) {
			int channelID = changes[i].getChannel();
			CircuitType newType = changes[i].getType();
			
			channelCircuitTypes[channelID] = newType;
			
			if(CircuitType.canBeToggledBetweenPulseAndNormal(newType)) {
				inputActivatesOnPulse[channelID] = changes[i].getPulseOrToggle();
			}
			
			if(CircuitType.hasCoordinate(newType)) {
				BlockPos coord = changes[i].getCoord();
				
				// Validate that we're pointing at the right thing, just in case.
				if(coord != null) {
					TileEntity te = this.getWorld().getTileEntity(coord);
					if(!(te instanceof TileEntityReactorControlRod)) {
						BRLog.warning("Invalid tile entity reference at coordinate %s - rednet circuit expected a control rod", coord);
						coord = null;
					}
				}

				coordMappings[channelID] = coord;
			}
			else {
				coordMappings[channelID] = null;
			}
		}

		WorldHelper.notifyBlockUpdate(this.getWorld(), this.getPos(), null, null);
		markDirty();
	}

	
	/**
	 * Check for a world connection, if we're assembled.
	 *//*
	protected void checkForConnections(IBlockAccess world, int x, int y, int z) {
		ForgeDirection out = getOutwardsDir();

		if(out == ForgeDirection.UNKNOWN) {
			redNetwork = null;
			redNetInput = null;
		}
		else {
			// Check for rednet connections nearby
			redNetwork = null;
			redNetInput = null;

			Block b = worldObj.getBlock(x + out.offsetX, y + out.offsetY, z + out.offsetZ);
			if(!(b instanceof BlockReactorPart)) {
				if(b instanceof IRedNetNetworkContainer) {
					redNetwork = (IRedNetNetworkContainer)b;
				}
				else if(b instanceof IRedNetInputNode) {
					redNetInput = (IRedNetInputNode)b;
				}
			}
		}
	}*/

	// Static Helpers
	public static boolean isInput(CircuitType type) { return type.ordinal() >= minInputEnumValue && type.ordinal() <= maxInputEnumValue; }
	public static boolean isOutput(CircuitType type) { return type.ordinal() >= minOutputEnumValue && type.ordinal() <= maxOutputEnumValue; }
}