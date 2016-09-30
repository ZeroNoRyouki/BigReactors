package erogenousbeef.bigreactors.common.multiblock.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityReactorPowerTapTesla extends TileEntityReactorPowerTap {

    public TileEntityReactorPowerTapTesla() {
        this._handler = new PowerTapHandlerTesla(this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

        if (((null != PowerTapHandlerTesla.CAPABILITY_TESLA_PRODUCER && PowerTapHandlerTesla.CAPABILITY_TESLA_PRODUCER == capability) ||
                (null != PowerTapHandlerTesla.CAPABILITY_TESLA_HOLDER && PowerTapHandlerTesla.CAPABILITY_TESLA_HOLDER == capability)))
            return true;

        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

        if ((null != PowerTapHandlerTesla.CAPABILITY_TESLA_PRODUCER) && (PowerTapHandlerTesla.CAPABILITY_TESLA_PRODUCER == capability))
            return PowerTapHandlerTesla.CAPABILITY_TESLA_PRODUCER.cast(this._handler);

        if ((null != PowerTapHandlerTesla.CAPABILITY_TESLA_HOLDER) && (PowerTapHandlerTesla.CAPABILITY_TESLA_HOLDER == capability))
            return PowerTapHandlerTesla.CAPABILITY_TESLA_HOLDER.cast(this._handler);

        return super.getCapability(capability, facing);
    }

    // IPowerProvider

    @Override
    public boolean isProviderConnected() {
        return this._handler.isProviderConnected();
    }

    @Override
    public long onProvidePower(long units) {
        return this._handler.onProvidePower(units);
    }

    // TileEntityReactorPowerTap

    @Override
    protected void checkForConnections(IBlockAccess world, BlockPos position) {
        this._handler.checkForConnections(world, position);
    }

    private final PowerTapHandlerTesla _handler;
}