package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorBearing;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockTurbineRotorBearing extends BlockMultiblockDevice {

    public BlockTurbineRotorBearing(String blockName) {
        super(PartType.TurbineRotorBearing, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTurbineRotorBearing();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        // allow correct brightness of the rotor TESR
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {

        final TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileEntityTurbineRotorBearing) {

            final TileEntityTurbineRotorBearing bearing = (TileEntityTurbineRotorBearing)te;

            if (bearing.isConnected() && bearing.getTurbine().getActive())
                return bearing.getAABB();
        }

        return super.getCollisionBoundingBox(state, world, pos);
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

        if (BigReactors.CONFIG.disableTurbineParticles)
            return;

        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileEntityTurbineRotorBearing) {

            // Rotor bearing found!
            final TileEntityTurbineRotorBearing bearing = (TileEntityTurbineRotorBearing)te;
            final MultiblockTurbine turbine = bearing.getTurbine();

            if (turbine != null && !turbine.isInteriorInvisible() && turbine.isAssembledAndActive()) {

                // Spawn particles!
                final int numParticles = Math.min(20, Math.max(1, turbine.getFluidConsumedLastTick() / 40));
                final BlockPos minCoord = turbine.getMinimumCoord().add(1, 1, 1);
                final BlockPos maxCoord = turbine.getMaximumCoord().add(-1, -1, -1);
                final EnumFacing inwardsDir = bearing.getOutwardFacing().getOpposite();
                final int offsetX = inwardsDir.getXOffset();
                final int offsetY = inwardsDir.getYOffset();
                final int offsetZ = inwardsDir.getZOffset();
                final EnumParticleTypes particle = BigReactors.VALENTINES_DAY ? EnumParticleTypes.HEART : EnumParticleTypes.CLOUD;

                int minX = minCoord.getX();
                int minY = minCoord.getY();
                int minZ = minCoord.getZ();
                int maxX = maxCoord.getX();
                int maxY = maxCoord.getY();
                int maxZ = maxCoord.getZ();

                if (offsetX != 0)
                    minX = maxX = bearing.getWorldPosition().getX() + offsetX;

                else if (offsetY != 0)
                    minY = maxY = bearing.getWorldPosition().getY() + offsetY;

                else
                    minZ = maxZ = bearing.getWorldPosition().getZ() + offsetZ;

                int particleX, particleY, particleZ;

                for (int i = 0; i < numParticles; i++) {

                    particleX = minX + (int)(rand.nextFloat() * (maxX - minX + 1));
                    particleY = minY + (int)(rand.nextFloat() * (maxY - minY + 1));
                    particleZ = minZ + (int)(rand.nextFloat() * (maxZ - minZ + 1));

                    WorldHelper.spawnVanillaParticles(bearing.getWorld(), particle, 1, numParticles,
                            particleX, particleY, particleZ, 0, 0, 0);
                }
            }
        }
    }



    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    /*
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileEntityTurbineRotorBearing) {

            // Rotor bearing found!
            final TileEntityTurbineRotorBearing bearing = (TileEntityTurbineRotorBearing)te;
            final MultiblockTurbine turbine = bearing.getTurbine();
            final EnumFacing direction = bearing.getOutwardFacing();

            if (null != direction && turbine != null && !turbine.isInteriorInvisible() && turbine.isAssembledAndActive()) {

                final BlockPos minCoord = turbine.getMinimumCoord().add(1, 1, 1);
                final BlockPos maxCoord = turbine.getMaximumCoord().add(-1, -1, -1);
                final int radiusX = maxCoord.getX() - minCoord.getX() - 1;
                final int radiusY = maxCoord.getY() - minCoord.getY() - 1;
                final int radiusZ = maxCoord.getZ() - minCoord.getZ() - 1;

                pos = pos.offset(direction);
                pos = pos.add(0, 20, 0);

                for (int i = 0; i < 54; ++i)
                SteamParticle.spawn(world, pos, direction, radiusX, radiusY, radiusZ);

            }
        }
    }
    */
}
