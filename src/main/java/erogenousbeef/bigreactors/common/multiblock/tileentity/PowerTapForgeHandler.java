package erogenousbeef.bigreactors.common.multiblock.tileentity;

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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerTapForgeHandler extends PowerTapHandler implements IEnergyStorage {

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> CAPABILITY_FORGE_ENERGYSTORAGE = null;

    public PowerTapForgeHandler(RectangularMultiblockTileEntityBase part) {

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

        if (null == this._consumer)
            return units;

        int maxUnits = (int)Math.min(units, Integer.MAX_VALUE);
        int consumed = this._consumer.receiveEnergy(maxUnits, false);

        return maxUnits - consumed;
    }

    // IEnergyStorage

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive
     *            Maximum amount of energy to be inserted.
     * @param simulate
     *            If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract
     *            Maximum amount of energy to be extracted.
     * @param simulate
     *            If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {

        IPowerGenerator generator = this.getPowerGenerator(null);

        return null == generator ? 0 : (int)generator.extractEnergy(Math.min(maxExtract, PowerSystem.RedstoneFlux.maxCapacity), simulate);
    }

    /**
     * Returns the amount of energy currently stored.
     */
    @Override
    public int getEnergyStored() {

        IPowerGenerator generator = this.getPowerGenerator(null);

        return null == generator ? 0 : (int)Math.min(generator.getEnergyStored(), PowerSystem.RedstoneFlux.maxCapacity);
    }

    /**
     * Returns the maximum amount of energy that can be stored.
     */
    @Override
    public int getMaxEnergyStored() {

        IPowerGenerator generator = this.getPowerGenerator(null);

        return null == generator ? 0 : (int)Math.min(generator.getEnergyCapacity(), PowerSystem.RedstoneFlux.maxCapacity);
    }

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    @Override
    public boolean canExtract() {
        return true;
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    @Override
    public boolean canReceive() {
        return false;
    }

    // PowerTapHandler

    /**
     * Check for a world connection, if we're assembled.
     * @param world
     * @param position
     */
    @Override
    public void checkForConnections(IBlockAccess world, BlockPos position) {

        boolean wasConnected = this._consumer != null;
        final EnumFacing approachDirection = this._part.getOutwardFacing();

        if (null == approachDirection) {

            wasConnected = false;
            this._consumer = null;

        } else {

            // See if our adjacent non-turbine coordinate has a TE
            this._consumer = null;

            BlockPos targetPosition = position.offset(approachDirection);
            TileEntity te = world.getTileEntity(targetPosition);

            if ((null != te) && !(te instanceof IPowerProvider) && (null != CAPABILITY_FORGE_ENERGYSTORAGE) &&
                    te.hasCapability(CAPABILITY_FORGE_ENERGYSTORAGE, approachDirection))
                // Skip power taps, as they implement these APIs and we don't want to shit energy back and forth
                this._consumer = te.getCapability(CAPABILITY_FORGE_ENERGYSTORAGE, approachDirection.getOpposite());
        }

        final boolean isConnected = this._consumer != null;
        final World partWorld = this._part.getWorld();

        if (wasConnected != isConnected && WorldHelper.calledByLogicalClient(partWorld))
            WorldHelper.notifyBlockUpdate(partWorld, this._part.getWorldPosition(), null, null);
    }

    private IEnergyStorage _consumer;
}
