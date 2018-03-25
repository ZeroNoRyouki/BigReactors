package erogenousbeef.bigreactors.common.multiblock.computer;

import com.google.common.collect.Maps;
import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorComputerPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineComputerPort;
import it.zerono.mods.zerocore.lib.compat.LuaHelper;
import it.zerono.mods.zerocore.lib.compat.computer.ComputerMethod;
import it.zerono.mods.zerocore.lib.compat.computer.MultiblockComputerPeripheral;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class TurbineComputerPeripheral extends MultiblockComputerPeripheral<TileEntityTurbineComputerPort> {

    public TurbineComputerPeripheral(@Nonnull TileEntityTurbineComputerPort turbineComputerPort) {
        super(turbineComputerPort);
    }

    /**
     * Get the name of this ComputerPeripheral
     *
     * @return the name
     */
    @Override
    public String getPeripheralStaticName() {
        return "extremereactor-turbineComputerPort";
    }

    /**
     * Collect the mothods provided by this ComputerPeripheral
     *
     * @param methods add your methods to this List
     */
    @Override
    public void populatePeripheralMethods(@Nonnull List<ComputerMethod> methods) {

        super.populatePeripheralMethods(methods);

        methods.add(new ComputerMethod<>("getActive", TurbineComputerPeripheral::getActive));
        methods.add(new ComputerMethod<>("getEnergyProducedLastTick", TurbineComputerPeripheral::getEnergyProducedLastTick));
        methods.add(new ComputerMethod<>("getEnergyStored", TurbineComputerPeripheral::getEnergyStored));
        methods.add(new ComputerMethod<>("getFluidAmountMax", TurbineComputerPeripheral::getFluidAmountMax));
        methods.add(new ComputerMethod<>("getFluidFlowRate", TurbineComputerPeripheral::getFluidFlowRate));
        methods.add(new ComputerMethod<>("getFluidFlowRateMax", TurbineComputerPeripheral::getFluidFlowRateMax));
        methods.add(new ComputerMethod<>("getFluidFlowRateMaxMax", TurbineComputerPeripheral::getFluidFlowRateMaxMax));
        methods.add(new ComputerMethod<>("getInputAmount", TurbineComputerPeripheral::getInputAmount));
        methods.add(new ComputerMethod<>("getInputType", TurbineComputerPeripheral::getInputType));
        methods.add(new ComputerMethod<>("getOutputAmount", TurbineComputerPeripheral::getOutputAmount));
        methods.add(new ComputerMethod<>("getOutputType", TurbineComputerPeripheral::getOutputType));
        methods.add(new ComputerMethod<>("getRotorSpeed", TurbineComputerPeripheral::getRotorSpeed));
        methods.add(new ComputerMethod<>("getNumberOfBlades", TurbineComputerPeripheral::getNumberOfBlades));
        methods.add(new ComputerMethod<>("getBladeEfficiency", TurbineComputerPeripheral::getBladeEfficiency));
        methods.add(new ComputerMethod<>("getRotorMass", TurbineComputerPeripheral::getRotorMass));
        methods.add(new ComputerMethod<>("getInductorEngaged", TurbineComputerPeripheral::getInductorEngaged));
        methods.add(new ComputerMethod<>("getEnergyCapacity", TurbineComputerPeripheral::getEnergyCapacity));
        methods.add(new ComputerMethod<>("getEnergyStats", TurbineComputerPeripheral::getEnergyStats));

        methods.add(new ComputerMethod<>("setActive", TurbineComputerPeripheral::setActive, 1, true));
        methods.add(new ComputerMethod<>("setFluidFlowRateMax", TurbineComputerPeripheral::setFluidFlowRateMax, 1, true));
        methods.add(new ComputerMethod<>("setVentNone", TurbineComputerPeripheral::setVentNone, 0, true));
        methods.add(new ComputerMethod<>("setVentOverflow", TurbineComputerPeripheral::setVentOverflow, 0, true));
        methods.add(new ComputerMethod<>("setVentAll", TurbineComputerPeripheral::setVentAll, 0, true));
        methods.add(new ComputerMethod<>("setInductorEngaged", TurbineComputerPeripheral::setInductorEngaged, 1, true));
    }

    // Methods

    public static Object[] getActive(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getActive() };
    }

    public static Object[] getEnergyProducedLastTick(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getEnergyGeneratedLastTick() };
    }

    public static Object[] getEnergyStored(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getEnergyStored() };
    }

    public static Object[] getFluidAmountMax(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { MultiblockTurbine.TANK_SIZE };
    }

    public static Object[] getFluidFlowRate(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getFluidConsumedLastTick() };
    }

    public static Object[] getFluidFlowRateMax(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getMaxIntakeRate() };
    }

    public static Object[] getFluidFlowRateMaxMax(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getMaxIntakeRateMax() };
    }

    public static Object[] getInputAmount(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getFluidAmount(peripheral, IInputOutputPort.Direction.Input) };
    }

    public static Object[] getInputType(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getFluidName(peripheral, IInputOutputPort.Direction.Input) };
    }

    public static Object[] getOutputAmount(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getFluidAmount(peripheral, IInputOutputPort.Direction.Output) };
    }

    public static Object[] getOutputType(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getFluidName(peripheral, IInputOutputPort.Direction.Output) };
    }

    public static Object[] getRotorSpeed(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getRotorSpeed() };
    }

    public static Object[] getNumberOfBlades(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getNumRotorBlades() };
    }

    public static Object[] getBladeEfficiency(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getRotorEfficiencyLastTick() * 100f };
    }

    public static Object[] getRotorMass(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getRotorMass() };
    }

    public static Object[] getInductorEngaged(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getInductorEngaged() };
    }

    public static Object[] getEnergyCapacity(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getTurbineControllerOrFail(peripheral).getEnergyCapacity() };
    }

    public static Object[] getEnergyStats(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        final MultiblockTurbine turbine = getTurbineControllerOrFail(peripheral);
        final Map<String, Object> stats = Maps.newHashMap();

        stats.put("energyStored", turbine.getEnergyStored());
        stats.put("energyCapacity", turbine.getEnergyCapacity());
        stats.put("energyProducedLastTick", turbine.getEnergyGeneratedLastTick());

        return new Object[] { stats };
    }

    // Required Arg: boolean (active)
    public static Object[] setActive(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getTurbineControllerOrFail(peripheral).setActive(LuaHelper.getBooleanFromArgs(arguments, 0));
        return null;
    }

    // Required Arg: integer (rate)
    public static Object[] setFluidFlowRateMax(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getTurbineControllerOrFail(peripheral).setMaxIntakeRate(LuaHelper.getIntFromArgs(arguments, 0));
        return null;
    }

    public static Object[] setVentNone(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getTurbineControllerOrFail(peripheral).setVentStatus(MultiblockTurbine.VentStatus.DoNotVent, true);
        return null;
    }

    public static Object[] setVentOverflow(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getTurbineControllerOrFail(peripheral).setVentStatus(MultiblockTurbine.VentStatus.VentOverflow, true);
        return null;
    }

    public static Object[] setVentAll(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getTurbineControllerOrFail(peripheral).setVentStatus(MultiblockTurbine.VentStatus.VentAll, true);
        return null;
    }

    // Required Arg: boolean (engaged)
    public static Object[] setInductorEngaged(@Nonnull final TurbineComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getTurbineControllerOrFail(peripheral).setInductorEngaged(LuaHelper.getBooleanFromArgs(arguments, 0), true);
        return null;
    }

    // Internals

    private static MultiblockTurbine getTurbineControllerOrFail(@Nonnull final TurbineComputerPeripheral peripheral) throws Exception {

        if (!peripheral.getMultiblockPart().isConnected())
            throw new Exception("Unable to access turbine - port is not connected");

        return peripheral.getMultiblockPart().getTurbine();
    }

    @Nullable
    private static IFluidTankProperties getTankProperties(@Nonnull final TurbineComputerPeripheral peripheral,
                                                          @Nonnull final IInputOutputPort.Direction direction) throws Exception {

        final MultiblockTurbine turbine = getTurbineControllerOrFail(peripheral);
        final IFluidTankProperties[] properties = turbine.getFluidHandler(direction).getTankProperties();

        return null != properties && properties.length > 0 ? properties[0] : null;
    }

    private static int getFluidAmount(@Nonnull final TurbineComputerPeripheral peripheral,
                                      @Nonnull final IInputOutputPort.Direction direction) throws Exception {

        final IFluidTankProperties properties = getTankProperties(peripheral, direction);
        final FluidStack stack = null != properties ? properties.getContents() : null;

        return null != stack ? stack.amount : 0;
    }

    @Nullable
    private static String getFluidName(@Nonnull final TurbineComputerPeripheral peripheral,
                                       @Nonnull final IInputOutputPort.Direction direction) throws Exception {

        final IFluidTankProperties properties = getTankProperties(peripheral, direction);
        final FluidStack stack = null != properties ? properties.getContents() : null;
        final Fluid fluid = null != stack ? stack.getFluid() : null;

        return null != fluid ? fluid.getName() : null;
    }
}