package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.RotorBladeState;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;

public class TileEntityTurbineRotorBlade extends TileEntityTurbinePartBase {

    public TileEntityTurbineRotorBlade() {
        //this._state = RotorBladeState.Y;
    }

    public RotorBladeState getShaftState() {
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

    private RotorBladeState _state;
}
