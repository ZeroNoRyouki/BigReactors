package erogenousbeef.bigreactors.common.multiblock.computer;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import erogenousbeef.bigreactors.common.compat.IdReference;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineComputerPort;
import it.zerono.mods.zerocore.lib.compat.LuaHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = IdReference.MODID_COMPUTERCRAFT)
})
public class TurbineComputerCC extends TurbineComputer implements IPeripheral {

    private TurbineComputerCC(@Nonnull TileEntityTurbineComputerPort computerPort) {
        super(computerPort);
    }

    public static TurbineComputerCC create(TileEntityTurbineComputerPort tileEntity) {
        return new TurbineComputerCC(tileEntity);
    }

    // IPeripheral

    @Nonnull
    @Override
    @Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
    public String getType() {
        return "BigReactors-Turbine";
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

                case getEnergyStored:
                    return new Object[] { this.getEnergyStored() };

                case getRotorSpeed:
                    return new Object[] { this.getRotorSpeed() };

                case getInputAmount:
                    return new Object[] { this.getInputAmount() };

                case getInputType:
                    return new Object[] { this.getInputType() };

                case getOutputAmount:
                    return new Object[] { this.getOutputAmount() };

                case getOutputType:
                    return new Object[] { this.getOutputType() };

                case getFluidAmountMax:
                    return new Object[] { this.getFluidAmountMax() };

                case getFluidFlowRate:
                    return new Object[] { this.getFluidFlowRate() };

                case getFluidFlowRateMax:
                    return new Object[] { this.getFluidFlowRateMax() };

                case getFluidFlowRateMaxMax:
                    return new Object[] { this.getFluidFlowRateMaxMax() };

                case getEnergyProducedLastTick:
                    return new Object[] { this.getEnergyProducedLastTick() };

                case getNumberOfBlades:
                    return new Object[] { this.getNumberOfBlades() };

                case getBladeEfficiency:
                    return new Object[] { this.getBladeEfficiency() };

                case getRotorMass:
                    return new Object[] { this.getRotorMass() };

                case getInductorEngaged:
                    return new Object[] { this.getInductorEngaged() };

                case getMaximumCoordinate:

                    coords = this.getMaximumCoordinate();
                    return new Object[] { coords.getX(), coords.getY(), coords.getZ() };

                case getMinimumCoordinate:

                    coords = this.getMinimumCoordinate();
                    return new Object[]{ coords.getX(), coords.getY(), coords.getZ() };

                case setActive: // Required Arg: boolean (active)

                    LuaHelper.validateArgsCount(arguments, 1);
                    this.setActive(LuaHelper.getBooleanFromArgs(arguments, 0));
                    return null;

                case setFluidFlowRateMax: // Required Arg: integer (active)

                    LuaHelper.validateArgsCount(arguments, 1);
                    this.setFluidFlowRateMax(LuaHelper.getIntFromArgs(arguments, 0));
                    return null;

                case setVentNone:
                    this.setVentNone();
                    return null;

                case setVentOverflow:
                    this.setVentOverflow();
                    return null;

                case setVentAll:
                    this.setVentAll();
                    return null;

                case setInductorEngaged: // Required Arg: boolean (active)

                    LuaHelper.validateArgsCount(arguments, 1);
                    this.setInductorEngaged(LuaHelper.getBooleanFromArgs(arguments, 0));
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
        getEnergyStored, 		// No arguments
        getRotorSpeed,			// No arguments
        getInputAmount,  		// No arguments
        getInputType,			// No arguments
        getOutputAmount, 		// No arguments
        getOutputType,			// No arguments
        getFluidAmountMax,		// No arguments
        getFluidFlowRate,		// No arguments
        getFluidFlowRateMax,	// No arguments
        getFluidFlowRateMaxMax, // No arguments
        getEnergyProducedLastTick, // No arguments
        getNumberOfBlades,		// No arguments
        getBladeEfficiency,		// No arguments
        getRotorMass,			// No arguments
        getInductorEngaged,		// No arguments
        getMaximumCoordinate,	// No arguments
        getMinimumCoordinate,	// No arguments
        setActive,				// Required Arg: integer (active)
        setFluidFlowRateMax,	// Required Arg: integer (active)
        setVentNone,			// No arguments
        setVentOverflow,		// No arguments
        setVentAll,				// No arguments
        setInductorEngaged,		// Required Arg: integer (active)
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