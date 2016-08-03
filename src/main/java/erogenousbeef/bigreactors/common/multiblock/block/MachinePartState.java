package erogenousbeef.bigreactors.common.multiblock.block;

import it.zerono.mods.zerocore.api.multiblock.rectangular.PartPosition;
import net.minecraft.util.IStringSerializable;

public enum MachinePartState implements IStringSerializable {

    Disassembled,
    AssembledDown,
    AssembledUp,
    AssembledNorth,
    AssembledSouth,
    AssembledWest,
    AssembledEast;

    MachinePartState() {

        this._name = this.name().toLowerCase();
    }

    public static MachinePartState from(PartPosition position) {

        MachinePartState state;

        switch (position) {

            case NorthFace:
                state = AssembledNorth;
                break;

            case SouthFace:
                state = AssembledSouth;
                break;

            case WestFace:
                state = AssembledWest;
                break;

            case EastFace:
                state = AssembledEast;
                break;

            case TopFace:
                state = AssembledUp;
                break;
            case BottomFace:
                state = AssembledDown;
                break;

            default:
                state = Disassembled;
                break;
        }

        return state;
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
