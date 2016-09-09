package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.IPowerProvider;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class TileEntityTurbinePowerTap extends TileEntityTurbinePart implements INeighborUpdatableEntity, IPowerProvider {

	// INeighborUpdatableEntity

	@Override
	public void onNeighborBlockChange(World world, BlockPos position, IBlockState stateAtPosition, Block neighborBlock) {
		if(isConnected()) {
			checkForConnections(world, position);
		}
	}

	@Override
	public void onNeighborTileChange(IBlockAccess world, BlockPos position, BlockPos neighbor) {
		if(isConnected()) {
			checkForConnections(world, position);
		}
	}
	
	// IMultiblockPart

	@Override
	public void onAttached(MultiblockControllerBase newController) {
		super.onAttached(newController);

		BlockPos position = this.getPos();
		checkForConnections(this.worldObj, position);
		
		this.notifyNeighborsOfTileChange();
	}
	
	@Override
	public void onMachineAssembled(MultiblockControllerBase multiblockControllerBase) {
		super.onMachineAssembled(multiblockControllerBase);

		BlockPos position = this.getPos();
		checkForConnections(this.worldObj, position);
		
		this.notifyNeighborsOfTileChange();
	}

	protected abstract void checkForConnections(IBlockAccess world, BlockPos position);
}
