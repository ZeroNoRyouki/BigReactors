package erogenousbeef.bigreactors.common.multiblock.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.lib.BlockFacings;
import zero.mods.zerocore.util.WorldHelper;

public class TileEntityReactorPowerTap extends TileEntityReactorPart implements IEnergyProvider, INeighborUpdatableEntity {

	private IEnergyReceiver	rfNetwork;
	
	public TileEntityReactorPowerTap() {

		super();
		rfNetwork = null;
	}
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos position, IBlockState stateAtPosition, Block neighborBlock) {

		if(isConnected()) {
			checkForConnections(world, position);
		}
	}

	@Override
	public void onNeighborTileChange(IBlockAccess world, BlockPos position, BlockPos neighbor) {

		if (isConnected()) {
			checkForConnections(world, position);
		}
	}

	// IMultiblockPart
	@Override
	public void onAttached(MultiblockControllerBase newController) {

		super.onAttached(newController);
		checkForConnections(this.worldObj, this.getPos());
	}
	
	@Override
	public void onMachineAssembled(MultiblockControllerBase multiblockControllerBase) {

		super.onMachineAssembled(multiblockControllerBase);

		BlockPos position = this.getPos();
		checkForConnections(this.worldObj, position);
		
		// Force a connection to the power taps
		this.notifyNeighborsOfTileChange();
	}

	// Custom PowerTap methods
	/**
	 * Check for a world connection, if we're assembled.
	 * @param world
	 * @param position
	 */
	protected void checkForConnections(IBlockAccess world, BlockPos position) {

		boolean wasConnected = this.rfNetwork != null;
		BlockFacings out = this.getOutwardsDir();

		if (out.none()) {

			wasConnected = false;
			this.rfNetwork = null;

		} else if (1 == out.countFacesIf(true)) {

			// See if our adjacent non-reactor coordinate has a TE
			this.rfNetwork = null;

			TileEntity te = world.getTileEntity(out.offsetBlockPos(this.getWorldPosition()));

			// Skip power taps, as they implement these APIs and we don't want to shit energy back and forth
			if (!(te instanceof TileEntityReactorPowerTap) && (te instanceof IEnergyReceiver)) {

				IEnergyReceiver handler = (IEnergyReceiver)te;

				if(handler.canConnectEnergy(out.firstIf(true).getOpposite()))
					this.rfNetwork = handler;
			}
		}

		boolean isConnected = this.rfNetwork != null;

		if (wasConnected != isConnected)
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
	}

	/** This will be called by the Reactor Controller when this tap should be providing power.
	 * @return Power units remaining after consumption.
	 */
	public int onProvidePower(int units) {

		BlockFacings out = this.getOutwardsDir();

		if ((this.rfNetwork == null) || (1 != out.countFacesIf(true)))
			return units;

		EnumFacing approachDirection = out.firstIf(true).getOpposite();
		int energyConsumed = this.rfNetwork.receiveEnergy(approachDirection, units, false);

		units -= energyConsumed;

		return units;
	}

	public boolean hasEnergyConnection() {
		return null != this.rfNetwork;
	}

	// IEnergyConnection
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return this.getOutwardsDir().isSet(from);
	}
	
	// IEnergyProvider
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return this.isConnected() && this.canConnectEnergy(from) ? this.getReactorController().extractEnergy(from, maxExtract, simulate) : 0;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return this.isConnected() ? this.getReactorController().getEnergyStored(from) : 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return this.isConnected() ? this.getReactorController().getMaxEnergyStored(from) : 0;
	}
}
