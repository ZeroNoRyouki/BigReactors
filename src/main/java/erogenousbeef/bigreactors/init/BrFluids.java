package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
import it.zerono.mods.zerocore.lib.fluid.ModFluid;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class BrFluids {

    // Fluids
    public static final Fluid fluidYellorium;
    public static final Fluid fluidCyanite;
    public static final Fluid fluidSteam;
    public static final Fluid fluidFuelColumn;

    public static void initialize() {
    }

    public static boolean isSameFluid(final Fluid fluid1, final Fluid fluid2) {
        return null != fluid1 && null != fluid2 && (fluid1 == fluid2 || 0 == fluid1.getName().compareToIgnoreCase(fluid2.getName()));
    }

    public static boolean isSameFluid(final FluidStack fluidStack1, final FluidStack fluidStack2) {
        return null != fluidStack1 && null != fluidStack2 && isSameFluid(fluidStack1.getFluid(), fluidStack2.getFluid());
    }

    public static boolean isFluidUsableAsSteam(final Fluid currentlyUsedSteam, final Fluid otherFluid) {
        return isSameFluid(null != currentlyUsedSteam ? currentlyUsedSteam : fluidSteam, otherFluid);
    }

    public static boolean isFluidUsableAsSteam(final FluidStack currentlyUsedSteamStack, final FluidStack otherFluidStack) {

        Fluid currentFluid = null != currentlyUsedSteamStack ? currentlyUsedSteamStack.getFluid() : null;
        Fluid otherFluid = null != otherFluidStack ? otherFluidStack.getFluid() : null;

        return isSameFluid(null != currentFluid ? currentFluid : fluidSteam, otherFluid);
    }

    static {

        Fluid fluid;

        // register fluids

        // - yellorium
        if (null == (fluid = FluidRegistry.getFluid("yellorium"))) {

            FluidRegistry.registerFluid(fluid = new ModFluid("yellorium",
                    BigReactors.createBlockResourceLocation("yelloriumstill"),
                    BigReactors.createBlockResourceLocation("yelloriumflowing")) {

                @Override
                protected void initialize() {

                    this.setDensity(100);
                    this.setGaseous(false);
                    this.setLuminosity(10);
                    this.setRarity(EnumRarity.UNCOMMON);
                    this.setTemperature(295);
                    this.setViscosity(100);
                }
            });
        }

        FluidRegistry.addBucketForFluid(fluidYellorium = fluid);

        // - cyanite
        if (null == (fluid = FluidRegistry.getFluid("cyanite"))) {

            FluidRegistry.registerFluid(fluid = new ModFluid("cyanite",
                    BigReactors.createBlockResourceLocation("cyanitestill"),
                    BigReactors.createBlockResourceLocation("cyaniteflowing")) {

                @Override
                protected void initialize() {

                    this.setDensity(100);
                    this.setGaseous(false);
                    this.setLuminosity(6);
                    this.setRarity(EnumRarity.UNCOMMON);
                    this.setTemperature(295);
                    this.setViscosity(100);
                }
            });
        }

        FluidRegistry.addBucketForFluid(fluidCyanite = fluid);

        // - steam
        if (null == (fluid = FluidRegistry.getFluid("steam"))) {

            FluidRegistry.registerFluid(fluid = new ModFluid("steam",
                    BigReactors.createBlockResourceLocation("steamstill"),
                    BigReactors.createBlockResourceLocation("steamflowing")) {

                @Override
                protected void initialize() {

                    this.setTemperature(1000); // For consistency with TE
                    this.setGaseous(true);
                    this.setLuminosity(0);
                    this.setRarity(EnumRarity.COMMON);
                    this.setDensity(6);
                }
            });
        }

        FluidRegistry.addBucketForFluid(fluidSteam = fluid);

        // - fuel column for rendering
        if (null == (fluid = FluidRegistry.getFluid("fuelcolumn"))) {

            FluidRegistry.registerFluid(fluid = new ModFluid("fuelcolumn",
                    BigReactors.createBlockResourceLocation("fuelcolumnstill"),
                    BigReactors.createBlockResourceLocation("fuelcolumnflowing")) {

                @Override
                protected void initialize() {
                }
            });
        }

        fluidFuelColumn = fluid;
    }
}
