package erogenousbeef.bigreactors.common.multiblock.computer;

import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineComputerPort;
import it.zerono.mods.zerocore.lib.block.ModTileEntity;
import it.zerono.mods.zerocore.lib.compat.LuaHelper;
import li.cil.oc.api.API;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nonnull;

public class TurbineComputerOC extends TurbineComputer implements Environment {

    private TurbineComputerOC(@Nonnull TileEntityTurbineComputerPort computerPort) {

        super(computerPort);
        this._node = Network.newNode(this, Visibility.Network).withComponent("br_turbine").create();
    }

    public static boolean isComputerCapability(Capability<?> capability) {
        return null != ENVIRONMENT_CAPABILITY && ENVIRONMENT_CAPABILITY == capability;
    }

    public static TurbineComputer create(TileEntityTurbineComputerPort tileEntity) {
        return new TurbineComputerOC(tileEntity);
    }

    @Override
    public void onAttachedToController() {

        if (null != this._node && this._node.network() == null) {

            API.network.joinOrCreateNetwork(this.getComputerPort());
            this.getComputerPort().markDirty();
        }
    }

    @Override
    public void onDetachedFromController() {

        if (null != this._node)
            this._node.remove();
    }

    @Override
    public void syncDataFrom(NBTTagCompound data, ModTileEntity.SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);

        if (null != this.node() && data.hasKey(NODE_TAG))
            this.node().load(data.getCompoundTag(NODE_TAG));
    }

    @Override
    public void syncDataTo(NBTTagCompound data, ModTileEntity.SyncReason syncReason) {

        super.syncDataTo(data, syncReason);

        // let's do this like OC' AbstractManagedEnvironment do ...

        if (this.node() != null) {

            // Force joining a network when saving and we're not in one yet, so that
            // the address is embedded in the saved data that gets sent to the client,
            // so that that address can be used to associate components on server and
            // client (for example keyboard and screen/text buffer).

            if (this.node().address() == null) {

                li.cil.oc.api.Network.joinNewNetwork(this.node());

                final NBTTagCompound nodeTag = new NBTTagCompound();

                this.node().save(nodeTag);
                data.setTag(NODE_TAG, nodeTag);

                this.node().remove();

            } else {

                final NBTTagCompound nodeTag = new NBTTagCompound();

                this.node().save(nodeTag);
                data.setTag(NODE_TAG, nodeTag);
            }
        }
    }

    // Callbacks - Multiblock

    @Callback(direct = true)
    public Object[] getMultiblockAssembled(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getMultiblockAssembled() };
    }

    @Callback(direct = true)
    public Object[] getConnected(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getConnected() };
    }

    // Callbacks - Reactor

    @Callback(direct = true)
    public Object[] getActive(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getActive() };
    }

    @Callback(direct = true)
    public Object[] getEnergyProducedLastTick(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getEnergyProducedLastTick() };
    }

    @Callback(direct = true)
    public Object[] getEnergyStored(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getEnergyStored() };
    }

    @Callback(direct = true)
    public Object[] getFluidAmountMax(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFluidAmountMax() };
    }

    @Callback(direct = true)
    public Object[] getFluidFlowRate(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFluidFlowRate() };
    }

    @Callback(direct = true)
    public Object[] getFluidFlowRateMax(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFluidFlowRateMax() };
    }

    @Callback(direct = true)
    public Object[] getFluidFlowRateMaxMax(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFluidFlowRateMaxMax() };
    }

    @Callback(direct = true)
    public Object[] getInputAmount(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getInputAmount() };
    }

    @Callback(direct = true)
    public Object[] getInputType(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getInputType() };
    }

    @Callback(direct = true)
    public Object[] getOutputAmount(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getOutputAmount() };
    }

    @Callback(direct = true)
    public Object[] getOutputType(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getOutputType() };
    }

    @Callback(direct = true)
    public Object[] getRotorSpeed(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getRotorSpeed() };
    }

    @Callback(direct = true)
    public Object[] getNumberOfBlades(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getNumberOfBlades() };
    }

    @Callback(direct = true)
    public Object[] getBladeEfficiency(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getBladeEfficiency() };
    }

    @Callback(direct = true)
    public Object[] getRotorMass(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getRotorMass() };
    }

    @Callback(direct = true)
    public Object[] getInductorEngaged(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getInductorEngaged() };
    }

    @Callback(direct = true)
    public Object[] getMinimumCoordinate(Context context, Arguments arguments) throws Exception {

        final BlockPos coords = this.getMinimumCoordinate();

        return new Object[]{ coords.getX(), coords.getY(), coords.getZ() };
    }

    @Callback(direct = true)
    public Object[] getMaximumCoordinate(Context context, Arguments arguments) throws Exception {

        final BlockPos coords = this.getMaximumCoordinate();

        return new Object[]{ coords.getX(), coords.getY(), coords.getZ() };
    }

    @Callback(direct = true)
    // Required Arg: boolean (active)
    public Object[] setActive(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        this.setActive(LuaHelper.getBooleanFromArgs(arguments, 0));
        return null;
    }

    @Callback(direct = true)
    // Required Arg: integer (active)
    public Object[] setFluidFlowRateMax(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        this.setFluidFlowRateMax(LuaHelper.getIntFromArgs(arguments, 0));
        return null;
    }

    @Callback(direct = true)
    public Object[] setVentNone(Context context, Arguments arguments) throws Exception {

        this.setVentNone();
        return null;
    }

    @Callback(direct = true)
    public Object[] setVentOverflow(Context context, Arguments arguments) throws Exception {

        this.setVentOverflow();
        return null;
    }

    @Callback(direct = true)
    public Object[] setVentAll(Context context, Arguments arguments) throws Exception {

        this.setVentAll();
        return null;
    }

    @Callback(direct = true)
    public Object[] setInductorEngaged(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        this.setInductorEngaged(LuaHelper.getBooleanFromArgs(arguments, 0));
        return null;
    }

    @Callback(direct = true)
    public Object[] getEnergyCapacity(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getEnergyCapacity() };
    }

    // Environment

    @Override
    public Node node() {
        return this._node;
    }

    @Override
    public void onConnect(Node node) {
    }

    @Override
    public void onDisconnect(Node node) {
    }

    @Override
    public void onMessage(Message message) {
    }

    @CapabilityInject(Environment.class)
    private static Capability<Environment> ENVIRONMENT_CAPABILITY = null;

    private final Node _node;

    private static final String NODE_TAG = "ocNode";
}