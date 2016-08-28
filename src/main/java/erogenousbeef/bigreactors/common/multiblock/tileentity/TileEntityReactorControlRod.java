package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.client.gui.GuiReactorControlRod;
import erogenousbeef.bigreactors.gui.container.ContainerBasic;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import erogenousbeef.bigreactors.net.message.ControlRodUpdateMessage;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityReactorControlRod extends TileEntityReactorPart {
	public final static short maxInsertion = 100;
	public final static short minInsertion = 0;

	// Radiation
	protected short controlRodInsertion; // 0 = retracted fully, 100 = inserted fully
	
	// User settings
	protected String name;
	
	public TileEntityReactorControlRod() {
		super();
	
		controlRodInsertion = minInsertion;
		name = "";
	}

	@Override
	public boolean canOpenGui(World world, BlockPos posistion, IBlockState state) {

		MultiblockControllerBase controller = this.getMultiblockController();

		return null != controller && controller.isAssembled();
	}

	// Data accessors
	public short getControlRodInsertion() {
		return this.controlRodInsertion;
	}
	
	public void setControlRodInsertion(short newInsertion) {
		if(newInsertion > maxInsertion || newInsertion < minInsertion || newInsertion == controlRodInsertion) { return; }
		if(!isConnected()) { return; }

		this.controlRodInsertion = (short)Math.max(Math.min(newInsertion, maxInsertion), minInsertion);
		this.sendControlRodUpdate();
	}
	
	public void setName(String newName) {
		if(this.name.equals(newName)) { return; }
		
		this.name = newName;
		if(!this.worldObj.isRemote) {
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
		}
	}
	
	public String getName() {
		return this.name;
	}

	// Network Messages
	public void onClientControlRodChange(int amount) {
		setControlRodInsertion((short)(this.controlRodInsertion + amount));
	}

	protected void sendControlRodUpdate() {
		if(this.worldObj == null || this.worldObj.isRemote) { return; }

		BlockPos position = this.getPos();

        CommonPacketHandler.INSTANCE.sendToAllAround(new ControlRodUpdateMessage(position, controlRodInsertion),
				new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(),
						position.getX(), position.getY(), position.getZ(), 50));
	}
	
	@SideOnly(Side.CLIENT)
	public void onControlRodUpdate(short controlRodInsertion) {
		this.controlRodInsertion = controlRodInsertion;
	}

	// TileEntity overrides

	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataFrom(data, syncReason);

		if (SyncReason.NetworkUpdate == syncReason) {

			this.readLocalDataFromNBT(data);

		} else {

			if(data.hasKey("reactorControlRod")) {
				NBTTagCompound localData = data.getCompoundTag("reactorControlRod");
				this.readLocalDataFromNBT(localData);

				if(worldObj != null && worldObj.isRemote) {
					WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
				}
			}
		}
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataTo(data, syncReason);

		if (SyncReason.FullSync == syncReason) {

			this.writeLocalDataToNBT(data);

		} else {

			NBTTagCompound localData = new NBTTagCompound();
			this.writeLocalDataToNBT(localData);
			data.setTag("reactorControlRod", localData);
		}
	}

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player) {
		return new ContainerBasic();
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player) {
		return new GuiReactorControlRod(new ContainerBasic(), this);
	}
	
	// TileEntityReactorPart
	@Override
	public boolean isGoodForFrame(IMultiblockValidator validatorCallback) {

		BlockPos position = this.getPos();

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_control_rods_position",
				position.getX(), position.getY(), position.getZ());
		return false;
	}

	@Override
	public boolean isGoodForSides(IMultiblockValidator validatorCallback) {

        for (EnumFacing direction: EnumFacing.UP.HORIZONTALS)
            if (this.checkForFuelRod(direction))
                return true;

        BlockPos position = this.getPos();

        validatorCallback.setLastError("multiblock.validation.reactor.invalid_control_rods_position",
                position.getX(), position.getY(), position.getZ());

        return false;
	}

	@Override
	public boolean isGoodForTop(IMultiblockValidator validatorCallback) {

		if (!this.checkForFuelRod(EnumFacing.DOWN)) {

			BlockPos position = this.getPos();

			validatorCallback.setLastError("multiblock.validation.reactor.invalid_control_rods_column",
					position.getX(), position.getY(), position.getZ());
			return false;
		}

		return true;
	}

	@Override
	public boolean isGoodForBottom(IMultiblockValidator validatorCallback) {

		if (!this.checkForFuelRod(EnumFacing.UP)) {

			BlockPos position = this.getPos();

			validatorCallback.setLastError("multiblock.validation.reactor.invalid_control_rods_position",
					position.getX(), position.getY(), position.getZ());
			return false;
		}

		return true;
	}

	@Override
	public boolean isGoodForInterior(IMultiblockValidator validatorCallback) {

		BlockPos position = this.getPos();

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_control_rods_position",
				position.getX(), position.getY(), position.getZ());
		return false;
	}

	private boolean checkForFuelRod(EnumFacing fuelDirection) {
		return null != fuelDirection && this.worldObj.getTileEntity(this.getWorldPosition().offset(fuelDirection)) instanceof TileEntityReactorFuelRod;
	}

	// Save/Load Helpers
	private void readLocalDataFromNBT(NBTTagCompound data) {
		if(data.hasKey("controlRodInsertion")) {
			this.controlRodInsertion = data.getShort("controlRodInsertion");
		}
		
		if(data.hasKey("name")) {
			this.name = data.getString("name");
		}
		else {
			this.name = "";
		}
	}
	
	private void writeLocalDataToNBT(NBTTagCompound data) {
		data.setShort("controlRodInsertion", controlRodInsertion);
		
		if(!this.name.isEmpty()) {
			data.setString("name", this.name);
		}
	}
}
