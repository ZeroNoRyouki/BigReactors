package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorBearing;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public void registerRecipes() {

        if (PartTier.TURBINE_TIERS.contains(PartTier.Legacy))
            GameRegistry.addRecipe(BrBlocks.turbineBearing.createItemStack(), "HRH", "DDD", "HRH",
                    'H', BrBlocks.turbineHousing.createItemStack(PartTier.Legacy, 1), 'D', Items.DIAMOND,
                    'R', BrBlocks.turbineRotorShaft.createItemStack(PartTier.Legacy, 1));

        if (PartTier.TURBINE_TIERS.contains(PartTier.Basic))
            GameRegistry.addRecipe(BrBlocks.turbineBearing.createItemStack(), "HRH", "DDD", "HRH",
                    'H', BrBlocks.turbineHousing.createItemStack(PartTier.Basic, 1), 'D', Items.DIAMOND,
                    'R', BrBlocks.turbineRotorShaft.createItemStack(PartTier.Basic, 1));
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        // allow correct brightness of the rotor TESR
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullyOpaque(IBlockState state) {
        return false;
    }

    @Override
    public boolean isVisuallyOpaque() {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {

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

        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileEntityTurbineRotorBearing) {

            // Rotor bearing found!
            final TileEntityTurbineRotorBearing bearing = (TileEntityTurbineRotorBearing)te;
            final MultiblockTurbine turbine = bearing.getTurbine();

            if (turbine != null && turbine.isAssembled() && turbine.getActive()) {

                // Spawn particles!
                final int numParticles = Math.min(20, Math.max(1, turbine.getFluidConsumedLastTick() / 40));
                final BlockPos minCoord = turbine.getMinimumCoord().add(1, 1, 1);
                final BlockPos maxCoord = turbine.getMaximumCoord().add(-1, -1, -1);
                final EnumFacing inwardsDir = bearing.getOutwardFacing().getOpposite();
                final int offsetX = inwardsDir.getFrontOffsetX();
                final int offsetY = inwardsDir.getFrontOffsetY();
                final int offsetZ = inwardsDir.getFrontOffsetZ();

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

                    WorldHelper.spawnVanillaParticles(bearing.getWorld(),
                            BigReactors.VALENTINES_DAY ? EnumParticleTypes.HEART : EnumParticleTypes.CLOUD, 1, numParticles,
                            particleX, particleY, particleZ, 0, 0, 0);
                }
            }
        }
    }
}
