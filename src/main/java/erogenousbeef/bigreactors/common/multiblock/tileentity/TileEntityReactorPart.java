package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

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

	public PartTier getMachineTier() {
		return this.isConnected() ? this.getReactorController().getMachineTier() : PartTier.Legacy;
	}
}
