package erogenousbeef.bigreactors.common.multiblock.tileentity;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import erogenousbeef.bigreactors.common.multiblock.IPowerGenerator;
import erogenousbeef.bigreactors.common.multiblock.IPowerProvider;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

class PowerTapRedstoneFluxHandler extends PowerTapHandler implements IEnergyProvider {

    public PowerTapRedstoneFluxHandler(RectangularMultiblockTileEntityBase part) {

        super(part);
        this._consumer = null;
    }

    // IPowerProvider

    @Override
    public boolean isProviderConnected() {
        return null != this._consumer;
    }

    @Override
    public long onProvidePower(long units) {

        int maxUnits = (int)Math.min(units, Integer.MAX_VALUE);
        EnumFacing approachDirection = this._part.getOutwardFacing();

        if (null == this._consumer || null == approachDirection)
            return units;

        int consumed = this._consumer.receiveEnergy(approachDirection, maxUnits, false);

        return maxUnits - consumed;
    }

    // IEnergyConnection

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return null != this.getPowerGenerator(from);
    }

    // IEnergyProvider

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

        IPowerGenerator generator = this.getPowerGenerator(from);

        return null == generator ? 0 : (int)generator.extractEnergy(Math.min(maxExtract, PowerSystem.RedstoneFlux.maxCapacity), simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {

        IPowerGenerator generator = this.getPowerGenerator(from);

        return null == generator ? 0 : (int)Math.min(generator.getEnergyStored(), PowerSystem.RedstoneFlux.maxCapacity);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {

        IPowerGenerator generator = this.getPowerGenerator(from);

        return null == generator ? 0 : (int)Math.min(generator.getEnergyCapacity(), PowerSystem.RedstoneFlux.maxCapacity);
    }

    // PowerTapHandler

    /**
     * Check for a world connection, if we're assembled.
     * @param world
     * @param position
     */
    @Override
    public void checkForConnections(IBlockAccess world, BlockPos position) {

        boolean wasConnected = null != this._consumer;
        final EnumFacing approachDirection = this._part.getOutwardFacing();

        if (null == approachDirection) {

            wasConnected = false;
            this._consumer = null;

        } else {

            // See if our adjacent non-turbine coordinate has a TE
            this._consumer = null;

            BlockPos targetPosition = position.offset(approachDirection);
            TileEntity te = world.getTileEntity(targetPosition);

            if (!(te instanceof IPowerProvider) && te instanceof IEnergyReceiver) {

                // Skip power taps, as they implement these APIs and we don't want to shit energy back and forth

                IEnergyReceiver handler = (IEnergyReceiver)te;

                if (handler.canConnectEnergy(approachDirection.getOpposite()))
                    this._consumer = handler;
            }
        }

        final boolean isConnected = null != this._consumer;
        final World partWorld = this._part.getWorld();

        if (wasConnected != isConnected && WorldHelper.calledByLogicalClient(partWorld))
            // Re-render on clients
            WorldHelper.notifyBlockUpdate(partWorld, this._part.getWorldPosition(), null, null);
    }

    private IEnergyReceiver _consumer;

}
