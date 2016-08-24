package erogenousbeef.bigreactors.common.multiblock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum RotorShaftState implements IStringSerializable {

    HIDDEN,
    // Shaft direction - blades direction
    Y_NOBLADES,
    Y_X,
    Y_Z,
    Y_XZ,
    X_NOBLADES,
    X_Y,
    X_Z,
    X_YZ,
    Z_NOBLADES,
    Z_Y,
    Z_X,
    Z_XY;

    RotorShaftState() {
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

    @Nonnull
    public static RotorShaftState from(@Nonnull EnumFacing.Axis shaftAxis) {

        RotorShaftState state;

        switch (shaftAxis) {

            default:
            case Y:
                state = Y_NOBLADES;
                break;

            case X:
                state = X_NOBLADES;
                break;

            case Z:
                state = Z_NOBLADES;
                break;
        }

        return state;
    }

    @Nonnull
    public static RotorShaftState from(@Nonnull EnumFacing.Axis shaftAxis, @Nonnull boolean[] directionsWithBlades) {

        RotorShaftState state;
        final EnumFacing[] directions = RotorShaftState.getBladesDirections(shaftAxis);
        EnumFacing.Axis[] bladesAxis = new EnumFacing.Axis[2];

        for (int i = 0; i < 2; ++i) {

            if (directionsWithBlades[i] && directionsWithBlades[i + 2])
                bladesAxis[i] = directions[i].getAxis();
        }

        switch (shaftAxis) {

            default:
            case Y:
                state = Y_NOBLADES;
                break;

            case X:
                state = X_NOBLADES;
                break;

            case Z:
                state = Z_NOBLADES;
                break;
        }

        return state;
    }

    public static EnumFacing[] getBladesDirections(EnumFacing.Axis axis) {

        switch (axis) {

            case X:
                return BLADES_DIRECTIONS_X;

            default:
            case Y:
                return BLADES_DIRECTIONS_Y;

            case Z:
                return BLADES_DIRECTIONS_Z;
        }
    }

    public static final EnumFacing[] BLADES_DIRECTIONS_X;
    public static final EnumFacing[] BLADES_DIRECTIONS_Y;
    public static final EnumFacing[] BLADES_DIRECTIONS_Z;

    private final String _name;

    static {

        BLADES_DIRECTIONS_X = new EnumFacing[] { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.DOWN, EnumFacing.SOUTH };
        BLADES_DIRECTIONS_Y = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST };
        BLADES_DIRECTIONS_Z = new EnumFacing[] { EnumFacing.UP, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.WEST };
    }
}
