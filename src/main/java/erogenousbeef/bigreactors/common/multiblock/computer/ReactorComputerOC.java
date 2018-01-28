package erogenousbeef.bigreactors.common.multiblock.computer;

import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorComputerPort;
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

public class ReactorComputerOC extends ReactorComputer implements Environment {

    private ReactorComputerOC(@Nonnull TileEntityReactorComputerPort computerPort) {

        super(computerPort);
        this._node = Network.newNode(this, Visibility.Network).withComponent("br_reactor").create();
    }

    public static boolean isComputerCapability(Capability<?> capability) {
        return null != ENVIRONMENT_CAPABILITY && ENVIRONMENT_CAPABILITY == capability;
    }

    public static ReactorComputer create(TileEntityReactorComputerPort tileEntity) {
        return new ReactorComputerOC(tileEntity);
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
    /*
    public static class Test {
        public int i;
        public float f;
        public String s;
        public double d;
    }

    @Callback
    public Object[] getTest(Context context, Arguments arguments) throws Exception {

        Test t = new Test();
        t.i = 42;
        t.f = 144932.3343f;
        t.s = "Testing 123";
        t.d = 877339384833.454;
        return new Object[] {t};
    }*/

    @Callback(direct = true)
    public Object[] getActive(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getActive() };
    }

    @Callback(direct = true)
    public Object[] getFuelTemperature(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFuelTemperature() };
    }

    @Callback(direct = true)
    public Object[] getCasingTemperature(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getCasingTemperature() };
    }

    @Callback(direct = true)
    public Object[] getEnergyStored(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getEnergyStored() };
    }

    @Callback(direct = true)
    public Object[] getFuelAmount(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFuelAmount() };
    }

    @Callback(direct = true)
    public Object[] getWasteAmount(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getWasteAmount() };
    }

    @Callback(direct = true)
    public Object[] getFuelAmountMax(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFuelAmountMax() };
    }

    @Callback(direct = true)
    // Required Arg: fuel rod index
    public Object[] getControlRodName(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        return new Object[] { this.getControlRodName(LuaHelper.getIntFromArgs(arguments, 0)) };
    }

    @Callback(direct = true)
    public Object[] getNumberOfControlRods(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getNumberOfControlRods() };
    }

    @Callback(direct = true)
    // Required Arg: control rod index
    public Object[] getControlRodLevel(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        return new Object[] { this.getControlRodLevel(LuaHelper.getIntFromArgs(arguments, 0)) };
    }

    @Callback(direct = true)
    public Object[] getEnergyProducedLastTick(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getEnergyProducedLastTick() };
    }

    @Callback(direct = true)
    public Object[] getHotFluidProducedLastTick(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getHotFluidProducedLastTick() };
    }

    @Callback(direct = true)
    public Object[] getCoolantAmount(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getCoolantAmount() };
    }

    @Callback(direct = true)
    public Object[] getCoolantAmountMax(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getCoolantAmountMax() };
    }

    @Callback(direct = true)
    public Object[] getCoolantType(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getCoolantType() };
    }

    @Callback(direct = true)
    public Object[] getHotFluidAmount(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getHotFluidAmount() };
    }

    @Callback(direct = true)
    public Object[] getHotFluidAmountMax(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getHotFluidAmountMax() };
    }

    @Callback(direct = true)
    public Object[] getHotFluidType(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getHotFluidType() };
    }

    @Callback(direct = true)
    public Object[] getFuelReactivity(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFuelReactivity() };
    }

    @Callback(direct = true)
    public Object[] getFuelConsumedLastTick(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFuelConsumedLastTick() };
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
    // Required Arg: integer (index)
    public Object[] getControlRodLocation(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        return new Object[] { this.getControlRodLocation(LuaHelper.getIntFromArgs(arguments, 0)) };
    }

    @Callback(direct = true)
    public Object[] isActivelyCooled(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.isActivelyCooled() };
    }

    @Callback(direct = true)
    // Required Arg: integer (active)
    public Object[] setActive(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        this.setActive(LuaHelper.getBooleanFromArgs(arguments, 0));
        return null;
    }

    @Callback(direct = true)
    // Required Args: integer (index), integer (insertion)
    public Object[] setControlRodLevel(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 2);
        this.setControlRodLevel(LuaHelper.getIntFromArgs(arguments, 0), LuaHelper.getIntFromArgs(arguments, 1, 0, 100));
        return null;
    }

    @Callback(direct = true)
    // Required Arg: integer (insertion)
    public Object[] setAllControlRodLevels(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        this.setAllControlRodLevels(LuaHelper.getIntFromArgs(arguments, 0, 0, 100));
        return null;
    }

    @Callback(direct = true)
    // Required Args: fuel rod index, string (name)
    public Object[] setControlRodName(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 2);
        this.setControlRodName(LuaHelper.getIntFromArgs(arguments, 0), LuaHelper.getStringFromArgs(arguments, 1));
        return null;
    }

    @Callback(direct = true)
    public Object[] doEjectWaste(Context context, Arguments arguments) throws Exception {

        this.doEjectWaste();
        return null;
    }

    @Callback(direct = true)
    public Object[] doEjectFuel(Context context, Arguments arguments) throws Exception {

        this.doEjectFuel();
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