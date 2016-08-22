package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import net.minecraft.util.math.BlockPos;

public class TileEntityTurbinePart extends TileEntityTurbinePartBase {

	@Override
	public boolean isGoodForFrame(IMultiblockValidator validatorCallback) {

		if (this.getBlockType() == BrBlocks.turbineHousing)
			return true;

		BlockPos position = this.getPos();

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_part_for_frame", position.getX(), position.getY(), position.getZ());
		return false;
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

		if (this.getBlockType() == BrBlocks.turbineHousing)
			return true;

		BlockPos position = this.getPos();

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_part_for_interior", position.getX(), position.getY(), position.getZ());
		return false;
	}
}
