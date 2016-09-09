package erogenousbeef.bigreactors.common.multiblock.tileentity;

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;

public class TileEntityTurbineRotorShaft extends TileEntityTurbinePartBase {

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
}
