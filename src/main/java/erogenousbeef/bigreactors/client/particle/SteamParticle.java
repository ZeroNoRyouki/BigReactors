package erogenousbeef.bigreactors.client.particle;

import it.zerono.mods.zerocore.lib.client.particle.SpiralParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SteamParticle extends SpiralParticle {

    public static void spawn(final World world, final BlockPos origin, final EnumFacing direction,
                             int xRadius, int yRadius, int zRadius) {


        double radius = Math.min(xRadius, zRadius);
        int lifeInTicks = 20 * yRadius;

        SteamParticle particle = new SteamParticle(world, origin.getX(), origin.getY(), origin.getZ(), radius, lifeInTicks);

        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    protected SteamParticle(World world, double centerX, double centerY, double centerZ, double radius, int lifeInTicks) {

        super(world, centerX, centerY, centerZ, radius, lifeInTicks);

        this.particleRed = this.particleGreen = this.particleBlue = 1.0F - (float)(Math.random() * 0.30000001192092896D);
        this.particleScale *= 0.75F;
        this.particleScale *= 2.5F;
        this.oSize = this.particleScale;

    }

    /**
     * Renders the particle
     */
    public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ,
                               float rotationYZ, float rotationXY, float rotationXZ)  {

        float f = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge * 32.0F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        this.particleScale = this.oSize * f;
        super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    private float oSize;
}
