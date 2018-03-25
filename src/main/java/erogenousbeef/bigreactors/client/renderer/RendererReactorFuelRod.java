package erogenousbeef.bigreactors.client.renderer;

import erogenousbeef.bigreactors.client.ClientReactorFuelRodsLayout;
import erogenousbeef.bigreactors.client.ClientReactorFuelRodsLayout.FuelRodFluidStatus;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;
import erogenousbeef.bigreactors.init.BrFluids;
import it.zerono.mods.zerocore.lib.BlockFacings;
import it.zerono.mods.zerocore.lib.client.render.CachedRender;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.math.Colour;
import it.zerono.mods.zerocore.lib.math.Cuboid;
import it.zerono.mods.zerocore.lib.math.LightMap;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RendererReactorFuelRod extends TileEntitySpecialRenderer<TileEntityReactorFuelRod> {

    @Override
    public void renderTileEntityAt(TileEntityReactorFuelRod rod, double x, double y, double z, float partialTicks, int destroyStage) {

        if (rod.isOccluded()) {
            return;
        }

        final MultiblockReactor reactor = rod.getReactorController();

        if (null == reactor || !reactor.isAssembled() || reactor.isInteriorInvisible()) {
            return;
        }

        final ClientReactorFuelRodsLayout layout = (ClientReactorFuelRodsLayout) reactor.getFuelRodsLayout();
        final BlockPos rodPosition = rod.getWorldPosition();

        if (EnumFacing.Plane.VERTICAL == layout.getAxis().getPlane()) {

            ////////////////////////////////////////////////////////////////////////////////////
            // Vertical rod
            ////////////////////////////////////////////////////////////////////////////////////

            final int rodIndex = rodPosition.getY() - reactor.getMinimumCoord().getY() - 1;
            final ClientReactorFuelRodsLayout.FuelData rodData = layout.getFuelData(rodIndex);
            final FuelRodFluidStatus fluidStatus = null != rodData ? rodData.getFluidStatus() :
                    FuelRodFluidStatus.Empty;

            if (FuelRodFluidStatus.Empty == fluidStatus) {

                return;

            } else if (FuelRodFluidStatus.Mixed == fluidStatus) {

                final VertexBuffer vertexBuffer = Tessellator.getInstance().getBuffer();

                vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

                final float offset = MathHelper.sin((this.getWorld().getTotalWorldTime() + partialTicks) * 0.1f) * 0.01f;
                final LightMap lightMap = this.getFuelLightMap(rodPosition);
                float wasteHeight = rodData.getWasteHeight() + offset;
                float fuelHeight = rodData.getFuelHeight() - offset;
                Cuboid cuboid;

                // waste

                cuboid = new Cuboid(0.005, 0.0, 0.005, 0.995, Math.min(wasteHeight, 1.0), 0.995);
                ModRenderHelper.bufferFluidCube(vertexBuffer, cuboid, BlockFacings.HORIZONTAL, layout.getWasteColor(), lightMap,
                        BrFluids.fluidFuelColumn);

                // fuel

                final BlockFacings visibleFaces = BlockFacings.HORIZONTAL
                        .set(EnumFacing.UP, (layout.getRodLength() - 1 == rodIndex) ||
                                (rodData.getFuelAmount() + (MultiblockReactor.FuelCapacityPerFuelRod * rodIndex)
                                        >= layout.getFuelQuota()));

                cuboid.MIN.Y = Math.max(wasteHeight, 0.0);
                cuboid.MAX.Y = Math.min(fuelHeight + wasteHeight, 1.0);

                ModRenderHelper.bufferFluidCube(vertexBuffer, cuboid, visibleFaces, layout.getFuelColor(), lightMap,
                        BrFluids.fluidFuelColumn);

                // render

                RenderHelper.disableStandardItemLighting();
                GlStateManager.translate(x, y, z);
                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                Tessellator.getInstance().draw();
                GlStateManager.translate(-x, -y, -z);
                RenderHelper.enableStandardItemLighting();

                return;
            }

            // not mixed nor empty ...

            CachedRender render = layout.getCachedRender(fluidStatus);

            if (null == render) {

                switch (fluidStatus) {

                    default:
                    case FullFuelOnly:
                    case FullWasteOnly:

                        render = new FuelRodCachedRender(BlockFacings.HORIZONTAL, FULL_VERTICAL_CUBE,
                                FuelRodFluidStatus.FullFuelOnly == fluidStatus ? layout.getFuelColor() : layout.getWasteColor(),
                                this.getFuelLightMap(rodPosition));
                        break;

                    case FuelOnly:
                    case WasteOnly:

                        final float height = FuelRodFluidStatus.FuelOnly == fluidStatus ? rodData.getFuelHeight() : rodData.getWasteHeight();

                        render = new FuelRodCachedRender(BlockFacings.ALL.set(EnumFacing.DOWN, false)
                                .set(EnumFacing.UP, 1.0 != height),
                                new Cuboid(0.005, 0.0, 0.005, 0.995, height, 0.995),
                                FuelRodFluidStatus.FuelOnly == fluidStatus ? layout.getFuelColor() : layout.getWasteColor(),
                                this.getFuelLightMap(rodPosition));
                        break;
                }

                layout.setChachedRender(fluidStatus, render);
            }

            render.paint(x, y, z);

        } else {

            ////////////////////////////////////////////////////////////////////////////////////
            // Horizontal rod
            ////////////////////////////////////////////////////////////////////////////////////

            final EnumFacing.Axis axis = layout.getAxis();
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

            final ClientReactorFuelRodsLayout.FuelData rodData = layout.getFuelData(0);
            final FuelRodFluidStatus fluidStatus = null != rodData ? rodData.getFluidStatus() :
                    FuelRodFluidStatus.Empty;

            if (FuelRodFluidStatus.Empty == fluidStatus) {

                return;

            } else if (FuelRodFluidStatus.Mixed == fluidStatus) {

                final VertexBuffer vertexBuffer = Tessellator.getInstance().getBuffer();

                vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

                final float offset = MathHelper.sin((this.getWorld().getTotalWorldTime() + partialTicks) * 0.1f) * 0.01f;
                final LightMap lightMap = this.getFuelLightMap(rodPosition);
                float wasteHeight = rodData.getWasteHeight() + offset;
                float fuelHeight = rodData.getFuelHeight() - offset;
                Cuboid cuboid;

                BlockFacings visibleFaces = BlockFacings.from(true, false, axis != EnumFacing.Axis.Z,
                        axis != EnumFacing.Axis.Z, axis != EnumFacing.Axis.X, axis != EnumFacing.Axis.X);

                // waste

                cuboid = new Cuboid(x1, 0.005, z1, x2, Math.min(wasteHeight, 0.995), z2);
                ModRenderHelper.bufferFluidCube(vertexBuffer, cuboid, visibleFaces, layout.getWasteColor(), lightMap, BrFluids.fluidFuelColumn);

                // fuel

                visibleFaces = visibleFaces.set(EnumFacing.DOWN, false).set(EnumFacing.UP, true);
                cuboid.MIN.Y = Math.max(wasteHeight, 0.0);
                cuboid.MAX.Y = Math.min(fuelHeight - 0.005, 0.995);
                ModRenderHelper.bufferFluidCube(vertexBuffer, cuboid, visibleFaces, layout.getFuelColor(), lightMap, BrFluids.fluidFuelColumn);

                // render

                RenderHelper.disableStandardItemLighting();
                GlStateManager.translate(x, y, z);
                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                Tessellator.getInstance().draw();
                GlStateManager.translate(-x, -y, -z);
                RenderHelper.enableStandardItemLighting();

                return;
            }

            // not mixed nor empty ...

            CachedRender render = layout.getCachedRender(fluidStatus);

            final BlockFacings visibleFaces = BlockFacings.from(true, true, axis != EnumFacing.Axis.Z,
                    axis != EnumFacing.Axis.Z, axis != EnumFacing.Axis.X, axis != EnumFacing.Axis.X);

            if (null == render) {

                switch (fluidStatus) {

                    default:
                    case FullFuelOnly:
                    case FullWasteOnly:

                        render = new FuelRodCachedRender(visibleFaces,
                                new Cuboid(x1, 0.005, z1, x2, 0.995, z2),
                                FuelRodFluidStatus.FullFuelOnly == fluidStatus ? layout.getFuelColor() : layout.getWasteColor(),
                                this.getFuelLightMap(rodPosition));
                        break;

                    case FuelOnly:
                    case WasteOnly:

                        final Colour colour = FuelRodFluidStatus.FuelOnly == fluidStatus ? layout.getFuelColor() : layout.getWasteColor();
                        final float height = FuelRodFluidStatus.FuelOnly == fluidStatus ? rodData.getFuelHeight() : rodData.getWasteHeight();

                        render = new FuelRodCachedRender(visibleFaces,
                                new Cuboid(x1, 0.005, z1, x2, height - 0.005, z2),
                                colour, this.getFuelLightMap(rodPosition));
                        break;
                }

                layout.setChachedRender(fluidStatus, render);
            }

            render.paint(x, y, z);
        }
    }

    private LightMap getFuelLightMap(@Nonnull final BlockPos position) {
        return LightMap.fromCombinedLight(this.getWorld().getCombinedLight(position, BrFluids.fluidFuelColumn.getLuminosity()));
    }

    private static final Cuboid FULL_VERTICAL_CUBE = new Cuboid(0.005, 0.0, 0.005, 0.995, 1, 0.995);

    private static class FuelRodCachedRender extends CachedRender {

        public FuelRodCachedRender(@Nonnull final BlockFacings visibleFaces, @Nonnull final Cuboid cuboid,
                                   final Colour argbColour, @Nonnull final LightMap lightMap) {

            this._visibleFaces = visibleFaces;
            this._cuboid = cuboid;
            this._colour = argbColour;
            this._lightMap = lightMap;
        }

        @Override
        protected ResourceLocation getTexture() {
            return TextureMap.LOCATION_BLOCKS_TEXTURE;
        }

        @Override
        protected void buildRender() {
            ModRenderHelper.paintFluidCube(this._cuboid, this._visibleFaces, this._colour, this._lightMap, BrFluids.fluidFuelColumn);
        }

        private final BlockFacings _visibleFaces;
        private final Cuboid _cuboid;
        private final Colour _colour;
        private final LightMap _lightMap;
    }
}