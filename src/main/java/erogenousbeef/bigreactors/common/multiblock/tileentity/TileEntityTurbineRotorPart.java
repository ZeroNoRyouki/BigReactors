package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.RotorShaftState;
import erogenousbeef.bigreactors.common.multiblock.block.BlockTurbineRotorPart;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;

public class TileEntityTurbineRotorPart extends TileEntityTurbinePartBase {

	public TileEntityTurbineRotorPart() {
		this._state = RotorShaftState.Y_NOBLADES;
	}

	public RotorShaftState getShaftState() {
		return this._state;
	}

	@Override
	public boolean isGoodForFrame(IMultiblockValidator validatorCallback) {

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_rotor_position");
		return false;
	}

	@Override
	public boolean isGoodForSides(IMultiblockValidator validatorCallback) {

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_rotor_position");
		return false;
	}

	@Override
	public boolean isGoodForTop(IMultiblockValidator validatorCallback) {

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_rotor_position");
		return false;
	}

	@Override
	public boolean isGoodForBottom(IMultiblockValidator validatorCallback) {

		validatorCallback.setLastError("multiblock.validation.turbine.invalid_rotor_position");
		return false;
	}

	@Override
	public boolean isGoodForInterior(IMultiblockValidator validatorCallback) {
		return true;
	}

	public boolean isRotorShaft() {
		return BlockTurbineRotorPart.isRotorShaft(getBlockMetadata());
	}

	public boolean isRotorBlade() {
		return BlockTurbineRotorPart.isRotorBlade(getBlockMetadata());
	}

	private RotorShaftState _state;
}
