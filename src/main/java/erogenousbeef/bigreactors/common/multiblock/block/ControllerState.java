package erogenousbeef.bigreactors.common.multiblock.block;

import net.minecraft.util.IStringSerializable;

public enum ControllerState implements IStringSerializable {

    Off,
    Idle,
    Active;

    ControllerState() {

        this._name = this.name().toLowerCase();
    }

    @Override
    public String toString() {

        return this._name;
    }

    @Override
    public String getName() {

        return this._name;
    }

    private final String _name;
}
