package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.init.BrBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import erogenousbeef.bigreactors.client.gui.GuiReactorStatus;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.block.BlockReactorPart;
import erogenousbeef.bigreactors.gui.container.ContainerReactorController;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import zero.mods.zerocore.util.WorldHelper;

public class TileEntityReactorPart extends TileEntityReactorPartBase {

	public TileEntityReactorPart() {
		super();
	}

	@Override
	public boolean isGoodForFrame(IMultiblockValidator validatorCallback) {

		BlockPos position = this.getPos();
		IBlockState state = this.worldObj.getBlockState(position);

		if (BrBlocks.reactorCasing == state.getBlock())
			return true;

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_frame_block", position.getX(), position.getY(), position.getZ());
		return false;
	}

	@Override
	public boolean isGoodForSides(IMultiblockValidator validatorCallback) {
		// All parts are valid for sides, by default
		return true;
	}

	@Override
	public boolean isGoodForTop(IMultiblockValidator validatorCallback) {
		// All parts are valid for the top, by default
		return true;
	}

	@Override
	public boolean isGoodForBottom(IMultiblockValidator validatorCallback) {
		// All parts are valid for the bottom, by default
		return true;
	}

	@Override
	public boolean isGoodForInterior(IMultiblockValidator validatorCallback) {

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_part_for_interior", this.getPos());
		return false;
	}

	@Override
	public void onMachineAssembled(MultiblockControllerBase multiblockController) {
		super.onMachineAssembled(multiblockController);
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();
	}

	@Override
	public void onMachineActivated() {
		// Re-render controllers on client
		if (this.worldObj.isRemote && (this.getBlockType() == BrBlocks.reactorController))
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
	}

	@Override
	public void onMachineDeactivated() {
		// Re-render controllers on client
		if (this.worldObj.isRemote && (this.getBlockType() == BrBlocks.reactorController))
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
	}

	/*
	// IMultiblockGuiHandler
	/ **
	 * @return The Container object for use by the GUI. Null if there isn't any.
	 * /
	@Override
	public Object getContainer(InventoryPlayer inventoryPlayer) {
		if(!this.isConnected()) {
			return null;
		}

		// TODO Commented temporarily to allow this thing to compile...
		/ *
		int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);		
		if(BlockReactorPart.isController(metadata)) {
			return new ContainerReactorController(this, inventoryPlayer.player);
		}
		* /
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getGuiElement(InventoryPlayer inventoryPlayer) {
		if(!this.isConnected()) {
			return null;
		}

		// TODO Commented temporarily to allow this thing to compile...
		/ *
		int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
		if(BlockReactorPart.isController(metadata)) {
			return new GuiReactorStatus(new ContainerReactorController(this, inventoryPlayer.player), this);
		}
		* /
		return null;
	}
	*/

	public PartTier getMachineTier() {
		return this.isConnected() ? ((MultiblockReactor)this.getMultiblockController()).getMachineTier() : PartTier.Standard;
	}
}
