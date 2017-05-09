package erogenousbeef.bigreactors.utils;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Fluid-related helper functions
 *
 * TODO: move this to Zero CORE
 */

public final class FluidHelper {

    public static int fillAdjacentHandler(TileEntity origin, EnumFacing facing, FluidStack fluid, boolean doFill) {

        final TileEntity target = getTile(origin, facing);

        facing = facing.getOpposite();
        /*
        return (null != CAPABILITY && null != target && target.hasCapability(CAPABILITY, facing)) ?
                target.getCapability(CAPABILITY, facing).fill(fluid, doFill) : 0;
        */
        if (null != CAPABILITY && null != target && target.hasCapability(CAPABILITY, facing)) {

            IFluidHandler hander = CAPABILITY.cast(target.getCapability(CAPABILITY, facing));

            return hander.fill(fluid, doFill);
        }
        return 0;

    }

    @CapabilityInject(IFluidHandler.class)
    private static final Capability<IFluidHandler> CAPABILITY = null;

    // TODO: move to Zero CORE World Helper

    @Nullable
    private static TileEntity getTile(@Nonnull World world, @Nonnull BlockPos origin, @Nonnull EnumFacing facing) {

        origin = origin.offset(facing);
        return world.isBlockLoaded(origin) ? world.getTileEntity(origin) : null;
    }

    @Nullable
    private static TileEntity getTile(@Nonnull TileEntity origin, @Nonnull EnumFacing facing) {
        return getTile(origin.getWorld(), origin.getPos(), facing);
    }
}
