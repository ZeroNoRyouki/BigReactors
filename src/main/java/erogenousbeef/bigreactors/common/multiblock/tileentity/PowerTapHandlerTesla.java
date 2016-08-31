package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.IPowerGenerator;
import erogenousbeef.bigreactors.common.multiblock.IPowerProvider;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla")
})
class PowerTapHandlerTesla extends PowerTapHandler implements ITeslaHolder, ITeslaProducer {

    @CapabilityInject(ITeslaConsumer.class)
    public static Capability<ITeslaConsumer> CAPABILITY_TESLA_CONSUMER = null;

    @CapabilityInject(ITeslaProducer.class)
    public static Capability<ITeslaProducer> CAPABILITY_TESLA_PRODUCER = null;

    @CapabilityInject(ITeslaHolder.class)
    public static Capability<ITeslaHolder> CAPABILITY_TESLA_HOLDER = null;

    public PowerTapHandlerTesla(RectangularMultiblockTileEntityBase part) {

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

        long energyAccepted = this._consumer.givePower(units, false);

        return units - energyAccepted;
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

            if ((null != te) && !(te instanceof IPowerProvider) && (null != CAPABILITY_TESLA_CONSUMER) && te.hasCapability(CAPABILITY_TESLA_CONSUMER, approachDirection))
                // Skip power taps, as they implement these APIs and we don't want to shit energy back and forth
                this._consumer = te.getCapability(CAPABILITY_TESLA_CONSUMER, approachDirection.getOpposite());
        }

        final boolean isConnected = this._consumer != null;
        final World partWorld = this._part.getWorld();

        if (wasConnected != isConnected && WorldHelper.calledByLogicalClient(partWorld))
            WorldHelper.notifyBlockUpdate(partWorld, this._part.getWorldPosition(), null, null);
    }

    // ITeslaHolder

    @Override
    public long getStoredPower() {

        IPowerGenerator generator = this.getPowerGenerator(null);

        return null == generator ? 0 : Math.min(generator.getEnergyStored(), PowerSystem.Tesla.maxCapacity);
    }

    @Override
    public long getCapacity() {

        IPowerGenerator generator = this.getPowerGenerator(null);

        return null == generator ? 0 : Math.min(generator.getEnergyCapacity(), PowerSystem.Tesla.maxCapacity);
    }

    // ITeslaProducer

    @Override
    public long takePower(long power, boolean simulated) {

        IPowerGenerator generator = this.getPowerGenerator(null);

        return null == generator ? 0 : generator.extractEnergy(Math.min(power, PowerSystem.Tesla.maxCapacity), simulated);
    }

    private ITeslaConsumer _consumer;
}
