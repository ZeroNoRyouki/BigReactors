package erogenousbeef.bigreactors.common.multiblock.tileentity;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TileEntityTurbinePowerTapRedstoneFlux extends TileEntityTurbinePowerTap implements IEnergyProvider {

    public TileEntityTurbinePowerTapRedstoneFlux() {
        this._handler = new PowerTapRedstoneFluxHandler(this);
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

    // IEnergyConnection

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return this._handler.canConnectEnergy(from);
    }

    // IEnergyProvider

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return this._handler.extractEnergy(from, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return this._handler.getEnergyStored(from);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return this._handler.getMaxEnergyStored(from);
    }

    private PowerTapRedstoneFluxHandler _handler;
}
