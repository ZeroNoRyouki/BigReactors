package erogenousbeef.bigreactors.common.multiblock.tileentity;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import it.zerono.mods.zerocore.lib.BlockFacings;
import it.zerono.mods.zerocore.util.WorldHelper;

public class TileEntityReactorPowerTapRedstoneFlux extends TileEntityReactorPowerTap implements IEnergyProvider {

    public TileEntityReactorPowerTapRedstoneFlux() {
        this._rfNetwork = null;
    }

    @Override
    public boolean hasEnergyConnection() {
        return null != this._rfNetwork;
    }

    /**
     * Check for a world connection, if we're assembled.
     * @param world
     * @param position
     */
    @Override
    protected void checkForConnections(IBlockAccess world, BlockPos position) {

        boolean wasConnected = this._rfNetwork != null;
        BlockFacings out = this.getOutwardsDir();

        if (out.none()) {

            wasConnected = false;
            this._rfNetwork = null;

        } else if (1 == out.countFacesIf(true)) {

            // See if our adjacent non-reactor coordinate has a TE
            this._rfNetwork = null;

            TileEntity te = world.getTileEntity(out.offsetBlockPos(this.getWorldPosition()));

            // Skip power taps, as they implement these APIs and we don't want to shit energy back and forth
            if (!(te instanceof TileEntityReactorPowerTap) && (te instanceof IEnergyReceiver)) {

                IEnergyReceiver handler = (IEnergyReceiver)te;

                if(handler.canConnectEnergy(out.firstIf(true).getOpposite()))
                    this._rfNetwork = handler;
            }
        }

        boolean isConnected = this._rfNetwork != null;

        if (wasConnected != isConnected)
            WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
    }

    @Override
    public long onProvidePower(long units) {

        int cappedUnits = units > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)units;
        BlockFacings out = this.getOutwardsDir();

        if ((this._rfNetwork == null) || (1 != out.countFacesIf(true)))
            return units;

        EnumFacing approachDirection = out.firstIf(true).getOpposite();
        int energyConsumed = this._rfNetwork.receiveEnergy(approachDirection, cappedUnits, false);

        units -= energyConsumed;

        return units;
    }

    // IEnergyConnection
    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return null != this.getReactor(from);
    }

    // IEnergyProvider
    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

        MultiblockReactor reactor = this.getReactor(from);

        return null == reactor ? 0 : (int)reactor.extractEnergy(Math.min(maxExtract, PowerSystem.RedstoneFlux.maxCapacity), simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {

        MultiblockReactor reactor = this.getReactor(from);

        return null == reactor ? 0 : (int)Math.min(reactor.getEnergyStored(), PowerSystem.RedstoneFlux.maxCapacity);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {

        MultiblockReactor reactor = this.getReactor(from);

        return null == reactor ? 0 : (int)Math.min(reactor.getEnergyCapacity(), PowerSystem.RedstoneFlux.maxCapacity);
    }

    private MultiblockReactor getReactor(EnumFacing facing) {

        if (!this.isConnected() || !this.getOutwardsDir().isSet(facing))
            return null;

        MultiblockReactor reactor = this.getReactorController();

        return reactor.isAssembled() ? reactor : null;
    }

    private IEnergyReceiver _rfNetwork;
}
