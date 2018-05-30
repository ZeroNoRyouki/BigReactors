package erogenousbeef.bigreactors.common.multiblock.helpers;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Set;

public class ReactorFuelRodsLayout {

    public static final ReactorFuelRodsLayout DEFAULT = new ReactorFuelRodsLayout();

    public ReactorFuelRodsLayout(@Nonnull final MultiblockReactor reactor) {

        this._cachedOutwardFacing = reactor.getControlRodByIndex(0).getOutwardFacing();

        final BlockPos minCoord = reactor.getMinimumCoord();
        final BlockPos maxCoord = reactor.getMaximumCoord();

        switch (this.getAxis()) {

            default:
            case X:
                this._rodLength = Math.max(1, maxCoord.getX() - minCoord.getX() - 1);
                break;

            case Y:
                this._rodLength = Math.max(1, maxCoord.getY() - minCoord.getY() - 1);
                break;

            case Z:
                this._rodLength = Math.max(1, maxCoord.getZ() - minCoord.getZ() - 1);
                break;
        }
    }

    private ReactorFuelRodsLayout() {

        this._cachedOutwardFacing = EnumFacing.UP;
        this._rodLength = 1;
    }

    public EnumFacing.Axis getAxis() {
        return this._cachedOutwardFacing.getAxis();
    }

    public EnumFacing[] getRadiateDirections() {

        switch (this.getAxis()) {

            case X:
                return RADIATE_DIRECTIONS_X_AXIS;

            default:
            case Y:
                return RADIATE_DIRECTIONS_Y_AXIS;

            case Z:
                return RADIATE_DIRECTIONS_Z_AXIS;
        }
    }

    public int getRodLength() {
        return this._rodLength;
    }

    public void updateFuelData(@Nonnull final FuelContainer fuelData, final int fuelRodsTotalCount) {
    }

    public void updateFuelRodsOcclusion(@Nonnull final Set<TileEntityReactorFuelRod> fuelRods) {
    }

    private final EnumFacing _cachedOutwardFacing;
    private final int _rodLength;

    private static final EnumFacing[] RADIATE_DIRECTIONS_X_AXIS = {EnumFacing.UP, EnumFacing.NORTH, EnumFacing.DOWN, EnumFacing.SOUTH};
    private static final EnumFacing[] RADIATE_DIRECTIONS_Y_AXIS = EnumFacing.HORIZONTALS;
    private static final EnumFacing[] RADIATE_DIRECTIONS_Z_AXIS = {EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.WEST, EnumFacing.UP};
}