package erogenousbeef.bigreactors.common.multiblock.tileentity;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityReactorPowerTapRedstoneFlux extends TileEntityReactorPowerTap implements IEnergyProvider {

    public TileEntityReactorPowerTapRedstoneFlux() {

        this._rfHandler = new PowerTapRedstoneFluxHandler(this);
        this._forgeHandler = new PowerTapForgeHandler(this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return ((null != CAPAP_FORGE_ENERGYSTORAGE) && (CAPAP_FORGE_ENERGYSTORAGE == capability))
                || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

        if ((null != CAPAP_FORGE_ENERGYSTORAGE) && (CAPAP_FORGE_ENERGYSTORAGE == capability))
            return CAPAP_FORGE_ENERGYSTORAGE.cast(this._forgeHandler);

        return super.getCapability(capability, facing);
    }

    // IPowerProvider

    @Override
    public boolean isProviderConnected() {
        return this._forgeHandler.isProviderConnected() || this._rfHandler.isProviderConnected();
    }

    @Override
    public long onProvidePower(long units) {

        if (this._forgeHandler.isProviderConnected())
            return this._forgeHandler.onProvidePower(units);

        if (this._rfHandler.isProviderConnected())
            return this._rfHandler.onProvidePower(units);

        return units;
    }

    // TileEntityReactorPowerTap

    @Override
    protected void checkForConnections(IBlockAccess world, BlockPos position) {

        // try to link to the Forge capab first
        this._forgeHandler.checkForConnections(world, position);

        // if no connection was made, try RF next
        if (!this._forgeHandler.isProviderConnected())
            this._rfHandler.checkForConnections(world, position);
    }

    // IEnergyConnection (RF)

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return this._rfHandler.canConnectEnergy(from);
    }

    // IEnergyProvider (RF)

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return this._rfHandler.extractEnergy(from, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return this._rfHandler.getEnergyStored(from);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return this._rfHandler.getMaxEnergyStored(from);
    }

    @CapabilityInject(IEnergyStorage.class)
    private static Capability<IEnergyStorage> CAPAP_FORGE_ENERGYSTORAGE = null;

    private final PowerTapRedstoneFluxHandler _rfHandler;
    private final PowerTapForgeHandler _forgeHandler;
}