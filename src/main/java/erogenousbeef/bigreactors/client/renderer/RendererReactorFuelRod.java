package erogenousbeef.bigreactors.client.renderer;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.helpers.FuelAssembly;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;
import erogenousbeef.bigreactors.init.BrFluids;
import it.zerono.mods.zerocore.lib.BlockFacings;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RendererReactorFuelRod extends TileEntitySpecialRenderer<TileEntityReactorFuelRod> {

    @Override
    public void renderTileEntityAt(TileEntityReactorFuelRod rod, double x, double y, double z, float partialTicks, int destroyStage) {

        final FuelAssembly assembly = rod.getFuelAssembly();
        final MultiblockReactor reactor = rod.getReactorController();

        if (null == reactor || !reactor.isAssembled() || null == assembly)
            return;

        final World world = this.getWorld();
        final BlockPos rodPosition = rod.getWorldPosition();
        final int rodsCount = assembly.getFueldRodsCount();
        final EnumFacing.Axis axis = assembly.getAxis();
        BlockFacings facesToDraw;

        final Fluid fluid = BrFluids.fluidFuelColumn;
        final int fuelColor = assembly.getFuelColor();
        final int wasteColor = assembly.getWasteColor();

        final float rodCapacity = MultiblockReactor.FuelCapacityPerFuelRod;
        float wasteHight, fuelHight;

        if (EnumFacing.Axis.Y == axis) {

            final int rodIndex = rodPosition.getY() - reactor.getMinimumCoord().getY() - 1;
            final FuelAssembly.FuelRodData rodData = assembly.getFuelRodData(rodIndex);
            final boolean gotFuel, gotWaste;
            int brightness;

            if (null == rodData)
                return;

            facesToDraw = BlockFacings.ALL;

            wasteHight = rodData.getWasteAmount() / rodCapacity;
            fuelHight = rodData.getFuelAmount() / rodCapacity;

            gotWaste = wasteHight > 0.0f;
            gotFuel = fuelHight > 0.0f;

            if (gotWaste && gotFuel) {

                // both waste & fuel

                final float offset = MathHelper.sin((world.getTotalWorldTime() + partialTicks) * 0.1f) * 0.01f;

                brightness = world.getCombinedLight(rodPosition, fluid.getLuminosity());

                // - waste
                facesToDraw = facesToDraw.set(EnumFacing.DOWN, 0 == rodIndex);
                facesToDraw = facesToDraw.set(EnumFacing.UP, false);

                wasteHight += offset;
                ModRenderHelper.renderFluidCube(fluid, facesToDraw, x, y, z,
                        0.005, 0.0, 0.005,
                        0.995, wasteHight, 0.995,
                        wasteColor, brightness);

                // - fuel
                facesToDraw = facesToDraw.set(EnumFacing.DOWN, false);
                facesToDraw = facesToDraw.set(EnumFacing.UP,
                        (rodsCount -1 == rodIndex) ||
                        (rodData.getFuelAmount() + (rodCapacity * rodIndex) >= assembly.getFuelQuota()));

                fuelHight -= offset;
                ModRenderHelper.renderFluidCube(fluid, facesToDraw, x, y + wasteHight, z,
                        0.005, 0.0, 0.005,
                        0.995, fuelHight, 0.995,
                        fuelColor, brightness);

            } else if (gotWaste) {

                // only waste

                facesToDraw = facesToDraw.set(EnumFacing.DOWN, 0 == rodIndex);
                facesToDraw = facesToDraw.set(EnumFacing.UP,
                                (rodsCount -1 == rodIndex) ||
                                (rodData.getWasteAmount() + (rodCapacity * rodIndex) >= assembly.getWasteQuota())
                );

                brightness = world.getCombinedLight(rodPosition, fluid.getLuminosity());
                ModRenderHelper.renderFluidCube(fluid, facesToDraw, x, y, z,
                        0.005, 0.0, 0.005,
                        0.995, wasteHight, 0.995,
                        wasteColor, brightness);

            } else if (gotFuel) {

                // only fuel

                facesToDraw = facesToDraw.set(EnumFacing.DOWN, 0 == rodIndex);
                facesToDraw = facesToDraw.set(EnumFacing.UP,
                        (rodsCount -1 == rodIndex) ||
                                (fuelHight + wasteHight < 1.0f) ||
                                (fuelHight + (rodCapacity * rodIndex) >= assembly.getFuelQuota())

                );

                brightness = world.getCombinedLight(rodPosition, fluid.getLuminosity());
                ModRenderHelper.renderFluidCube(fluid, facesToDraw, x, y + wasteHight, z,
                        0.005, 0.0, 0.005,
                        0.995, fuelHight, 0.995,
                        fuelColor, brightness);
            }
        } else {

            // X or Z axis

            float myFuelQuota = assembly.getFuelRodFuelQuota();
            float myWasteQuota = assembly.getFuelRodWasteQuota();
            final boolean gotFuel, gotWaste;
            int brightness;

            final double x1, x2, z1, z2;

            if (EnumFacing.Axis.X == axis) {

                x1 = 0.000;
                x2 = 1.000;
                z1 = 0.005;
                z2 = 0.995;

            } else {

                x1 = 0.005;
                x2 = 0.995;
                z1 = 0.000;
                z2 = 1.000;

            }

            wasteHight = myWasteQuota / rodCapacity;
            fuelHight = myFuelQuota / rodCapacity;

            gotWaste = wasteHight > 0.0f;
            gotFuel = fuelHight > 0.0f;

            if (gotWaste && gotFuel) {

                // both waste & fuel

                final float offset = MathHelper.sin((world.getTotalWorldTime() + partialTicks) * 0.1f) * 0.01f;

                brightness = world.getCombinedLight(rodPosition, fluid.getLuminosity());

                // - waste
                facesToDraw = BlockFacings.from(true, false, axis != EnumFacing.Axis.Z, axis != EnumFacing.Axis.Z,
                        axis != EnumFacing.Axis.X, axis != EnumFacing.Axis.X);

                wasteHight += offset;
                ModRenderHelper.renderFluidCube(fluid, facesToDraw, x, y, z,
                        x1, 0.005, z1,
                        x2, wasteHight, z2,
                        wasteColor, brightness);

                // - fuel
                facesToDraw = facesToDraw.set(EnumFacing.DOWN, false);
                facesToDraw = facesToDraw.set(EnumFacing.UP, true);

                fuelHight -= offset;
                ModRenderHelper.renderFluidCube(fluid, facesToDraw, x, y + wasteHight, z,
                        x1, 0.000, z1,
                        x2, fuelHight - 0.005, z2,
                        fuelColor, brightness);

            } else if (gotWaste) {

                // only waste

                facesToDraw = BlockFacings.from(true, true, axis != EnumFacing.Axis.Z, axis != EnumFacing.Axis.Z,
                        axis != EnumFacing.Axis.X, axis != EnumFacing.Axis.X);

                brightness = world.getCombinedLight(rodPosition, fluid.getLuminosity());
                ModRenderHelper.renderFluidCube(fluid, facesToDraw, x, y, z,
                        x1, 0.005, z1,
                        x2, wasteHight - 0.005, z2,
                        wasteColor, brightness);

            } else if (gotFuel) {

                // only fuel

                facesToDraw = BlockFacings.from(true, true, axis != EnumFacing.Axis.Z, axis != EnumFacing.Axis.Z,
                        axis != EnumFacing.Axis.X, axis != EnumFacing.Axis.X);

                brightness = world.getCombinedLight(rodPosition, fluid.getLuminosity());
                ModRenderHelper.renderFluidCube(fluid, facesToDraw, x, y + wasteHight, z,
                        x1, 0.005, z1,
                        x2, fuelHight - 0.005, z2,
                        fuelColor, brightness);
            }
        }
    }
}
