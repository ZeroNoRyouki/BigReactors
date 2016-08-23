package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
import it.zerono.mods.zerocore.lib.fluid.ModFluid;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BrFluids {

    // Fluids
    public static final Fluid fluidYellorium;
    public static final Fluid fluidCyanite;
    public static final Fluid fluidSteam;
    public static final Fluid fluidFuelColumn;

    public static void initialize() {
    }

    static {

        Fluid fluid;

        // register fluids

        // - yellorium
        if (null == (fluid = FluidRegistry.getFluid("yellorium"))) {

            FluidRegistry.registerFluid(fluid = new ModFluid("yellorium",
                    BigReactors.createBlockResourceLocation("yelloriumStill"),
                    BigReactors.createBlockResourceLocation("yelloriumFlowing")) {

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
                    BigReactors.createBlockResourceLocation("cyaniteStill"),
                    BigReactors.createBlockResourceLocation("cyaniteFlowing")) {

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
                    BigReactors.createBlockResourceLocation("steamStill"),
                    BigReactors.createBlockResourceLocation("steamFlowing")) {

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
                    BigReactors.createBlockResourceLocation("fuelColumnStill"),
                    BigReactors.createBlockResourceLocation("fuelColumnFlowing")) {

                @Override
                protected void initialize() {
                }
            });
        }

        fluidFuelColumn = fluid;
    }
}
