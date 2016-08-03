package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.client.gui.GuiTurbineController;
import erogenousbeef.bigreactors.common.multiblock.block.BlockTurbinePart;
import erogenousbeef.bigreactors.gui.container.ContainerSlotless;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTurbinePartStandard extends TileEntityTurbinePartBase {

	public TileEntityTurbinePartStandard() {
		super();
	}

	@Override
	public boolean isGoodForFrame(IMultiblockValidator validatorCallback) {
		if(getBlockMetadata() != BlockTurbinePart.METADATA_HOUSING) {

			BlockPos position = this.getPos();

			validatorCallback.setLastError("multiblock.validation.turbine.invalid_part_for_frame", position.getX(), position.getY(), position.getZ());
			return false;
		}
		return true;
	}

	@Override
	public boolean isGoodForSides(IMultiblockValidator validatorCallback) {
		return true;
	}

	@Override
	public boolean isGoodForTop(IMultiblockValidator validatorCallback) {
		return true;
	}

	@Override
	public boolean isGoodForBottom(IMultiblockValidator validatorCallback) {
		return true;
	}

	@Override
	public boolean isGoodForInterior(IMultiblockValidator validatorCallback) {
		if(getBlockMetadata() != BlockTurbinePart.METADATA_HOUSING) {

			BlockPos position = this.getPos();

			validatorCallback.setLastError("multiblock.validation.turbine.invalid_part_for_interior", position.getX(), position.getY(), position.getZ());
			return false;
		}
		return true;
	}

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player) {
		return super.getServerGuiElement(guiId, player);
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player) {
		return super.getClientGuiElement(guiId, player);
	}


	// TODO GUI
	//@Override
	public Object getContainer(InventoryPlayer inventoryPlayer) {
		if(!this.isConnected()) {
			return null;
		}
		
		if(getBlockMetadata() == BlockTurbinePart.METADATA_CONTROLLER) {
			return (Object)(new ContainerSlotless(getTurbine(), inventoryPlayer.player));
		}
		
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	//@Override
	public Object getGuiElement(InventoryPlayer inventoryPlayer) {
		if(!this.isConnected()) {
			return null;
		}

		if(getBlockMetadata() == BlockTurbinePart.METADATA_CONTROLLER) {
			return new GuiTurbineController((Container)getContainer(inventoryPlayer), this);
		}
		return null;
	}

	@Override
	public void onMachineActivated() {
		// Re-render controller as active state has changed
		if(worldObj.isRemote && getBlockMetadata() == BlockTurbinePart.METADATA_CONTROLLER) {
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
		}
	}

	@Override
	public void onMachineDeactivated() {
		// Re-render controller as active state has changed
		if(worldObj.isRemote && getBlockMetadata() == BlockTurbinePart.METADATA_CONTROLLER) {
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
		}
	}	
}
