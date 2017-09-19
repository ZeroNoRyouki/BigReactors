package erogenousbeef.bigreactors.common.multiblock.computer;

import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorComputerPort;
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

    public static ReactorComputer createCapability(TileEntityReactorComputerPort tileEntity) {
        return new ReactorComputerOC(tileEntity);
    }

    @Override
    public void onServerTick() {

        if (null != this._node && this._node.network() == null)
            API.network.joinOrCreateNetwork(this.getComputerPort());
    }

    @Override
    public void onChunkUnload() {

        if (null != this._node)
            this._node.remove();    }

    @Override
    public void onPortRemoved() {

        if (null != this._node)
            this._node.remove();
    }

    // Callbacks

    @Callback
    public Object[] getConnected(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getConnected() };
    }

    @Callback
    public Object[] getActive(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getActive() };
    }

    @Callback
    public Object[] getFuelTemperature(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFuelTemperature() };
    }

    @Callback
    public Object[] getCasingTemperature(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getCasingTemperature() };
    }

    @Callback
    public Object[] getEnergyStored(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getEnergyStored() };
    }

    @Callback
    public Object[] getFuelAmount(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFuelAmount() };
    }

    @Callback
    public Object[] getWasteAmount(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getWasteAmount() };
    }

    @Callback
    public Object[] getFuelAmountMax(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFuelAmountMax() };
    }

    @Callback
    // Required Arg: fuel rod index
    public Object[] getControlRodName(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        return new Object[] { this.getControlRodName(LuaHelper.getIntFromArgs(arguments, 0)) };
    }

    @Callback
    public Object[] getNumberOfControlRods(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getNumberOfControlRods() };
    }

    @Callback
    // Required Arg: control rod index
    public Object[] getControlRodLevel(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        return new Object[] { this.getControlRodLevel(LuaHelper.getIntFromArgs(arguments, 0)) };
    }

    @Callback
    public Object[] getEnergyProducedLastTick(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getEnergyProducedLastTick() };
    }

    @Callback
    public Object[] getHotFluidProducedLastTick(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getHotFluidProducedLastTick() };
    }

    @Callback
    public Object[] getCoolantAmount(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getCoolantAmount() };
    }

    @Callback
    public Object[] getCoolantAmountMax(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getCoolantAmountMax() };
    }

    @Callback
    public Object[] getCoolantType(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getCoolantType() };
    }

    @Callback
    public Object[] getHotFluidAmount(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getHotFluidAmount() };
    }

    @Callback
    public Object[] getHotFluidAmountMax(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getHotFluidAmountMax() };
    }

    @Callback
    public Object[] getHotFluidType(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getHotFluidType() };
    }

    @Callback
    public Object[] getFuelReactivity(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFuelReactivity() };
    }

    @Callback
    public Object[] getFuelConsumedLastTick(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.getFuelConsumedLastTick() };
    }

    @Callback
    public Object[] getMinimumCoordinate(Context context, Arguments arguments) throws Exception {

        final BlockPos coords = this.getMinimumCoordinate();

        return new Object[]{ coords.getX(), coords.getY(), coords.getZ() };
    }

    @Callback
    public Object[] getMaximumCoordinate(Context context, Arguments arguments) throws Exception {

        final BlockPos coords = this.getMaximumCoordinate();

        return new Object[]{ coords.getX(), coords.getY(), coords.getZ() };
    }

    @Callback
    // Required Arg: integer (index)
    public Object[] getControlRodLocation(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        return new Object[] { this.getControlRodLocation(LuaHelper.getIntFromArgs(arguments, 0)) };
    }

    @Callback
    public Object[] isActivelyCooled(Context context, Arguments arguments) throws Exception {
        return new Object[] { this.isActivelyCooled() };
    }

    @Callback
    // Required Arg: integer (active)
    public Object[] setActive(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        this.setActive(LuaHelper.getBooleanFromArgs(arguments, 0));
        return null;
    }

    @Callback
    // Required Args: integer (index), integer (insertion)
    public Object[] setControlRodLevel(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 2);
        this.setControlRodLevel(LuaHelper.getIntFromArgs(arguments, 0), LuaHelper.getIntFromArgs(arguments, 1, 0, 100));
        return null;
    }

    @Callback
    // Required Arg: integer (insertion)
    public Object[] setAllControlRodLevels(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 1);
        this.setAllControlRodLevels(LuaHelper.getIntFromArgs(arguments, 0, 0, 100));
        return null;
    }

    @Callback
    // Required Args: fuel rod index, string (name)
    public Object[] setControlRodName(Context context, Arguments arguments) throws Exception {

        LuaHelper.validateArgsCount(arguments, 2);
        this.setControlRodName(LuaHelper.getIntFromArgs(arguments, 0), LuaHelper.getStringFromArgs(arguments, 1));
        return null;
    }

    @Callback
    public Object[] doEjectWaste(Context context, Arguments arguments) throws Exception {

        this.doEjectWaste();
        return null;
    }

    @Callback
    public Object[] doEjectFuel(Context context, Arguments arguments) throws Exception {

        this.doEjectFuel();
        return null;
    }

    @Callback
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

    private Node _node;
}