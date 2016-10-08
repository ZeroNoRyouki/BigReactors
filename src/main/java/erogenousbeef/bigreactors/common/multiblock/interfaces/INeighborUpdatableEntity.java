package erogenousbeef.bigreactors.common.multiblock.interfaces;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface INeighborUpdatableEntity {
	
	/**
	 * Called from a Block class's onNeighborBlockChange
	 * @param world The world containing the tileentity
	 * @param x Tile Entity's xcoord
	 * @param y Tile Entity's ycoord
	 * @param z Tile Entity's zcoord
	 * @param neighborBlock Block that changed
	 */
	public void onNeighborBlockChange(World world, BlockPos position, IBlockState stateAtPosition, Block neighborBlock);
	
	/**
	 * Called from a Block class's onNeighborTileChange
	 * @param world The world containing the TileEntity
	 * @param position Tile entity's position in the world
	 * @param neighbor Changed neighbor's position in the world
	 */
	public void onNeighborTileChange(IBlockAccess world, BlockPos position, BlockPos neighbor);

}
