package erogenousbeef.bigreactors.common.tileentity.base;

import cofh.api.tileentity.IReconfigurableFacing;
import erogenousbeef.bigreactors.common.interfaces.IBeefReconfigurableSides;
import erogenousbeef.bigreactors.common.interfaces.IWrenchable;
import erogenousbeef.bigreactors.gui.IBeefGuiEntity;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import erogenousbeef.bigreactors.net.message.DeviceUpdateExposureMessage;
import erogenousbeef.bigreactors.net.message.DeviceUpdateRotationMessage;
import it.zerono.mods.zerocore.lib.block.ModTileEntity;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import zero.temp.BlockHelper;

import java.util.HashSet;
import java.util.Set;

public abstract class TileEntityBeefBase extends ModTileEntity implements IBeefGuiEntity, IBeefReconfigurableSides,
		IReconfigurableFacing, IWrenchable, ITickable {
	private Set<EntityPlayer> updatePlayers;
	private int ticksSinceLastUpdate;
	private static final int ticksBetweenUpdates = 3;

	protected static final int SIDE_UNEXPOSED = -1;
	protected static final int[] kEmptyIntArray = new int[0];

	protected EnumFacing facing;	// Tile rotation
	int[] exposures; // Inventory/Fluid tank exposure

	public TileEntityBeefBase() {
		super();

		facing = EnumFacing.NORTH;

		exposures = new int[6];
		for(int i = 0; i < exposures.length; i++) {
			exposures[i] = SIDE_UNEXPOSED;
		}

		ticksSinceLastUpdate = 0;
		updatePlayers = new HashSet<EntityPlayer>();
	}

	// IReconfigurableFacing
	@Override
	public int getFacing() { return facing.getIndex(); }

	@Override
	public boolean setFacing(EnumFacing newFacing) {
		if(facing == newFacing) { return false; }

		if(!allowYAxisFacing() && (newFacing == EnumFacing.UP || newFacing == EnumFacing.DOWN)) {
			return false;
		}
		
		facing = newFacing;
		if(!worldObj.isRemote) {
			BlockPos position = this.getPos();
            CommonPacketHandler.INSTANCE.sendToAllAround(new DeviceUpdateRotationMessage(this.getPos(), facing),
					new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(),
							position.getX(), position.getY(), position.getZ(), 50));
            this.markChunkDirty();
		}

		this.callNeighborBlockChange();
		return true;
	}
	
	public int getRotatedSide(EnumFacing side) {
		return BlockHelper.ICON_ROTATION_MAP[facing.getIndex()][side.getIndex()];
	}
	
	@Override
	public boolean rotateBlock() {
		return setFacing(EnumFacing.VALUES[BlockHelper.SIDE_LEFT[facing.getIndex()]]);
	}
	
	@Override
	public boolean onWrench(EntityPlayer player, EnumFacing hitSide) {
		return rotateBlock();
	}
	
	@Override
	public boolean allowYAxisFacing() { return false; }

	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

		// Rotation

		int newFacing;

		if(data.hasKey("facing")) {
			newFacing = Math.max(0, Math.min(5, data.getInteger("facing")));
		}
		else {
			newFacing = 2;
		}

		this.facing = EnumFacing.VALUES[newFacing];

		// Exposure settings
		if(data.hasKey("exposures")) {
			int[] tagExposures = data.getIntArray("exposures");
			assert(tagExposures.length == exposures.length);
			System.arraycopy(tagExposures, 0, exposures, 0, exposures.length);
		}
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

		data.setInteger("facing", facing.getIndex());
		data.setIntArray("exposures", exposures);
	}

	@Override
	public void update() {
		
		if(!this.worldObj.isRemote && this.updatePlayers.size() > 0) {
			ticksSinceLastUpdate++;
			if(ticksSinceLastUpdate >= ticksBetweenUpdates) {
				sendUpdatePacket();
				ticksSinceLastUpdate = 0;
			}
		}
	}

	// Return true if this machine is active.
	public abstract boolean isActive();
	
	// Player updates via IBeefGuiEntity
	@Override
	public void beginUpdatingPlayer(EntityPlayer player) {
		updatePlayers.add(player);
		sendUpdatePacketToClient(player);
	}

	@Override
	public void stopUpdatingPlayer(EntityPlayer player) {
		updatePlayers.remove(player);
	}

	/* TODO commented out to make this compile
	protected IMessage getUpdatePacket() {
		NBTTagCompound childData = new NBTTagCompound();
		onSendUpdate(childData);
		
		return new DeviceUpdateMessage(this.getPos(), childData);
	}
	*/

	private void sendUpdatePacketToClient(EntityPlayer recipient) {
		if(this.worldObj.isRemote) { return; }
		/* TODO commented out to make this compile
        CommonPacketHandler.INSTANCE.sendTo(getUpdatePacket(), (EntityPlayerMP)recipient);
		*/
	}
	
	private void sendUpdatePacket() {
		if(this.worldObj.isRemote) { return; }
		if(this.updatePlayers.size() <= 0) { return; }
		/* TODO commented out to make this compile
		for(EntityPlayer player : updatePlayers) {
            CommonPacketHandler.INSTANCE.sendTo(getUpdatePacket(), (EntityPlayerMP)player);
		}
		*/
	}
	
	// Side Exposure Helpers
	@Override
	public boolean setSide(EnumFacing side, int config) {
		int rotatedSide = this.getRotatedSide(side);

		int numConfig = getNumConfig(side);
		if(config >= numConfig || config < -1) { config = SIDE_UNEXPOSED; }

		exposures[rotatedSide] = config;
		sendExposureUpdate();
		return true;
	}
	
	/**
	 * Autocorrecting getter for checking exposures without having to do the rotation yerself.
	 * @param worldSide The world side whose exposure you wish to get.
	 * @return The current exposure setting for the world side.
	 */
	protected int getExposure(EnumFacing worldSide) {
		return exposures[getRotatedSide(worldSide)];
	}
	
	/**
	 * Used when sending updates from server to client; batch-updates all exposures.
	 * @param newExposures The new set of inventory exposures.
	 */
	public void setSides(int[] newExposures) {
		assert(newExposures.length == exposures.length);
		System.arraycopy(newExposures, 0, exposures, 0, newExposures.length);
		sendExposureUpdate(); // On client, should just notify neighbors
	}
	
	@Override
	public boolean incrSide(EnumFacing side) {
		return changeSide(side, 1);
	}
	
	@Override
	public boolean decrSide(EnumFacing side) {
		return changeSide(side, -1);
	}
	
	private boolean changeSide(EnumFacing side, int amount) {
		int rotatedSide = this.getRotatedSide(side);
		
		int numConfig = getNumConfig(side);
		if(numConfig <= 0) { return false; }
		
		int newConfig = exposures[rotatedSide] + amount;
		if(newConfig >= numConfig) { newConfig = SIDE_UNEXPOSED; }

		return setSide(side, newConfig);
	}
	
	@Override
	public boolean resetSides() {
		boolean changed = false;
		
		for(int i = 0; i < exposures.length; i++) {
			if(exposures[i] != SIDE_UNEXPOSED) {
				changed = true;
				exposures[i] = SIDE_UNEXPOSED;
			}
		}
		
		if(changed) {
			sendExposureUpdate();
		}
		
		return true;
	}
	
	private void sendExposureUpdate() {
		if(!this.worldObj.isRemote) {
			// Send unrotated, as the rotation will be re-applied on the client
			BlockPos position = this.getPos();
            CommonPacketHandler.INSTANCE.sendToAllAround(new DeviceUpdateExposureMessage(this.getPos(), exposures),
					new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(),
							position.getX(), position.getY(), position.getZ(), 50));
            this.markChunkDirty();
		}
		else {
			// Re-render block on client
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
		}

		this.callNeighborTileChange();
		this.callNeighborBlockChange();
	}

	/**
	 * Fill this NBT Tag Compound with your custom entity data.
	 * @param updateTag The tag to which your data should be written
	 */
	protected void onSendUpdate(NBTTagCompound updateTag) {}
	
	/**
	 * Read your custom update data from this NBT Tag Compound.
	 * @param updateTag The tag which should contain your data.
	 */
	public void onReceiveUpdate(NBTTagCompound updateTag) {}

	// Weird shit from TileCoFHBase
	public String getName() {
		return this.getBlockType().getUnlocalizedName();
	}
	
	public int getType() {
		return getBlockMetadata();
	}
}
