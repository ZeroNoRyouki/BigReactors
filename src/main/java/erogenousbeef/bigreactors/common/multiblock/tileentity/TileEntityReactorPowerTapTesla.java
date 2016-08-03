package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.lib.BlockFacings;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Optional;

public class TileEntityReactorPowerTapTesla extends TileEntityReactorPowerTap {

    public TileEntityReactorPowerTapTesla() {

        this._consumer = null;
        this._handler = null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

        if (((null != CAPABILITY_TESLA_PRODUCER && CAPABILITY_TESLA_PRODUCER == capability) ||
                (null != CAPABILITY_TESLA_HOLDER && CAPABILITY_TESLA_HOLDER == capability)) && (null != this._handler))
            return true;

        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

        if (null != this._handler) {

            if ((null != CAPABILITY_TESLA_PRODUCER) && (CAPABILITY_TESLA_PRODUCER == capability))
                return CAPABILITY_TESLA_PRODUCER.cast(this._handler);

            if ((null != CAPABILITY_TESLA_HOLDER) && (CAPABILITY_TESLA_HOLDER == capability))
                return CAPABILITY_TESLA_HOLDER.cast(this._handler);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void onMachineAssembled(MultiblockControllerBase multiblockController) {

        super.onMachineAssembled(multiblockController);

        if (multiblockController instanceof MultiblockReactor)
            this._handler = new TeslaHandler((MultiblockReactor)multiblockController);
    }

    @Override
    public void onMachineBroken() {

        super.onMachineBroken();
        this._handler = null;
    }

    @Override
    public boolean hasEnergyConnection() {
        return null != this._consumer;
    }

    /**
     * Check for a world connection, if we're assembled.
     * @param world
     * @param position
     */
    @Override
    protected void checkForConnections(IBlockAccess world, BlockPos position) {

        boolean wasConnected = this._consumer != null;
        BlockFacings out = this.getOutwardsDir();

        if (out.none()) {

            wasConnected = false;
            this._consumer = null;

        } else if (1 == out.countFacesIf(true)) {

            // See if our adjacent non-reactor coordinate has a TE
            this._consumer = null;

            TileEntity te = world.getTileEntity(out.offsetBlockPos(this.getWorldPosition()));
            EnumFacing consumerFacing = out.firstIf(true).getOpposite();

            if ((null != te) && (null != CAPABILITY_TESLA_CONSUMER) && te.hasCapability(CAPABILITY_TESLA_CONSUMER, consumerFacing)) {

                this._consumer = te.getCapability(CAPABILITY_TESLA_CONSUMER, consumerFacing);
            }
        }

        boolean isConnected = this._consumer != null;

        if (wasConnected != isConnected)
            WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
    }

    @Override
    public long onProvidePower(long units) {

        if (null == this._consumer)
            return units;

        long energyAccepted = this._consumer.givePower(units, false);

        units -= energyAccepted;

        return units;
    }

    private ITeslaConsumer _consumer;
    private TeslaHandler _handler;

    @CapabilityInject(ITeslaConsumer.class)
    private static Capability<ITeslaConsumer> CAPABILITY_TESLA_CONSUMER = null;

    @CapabilityInject(ITeslaProducer.class)
    private static Capability<ITeslaProducer> CAPABILITY_TESLA_PRODUCER = null;

    @CapabilityInject(ITeslaHolder.class)
    private static Capability<ITeslaHolder> CAPABILITY_TESLA_HOLDER = null;

    @Optional.InterfaceList({
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "Tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "Tesla")
    })
    private static class TeslaHandler implements ITeslaHolder, ITeslaProducer {

        public TeslaHandler(MultiblockReactor reactor) {
            this._reactor = reactor;
        }

        @Override
        public long getStoredPower() {
            return this._reactor.getEnergyStored();
        }

        @Override
        public long getCapacity() {
            return this._reactor.getEnergyCapacity();
        }

        @Override
        public long takePower(long power, boolean simulated) {
            return this._reactor.extractEnergy(power, simulated);
        }

        private MultiblockReactor _reactor;
    }
}