package erogenousbeef.bigreactors.common.multiblock.computer;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import erogenousbeef.bigreactors.common.compat.IdReference;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorComputerPort;
import it.zerono.mods.zerocore.lib.compat.LuaHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = IdReference.MODID_COMPUTERCRAFT)
})
public class ReactorComputerCC extends ReactorComputer implements IPeripheral {

    public ReactorComputerCC(@Nonnull TileEntityReactorComputerPort computerPort) {
        super(computerPort);
    }

    // IPeripheral

    @Nonnull
    @Override
    @Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
    public String getType() {
        return "BigReactors-Reactor";
    }

    @Nonnull
    @Override
    @Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
    public String[] getMethodNames() {
        return METHODS_NAMES;
    }

    @Override
    @Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
    public void attach(@Nonnull IComputerAccess iComputerAccess) {
    }

    @Override
    @Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
    public void detach(@Nonnull IComputerAccess iComputerAccess) {
    }

    @Override
    @Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
    public boolean equals(@Nullable IPeripheral other) {
        return null != other && this.hashCode() == other.hashCode();
    }

    @Nullable
    @Override
    @Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
    public Object[] callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext luaContext, int methodIdx,
                               @Nonnull Object[] arguments) throws LuaException, InterruptedException {

        BlockPos coords;

        try {

            final ComputerMethod method = ComputerMethod.fromIndex(methodIdx);

            switch (method) {

                case getConnected:
                    return new Object[] { this.getConnected() };

                case getActive:
                    return new Object[] { this.getActive() };

                case getFuelTemperature:
                    return new Object[] { this.getFuelTemperature() };

                case getCasingTemperature:
                    return new Object[] { this.getCasingTemperature() };

                case getEnergyStored:
                    return new Object[] { this.getEnergyStored() };

                case getFuelAmount:
                    return new Object[] { this.getFuelAmount() };

                case getWasteAmount:
                    return new Object[] { this.getWasteAmount() };

                case getFuelAmountMax:
                    return new Object[] { this.getFuelAmountMax() };

                case getControlRodName: // Required Arg: fuel rod index

                    LuaHelper.validateArgsCount(arguments, 1);
                    return new Object[] { this.getControlRodName(LuaHelper.getIntFromArgs(arguments, 0)) };

                case getNumberOfControlRods:
                    return new Object[] { this.getNumberOfControlRods() };

                case getControlRodLevel: // Required Arg: control rod index

                    LuaHelper.validateArgsCount(arguments, 1);
                    return new Object[] { this.getControlRodLevel(LuaHelper.getIntFromArgs(arguments, 0)) };

                case getEnergyProducedLastTick:
                    return new Object[] { this.getEnergyProducedLastTick() };

                case getHotFluidProducedLastTick:
                    return new Object[] { this.getHotFluidProducedLastTick() };

                case getCoolantAmount:
                    return new Object[] { this.getCoolantAmount() };

                case getCoolantAmountMax:
                    return new Object[] { this.getCoolantAmountMax() };

                case getCoolantType:
                    return new Object[] { this.getCoolantType() };

                case getHotFluidAmount:
                    return new Object[] { this.getHotFluidAmount() };

                case getHotFluidAmountMax:
                    return new Object[] { this.getHotFluidAmountMax() };

                case getHotFluidType:
                    return new Object[] { this.getHotFluidType() };

                case getFuelReactivity:
                    return new Object[] { this.getFuelReactivity() };

                case getFuelConsumedLastTick:
                    return new Object[] { this.getFuelConsumedLastTick() };

                case getMinimumCoordinate:

                    coords = this.getMinimumCoordinate();
                    return new Object[] { coords.getX(), coords.getY(), coords.getZ() };

                case getMaximumCoordinate:

                    coords = this.getMaximumCoordinate();
                    return new Object[]{coords.getX(), coords.getY(), coords.getZ() };

                case getControlRodLocation: // Required Arg: integer (index)

                    LuaHelper.validateArgsCount(arguments, 1);
                    return new Object[] { this.getControlRodLocation(LuaHelper.getIntFromArgs(arguments, 0)) };

                case isActivelyCooled:
                    return new Object[] { this.isActivelyCooled() };

                case setActive: // Required Arg: integer (active)

                    LuaHelper.validateArgsCount(arguments, 1);
                    this.setActive(LuaHelper.getBooleanFromArgs(arguments, 0));
                    return null;

                case setControlRodLevel: // Required Args: integer (index), integer (insertion)

                    LuaHelper.validateArgsCount(arguments, 2);
                    this.setControlRodLevel(LuaHelper.getIntFromArgs(arguments, 0), LuaHelper.getIntFromArgs(arguments, 1, 0, 100));
                    return null;

                case setAllControlRodLevels: // Required Arg: integer (insertion)

                    LuaHelper.validateArgsCount(arguments, 1);
                    this.setAllControlRodLevels(LuaHelper.getIntFromArgs(arguments, 0, 0, 100));
                    return null;

                case setControlRodName: // Required Args: fuel rod index, string (name)

                    LuaHelper.validateArgsCount(arguments, 2);
                    this.setControlRodName(LuaHelper.getIntFromArgs(arguments, 0), LuaHelper.getStringFromArgs(arguments, 1));
                    return null;

                case doEjectWaste:
                    this.doEjectWaste();
                    return null;

                case doEjectFuel:
                    this.doEjectFuel();
                    return null;

                case getEnergyCapacity:
                    return new Object[] { this.getEnergyCapacity() };

                default:
                    throw new Exception("Method not implemented");
            }
        } catch(Exception ex) {

            // Rethrow errors as LuaExceptions for CC
            throw new LuaException(ex.getMessage());
        }
    }

    // internal helpers

    private enum ComputerMethod {
        getConnected,			// No arguments
        getActive,				// No arguments
        getFuelTemperature,		// No arguments
        getCasingTemperature,	// No arguments
        getEnergyStored, 		// No arguments
        getFuelAmount,  		// No arguments
        getWasteAmount, 		// No arguments
        getFuelAmountMax,		// No arguments
        getControlRodName,		// Required Arg: fuel rod index
        getNumberOfControlRods,	// No arguments
        getControlRodLevel, 	// Required Arg: control rod index
        getEnergyProducedLastTick, // No arguments
        getHotFluidProducedLastTick, // No arguments
        getCoolantAmount,		// No arguments
        getCoolantAmountMax,	// No arguments
        getCoolantType,			// No arguments
        getHotFluidAmount,		// No arguments
        getHotFluidAmountMax,	// No arguments
        getHotFluidType,		// No arguments
        getFuelReactivity,		// No arguments
        getFuelConsumedLastTick,// No arguments
        getMinimumCoordinate,	// No arguments
        getMaximumCoordinate,	// No arguments
        getControlRodLocation,	// Required Arg: integer (index)
        isActivelyCooled,		// No arguments
        setActive,				// Required Arg: integer (active)
        setControlRodLevel,		// Required Args: fuel rod index, integer (insertion)
        setAllControlRodLevels,	// Required Arg: integer (insertion)
        setControlRodName,		// Required Args: fuel rod index, string (name)
        doEjectWaste,			// No arguments
        doEjectFuel,			// No arguments
        getEnergyCapacity, 		// No arguments
        ;

        public static Boolean isValidIndex(int index) {
            return index >= 0 && index < ComputerMethod.values().length;
        }

        public static ComputerMethod fromIndex(int index) {

            if (!isValidIndex(index))
                throw new IndexOutOfBoundsException("Invalid method index");

            return ComputerMethod.values()[index];
        }
    }

    private static final String[] METHODS_NAMES;

    static {

        ComputerMethod[] methods = ComputerMethod.values();

        METHODS_NAMES = new String[methods.length];

        for (ComputerMethod method : methods)
            METHODS_NAMES[method.ordinal()] = method.toString();
    }
}