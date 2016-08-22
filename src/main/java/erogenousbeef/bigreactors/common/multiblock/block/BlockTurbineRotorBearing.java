package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorBearing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockTurbineRotorBearing extends BlockMultiblockDevice {

    public BlockTurbineRotorBearing(String blockName) {
        super(PartType.TurbineRotorBearing, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTurbineRotorBearing();
    }

    // TODO Commented temporarily to allow this thing to compile...
	/*

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
    	int metadata = world.getBlockMetadata(x, y, z);
    	if(metadata == METADATA_BEARING) {
        	TileEntity te = world.getTileEntity(x, y, z);
        	if(te instanceof TileEntityTurbineRotorBearing) {
        		TileEntityTurbineRotorBearing bearing = (TileEntityTurbineRotorBearing)te;
        		if(bearing.isConnected() && bearing.getTurbine().getActive()) {
        			return bearing.getAABB();
        		}
        	}
    	}

		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    */



    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState world, World pos, BlockPos state, Random rand) {
        // TODO Commented temporarily to allow this thing to compile...
		/*
    	int metadata = world.getBlockMetadata(x, y, z);
    	if(metadata == METADATA_BEARING) {
        	TileEntity te = world.getTileEntity(x, y, z);
    		if(te instanceof TileEntityTurbinePart) {
    			// Rotor bearing found!
    			TileEntityTurbinePart bearing = (TileEntityTurbinePart)te;
    			MultiblockTurbine turbine = bearing.getTurbine();
    			if(turbine != null && turbine.getActive()) {
    				// Spawn particles!
    				int numParticles = Math.min(20, Math.max(1, turbine.getFluidConsumedLastTick() / 40));
    				ForgeDirection inwardsDir = bearing.getOutwardsDir().getOpposite();
					BlockPos minCoord, maxCoord;
    				minCoord = turbine.getMinimumCoord().add(1, 1, 1);
    				maxCoord = turbine.getMaximumCoord().add(-1, -1, -1);
    				if(inwardsDir.offsetX != 0) {
    					minCoord.x = maxCoord.x = bearing.xCoord + inwardsDir.offsetX;
    				}
    				else if(inwardsDir.offsetY != 0) {
    					minCoord.y = maxCoord.y = bearing.yCoord + inwardsDir.offsetY;
    				}
    				else {
    					minCoord.z = maxCoord.z = bearing.zCoord + inwardsDir.offsetZ;
    				}

                    double particleX, particleY, particleZ;
    				for(int i = 0; i < numParticles; i++) {
    					particleX = minCoord.x + par5Random.nextFloat() * (maxCoord.x - minCoord.x + 1);
    					particleY = minCoord.y + par5Random.nextFloat() * (maxCoord.y - minCoord.y + 1);
    					particleZ = minCoord.z + par5Random.nextFloat() * (maxCoord.z - minCoord.z + 1);
                        world.spawnParticle(BigReactors.VALENTINES_DAY ? "heart" : "cloud", particleX, particleY, particleZ,
                        		par5Random.nextFloat() * inwardsDir.offsetX,
                        		par5Random.nextFloat() * inwardsDir.offsetY,
                        		par5Random.nextFloat() * inwardsDir.offsetZ);
    				}
    			}
    		}
    	}
    	*/
    }

}
