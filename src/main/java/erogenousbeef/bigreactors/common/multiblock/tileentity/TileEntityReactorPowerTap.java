package erogenousbeef.bigreactors.common.multiblock.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;

public abstract class TileEntityReactorPowerTap extends TileEntityReactorPart implements INeighborUpdatableEntity {

	public abstract boolean hasEnergyConnection();

	/** This will be called by the Reactor Controller when this tap should be providing power.
	 * @return Power units remaining after consumption.
	 */
	public abstract long onProvidePower(long units);

	@Override
	public void onNeighborBlockChange(World world, BlockPos position, IBlockState stateAtPosition, Block neighborBlock) {

		if (isConnected())
			checkForConnections(world, position);
	}

	@Override
	public void onNeighborTileChange(IBlockAccess world, BlockPos position, BlockPos neighbor) {

		if (isConnected())
			checkForConnections(world, position);
	}

	@Override
	public void onAttached(MultiblockControllerBase newController) {

		super.onAttached(newController);
		checkForConnections(this.worldObj, this.getWorldPosition());
	}
	
	@Override
	public void onMachineAssembled(MultiblockControllerBase multiblockControllerBase) {

		super.onMachineAssembled(multiblockControllerBase);
		checkForConnections(this.worldObj, this.getWorldPosition());
		
		// Force a connection to the power taps
		this.notifyNeighborsOfTileChange();
	}

	/**
	 * Check for a world connection, if we're assembled.
	 * @param world
	 * @param position
	 */
	protected abstract void checkForConnections(IBlockAccess world, BlockPos position);
}
