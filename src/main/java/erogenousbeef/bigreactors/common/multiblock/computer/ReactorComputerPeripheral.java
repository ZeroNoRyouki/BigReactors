package erogenousbeef.bigreactors.common.multiblock.computer;

import com.google.common.collect.Maps;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorComputerPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import it.zerono.mods.zerocore.lib.compat.LuaHelper;
import it.zerono.mods.zerocore.lib.compat.computer.ComputerMethod;
import it.zerono.mods.zerocore.lib.compat.computer.MultiblockComputerPeripheral;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class ReactorComputerPeripheral extends MultiblockComputerPeripheral<TileEntityReactorComputerPort> {

    public ReactorComputerPeripheral(@Nonnull TileEntityReactorComputerPort reactorComputerPort) {
        super(reactorComputerPort);
    }

    /**
     * Get the name of this ComputerPeripheral
     *
     * @return the name
     */
    @Override
    public String getPeripheralStaticName() {
        return "extremereactor-reactorComputerPort";
    }

    /**
     * Collect the mothods provided by this ComputerPeripheral
     *
     * @param methods add your methods to this List
     */
    @Override
    public void populatePeripheralMethods(@Nonnull List<ComputerMethod> methods) {

        super.populatePeripheralMethods(methods);

        methods.add(new ComputerMethod<>("getEnergyStored", ReactorComputerPeripheral::getEnergyStored));
        methods.add(new ComputerMethod<>("getNumberOfControlRods", ReactorComputerPeripheral::getNumberOfControlRods));
        methods.add(new ComputerMethod<>("getActive", ReactorComputerPeripheral::getActive));
        methods.add(new ComputerMethod<>("getFuelTemperature", ReactorComputerPeripheral::getFuelTemperature));
        methods.add(new ComputerMethod<>("getCasingTemperature", ReactorComputerPeripheral::getCasingTemperature));
        methods.add(new ComputerMethod<>("getFuelAmount", ReactorComputerPeripheral::getFuelAmount));
        methods.add(new ComputerMethod<>("getWasteAmount", ReactorComputerPeripheral::getWasteAmount));
        methods.add(new ComputerMethod<>("getFuelAmountMax", ReactorComputerPeripheral::getFuelAmountMax));
        methods.add(new ComputerMethod<>("getControlRodName", ReactorComputerPeripheral::getControlRodName, 1));
        methods.add(new ComputerMethod<>("getControlRodLevel", ReactorComputerPeripheral::getControlRodLevel, 1));
        methods.add(new ComputerMethod<>("getEnergyProducedLastTick", ReactorComputerPeripheral::getEnergyProducedLastTick));
        methods.add(new ComputerMethod<>("getHotFluidProducedLastTick", ReactorComputerPeripheral::getHotFluidProducedLastTick));
        methods.add(new ComputerMethod<>("isActivelyCooled", ReactorComputerPeripheral::isActivelyCooled));
        methods.add(new ComputerMethod<>("getCoolantAmount", ReactorComputerPeripheral::getCoolantAmount));
        methods.add(new ComputerMethod<>("getCoolantAmountMax", ReactorComputerPeripheral::getCoolantAmountMax));
        methods.add(new ComputerMethod<>("getCoolantType", ReactorComputerPeripheral::getCoolantType));
        methods.add(new ComputerMethod<>("getHotFluidAmount", ReactorComputerPeripheral::getHotFluidAmount));
        methods.add(new ComputerMethod<>("getHotFluidAmountMax", ReactorComputerPeripheral::getHotFluidAmountMax));
        methods.add(new ComputerMethod<>("getHotFluidType", ReactorComputerPeripheral::getHotFluidType));
        methods.add(new ComputerMethod<>("getFuelReactivity", ReactorComputerPeripheral::getFuelReactivity));
        methods.add(new ComputerMethod<>("getFuelConsumedLastTick", ReactorComputerPeripheral::getFuelConsumedLastTick));
        methods.add(new ComputerMethod<>("getControlRodLocation", ReactorComputerPeripheral::getControlRodLocation, 1));
        methods.add(new ComputerMethod<>("getEnergyCapacity", ReactorComputerPeripheral::getEnergyCapacity));

        methods.add(new ComputerMethod<>("getControlRodsLevels", ReactorComputerPeripheral::getControlRodsLevels));
        methods.add(new ComputerMethod<>("setControlRodsLevels", ReactorComputerPeripheral::setControlRodsLevels, 1, true));

        methods.add(new ComputerMethod<>("getEnergyStats", ReactorComputerPeripheral::getEnergyStats));
        methods.add(new ComputerMethod<>("getFuelStats", ReactorComputerPeripheral::getFuelStats));
        methods.add(new ComputerMethod<>("getHotFluidStats", ReactorComputerPeripheral::getHotFluidStats));
        methods.add(new ComputerMethod<>("getCoolantFluidStats", ReactorComputerPeripheral::getCoolantFluidStats));

        methods.add(new ComputerMethod<>("setActive", ReactorComputerPeripheral::setActive, 1, true));
        methods.add(new ComputerMethod<>("setControlRodLevel", ReactorComputerPeripheral::setControlRodLevel, 2, true));
        methods.add(new ComputerMethod<>("setAllControlRodLevels", ReactorComputerPeripheral::setAllControlRodLevels, 1, true));
        methods.add(new ComputerMethod<>("setControlRodName", ReactorComputerPeripheral::setControlRodName, 2, true));
        methods.add(new ComputerMethod<>("doEjectWaste", ReactorComputerPeripheral::doEjectWaste, 0, true));
        methods.add(new ComputerMethod<>("doEjectFuel", ReactorComputerPeripheral::doEjectFuel, 0, true));
    }

    // Methods

    public static Object[] getEnergyStored(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getEnergyStored() };
    }

    public static Object[] getNumberOfControlRods(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getFuelRodCount() };
    }

    public static Object[] getActive(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getActive() };
    }

    public static Object[] getFuelTemperature(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getFuelHeat() };
    }

    public static Object[] getCasingTemperature(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getReactorHeat() };
    }

    public static Object[] getFuelAmount(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getFuelAmount() };
    }

    public static Object[] getWasteAmount(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getWasteAmount() };
    }

    public static Object[] getFuelAmountMax(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getCapacity() };
    }

    public static Object[] getControlRodName(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getControlRodByIndex(peripheral, LuaHelper.getIntFromArgs(arguments, 0)).getName() };
    }

    public static Object[] getControlRodLevel(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getControlRodByIndex(peripheral, LuaHelper.getIntFromArgs(arguments, 0)).getControlRodInsertion() };
    }

    public static Object[] getEnergyProducedLastTick(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getEnergyGeneratedLastTick() };
    }

    public static Object[] getHotFluidProducedLastTick(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        final MultiblockReactor reactor = getReactorControllerOrFail(peripheral);

        return new Object[] { reactor.isPassivelyCooled() ? 0f : reactor.getEnergyGeneratedLastTick() };
    }

    public static Object[] isActivelyCooled(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { !getReactorControllerOrFail(peripheral).isPassivelyCooled() };
    }

    public static Object[] getCoolantAmount(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getCoolantContainer().getCoolantAmount() };
    }

    public static Object[] getCoolantAmountMax(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getCoolantContainer().getCapacity() };
    }

    public static Object[] getCoolantType(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        final Fluid fluidType = getReactorControllerOrFail(peripheral).getCoolantContainer().getCoolantType();

        return new Object[] { null == fluidType ? null : fluidType.getName() };
    }

    public static Object[] getHotFluidAmount(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getCoolantContainer().getVaporAmount() };
    }

    public static Object[] getHotFluidAmountMax(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getCoolantContainer().getCapacity() };
    }

    public static Object[] getHotFluidType(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        final Fluid fluidType = getReactorControllerOrFail(peripheral).getCoolantContainer().getVaporType();

        return new Object[] { null == fluidType ? null : fluidType.getName() };
    }

    public static Object[] getFuelReactivity(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getFuelFertility() * 100f };
    }

    public static Object[] getFuelConsumedLastTick(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getFuelConsumedLastTick() };
    }

    // Required Args: integer (index)
    public static Object[] getControlRodLocation(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        final BlockPos reactorMinCoord = getReactorControllerOrFail(peripheral).getMinimumCoord();
        final BlockPos rodCoord = getControlRodByIndex(peripheral, LuaHelper.getIntFromArgs(arguments, 0)).getWorldPosition();
        final BlockPos coords = rodCoord.subtract(reactorMinCoord);

        return new Object[] { coords.getX(), coords.getY(), coords.getZ() };
    }

    public static Object[] getEnergyCapacity(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {
        return new Object[] { getReactorControllerOrFail(peripheral).getEnergyCapacity() };
    }

    public static Object[] getControlRodsLevels(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        final MultiblockReactor reactor = getReactorControllerOrFail(peripheral);
        /*
        final Object[] rods = new Object[reactor.getFuelRodCount()];

        for (int idx = 0; idx < rods.length; ++idx) {

            final TileEntityReactorControlRod rod = reactor.getControlRodByIndex(idx);

            rods[idx] = null != rod ? rod.getControlRodInsertion() : -1;
        }

        return new Object[] { rods };
        */

        final Map<Integer, Integer> levels = Maps.newHashMap();
        final int controlRodsCount = reactor.getFuelRodCount();

        for (int idx = 0; idx < controlRodsCount; ++idx) {

            final TileEntityReactorControlRod rod = reactor.getControlRodByIndex(idx);

            levels.put(idx, (int)(null != rod ? rod.getControlRodInsertion() : -1));
        }

        return new Object[] { levels };
    }

    // Required Arg: integer[] (levels)
    public static Object[] setControlRodsLevels(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        if (arguments[0] instanceof Map) {

            final Map levels = (Map)arguments[0];
            final MultiblockReactor reactor = getReactorControllerOrFail(peripheral);
            final int controlRodsCount = reactor.getFuelRodCount();

            if (controlRodsCount != levels.size()) {
                throw new Exception("Invalid levels count in a call to setControlRodsLevels()");
            }

            final int[] newLevels = new int[controlRodsCount];

            for (int idx = 0; idx < newLevels.length; ++idx) {

                double value;

                if (levels.containsKey((double)idx)) {

                    value = (double)levels.get((double)idx);

                } else if (levels.containsKey(idx)) {

                    value = (double)levels.get(idx);

                } else {

                    throw new Exception("Invalid table key in a call to setControlRodsLevels()");
                }

                newLevels[idx] = (int)Math.round(value);

                if (newLevels[idx] < 0 || newLevels[idx] > 100) {
                    LuaHelper.raiseIllegalArgumentRange(idx, 0, 100);
                }
            }

            for (int idx = 0; idx < newLevels.length; ++idx) {

                final TileEntityReactorControlRod rod = reactor.getControlRodByIndex(idx);

                if (null != rod) {
                    rod.setControlRodInsertion((short)newLevels[idx]);
                }
            }

            return null;
        }

        throw new Exception("Invalid parameter type in a call to setControlRodsLevels()");

        /*
        final MultiblockReactor reactor = getReactorControllerOrFail(peripheral);
        final int controlRodsCount = reactor.getFuelRodCount();

        if (controlRodsCount != arguments.length) {
            throw new Exception("Invalid levels count");
        }

        final int[] newLevels = new int[controlRodsCount];

        for (int idx = 0; idx < newLevels.length; ++idx) {
            newLevels[idx] = LuaHelper.getIntFromArgs(arguments, 1, 0, 100);
        }

        for (int idx = 0; idx < newLevels.length; ++idx) {

            final TileEntityReactorControlRod rod = reactor.getControlRodByIndex(idx);

            if (null != rod) {
                rod.setControlRodInsertion((short)newLevels[idx]);
            }
        }

        return null;
        */
    }

    public static Object[] getEnergyStats(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        final MultiblockReactor reactor = getReactorControllerOrFail(peripheral);
        final Map<String, Object> stats = Maps.newHashMap();

        stats.put("energyStored", reactor.getEnergyStored());
        stats.put("energyCapacity", reactor.getEnergyCapacity());
        stats.put("energyProducedLastTick", reactor.getEnergyGeneratedLastTick());

        return new Object[] { stats };
    }

    public static Object[] getFuelStats(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        final MultiblockReactor reactor = getReactorControllerOrFail(peripheral);
        final Map<String, Object> stats = Maps.newHashMap();

        stats.put("fuelAmount", reactor.getFuelAmount());
        stats.put("fuelCapacity", reactor.getCapacity());
        stats.put("fuelTemperature", reactor.getFuelHeat());
        stats.put("fuelConsumedLastTick", reactor.getFuelConsumedLastTick());
        stats.put("fuelReactivity", reactor.getFuelFertility() * 100f);
        stats.put("wasteAmount", reactor.getWasteAmount());

        return new Object[] { stats };
    }

    public static Object[] getHotFluidStats(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        final MultiblockReactor reactor = getReactorControllerOrFail(peripheral);
        final Fluid fluidType = reactor.getCoolantContainer().getVaporType();
        final Map<String, Object> stats = Maps.newHashMap();

        stats.put("fluidType", null == fluidType ? null : fluidType.getName());
        stats.put("fluidAmount", reactor.getCoolantContainer().getVaporAmount());
        stats.put("fluidCapacity", reactor.getCoolantContainer().getCapacity());
        stats.put("fluidProducedLastTick", reactor.isPassivelyCooled() ? 0f : reactor.getEnergyGeneratedLastTick());

        return new Object[] { stats };
    }

    public static Object[] getCoolantFluidStats(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        final MultiblockReactor reactor = getReactorControllerOrFail(peripheral);
        final Fluid fluidType = reactor.getCoolantContainer().getCoolantType();
        final Map<String, Object> stats = Maps.newHashMap();

        stats.put("fluidType", null == fluidType ? null : fluidType.getName());
        stats.put("fluidAmount", reactor.getCoolantContainer().getCoolantAmount());
        stats.put("fluidCapacity", reactor.getCoolantContainer().getCapacity());

        return new Object[] { stats };
    }


    // Required Arg: integer (active)
    public static Object[] setActive(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getReactorControllerOrFail(peripheral).setActive(LuaHelper.getBooleanFromArgs(arguments, 0));
        return null;
    }

    // Required Args: integer (index), integer (insertion)
    public static Object[] setControlRodLevel(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getControlRodByIndex(peripheral, LuaHelper.getIntFromArgs(arguments, 0))
                .setControlRodInsertion((short)LuaHelper.getIntFromArgs(arguments, 1, 0, 100));
        return null;
    }

    // Required Arg: integer (active)
    public static Object[] setAllControlRodLevels(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getReactorControllerOrFail(peripheral).setAllControlRodInsertionValues(LuaHelper.getIntFromArgs(arguments, 0, 0, 100));
        return null;
    }

    // Required Args: integer (fuel rod index), string (name)
    public static Object[] setControlRodName(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getControlRodByIndex(peripheral, LuaHelper.getIntFromArgs(arguments, 0))
                .setName(LuaHelper.getStringFromArgs(arguments, 1));
        return null;
    }

    public static Object[] doEjectWaste(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getReactorControllerOrFail(peripheral).ejectWaste(false, null);
        return null;
    }

    public static Object[] doEjectFuel(@Nonnull final ReactorComputerPeripheral peripheral, @Nonnull final Object[] arguments) throws Exception {

        getReactorControllerOrFail(peripheral).ejectFuel(false, null);
        return null;
    }

    // Internals

    private static MultiblockReactor getReactorControllerOrFail(@Nonnull final ReactorComputerPeripheral peripheral) throws Exception {

        if (!peripheral.getMultiblockPart().isConnected())
            throw new Exception("Unable to access reactor - port is not connected");

        return peripheral.getMultiblockPart().getReactorController();
    }

    private static TileEntityReactorControlRod getControlRodByIndex(@Nonnull final ReactorComputerPeripheral peripheral,
                                                                    int index) throws Exception {

        final TileEntityReactorControlRod controlRod = getReactorControllerOrFail(peripheral).getControlRodByIndex(index);

        if (null == controlRod)
            throw new IndexOutOfBoundsException(String.format("Invalid argument %d, control rod index is out of bounds", index));

        return controlRod;
    }
}