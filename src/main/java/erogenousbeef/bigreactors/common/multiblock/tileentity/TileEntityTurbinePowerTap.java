package erogenousbeef.bigreactors.common.multiblock.tileentity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.util.WorldHelper;

public class TileEntityTurbinePowerTap extends TileEntityTurbinePartStandard implements IEnergyProvider, INeighborUpdatableEntity {

	IEnergyReceiver	rfNetwork;
	
	public TileEntityTurbinePowerTap() {
		super();
		rfNetwork = null;
	}

	// INeighborUpdatableEntity
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
		if(isConnected()) {
			checkForConnections(world, x, y, z);
		}
	}

	@Override
	public void onNeighborTileChange(IBlockAccess world, int x, int y, int z, int neighborX, int neighborY, int neighborZ) {
		if(isConnected()) {
			checkForConnections(world, x, y, z);
		}
	}

	public boolean isAttachedToPowerNetwork() {
		return rfNetwork != null;
	}
	
	// IMultiblockPart
	@Override
	public void onAttached(MultiblockControllerBase newController) {
		super.onAttached(newController);

		BlockPos position = this.getPos();
		checkForConnections(this.worldObj, position.getX(), position.getY(), position.getZ());
		
		this.notifyNeighborsOfTileChange();
	}
	
	@Override
	public void onMachineAssembled(MultiblockControllerBase multiblockControllerBase) {
		super.onMachineAssembled(multiblockControllerBase);

		BlockPos position = this.getPos();
		checkForConnections(this.worldObj, position.getX(), position.getY(), position.getZ());
		
		this.notifyNeighborsOfTileChange();
	}
	
	/**
	 * Check for a world connection, if we're assembled.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	protected void checkForConnections(IBlockAccess world, int x, int y, int z) {
		boolean wasConnected = (rfNetwork != null);
		// TODO Commented temporarily to allow this thing to compile...
		/*
		ForgeDirection out = getOutwardsDir();
		if(out == ForgeDirection.UNKNOWN) {
			wasConnected = false;
			rfNetwork = null;
		}
		else {
			// See if our adjacent non-reactor coordinate has a TE
			rfNetwork = null;

			TileEntity te = world.getTileEntity(x + out.offsetX, y + out.offsetY, z + out.offsetZ);
			if(!(te instanceof TileEntityReactorPowerTap)) {
				// Skip power taps, as they implement these APIs and we don't want to shit energy back and forth
				if(te instanceof IEnergyReceiver) {
					IEnergyReceiver handler = (IEnergyReceiver)te;
					if(handler.canConnectEnergy(out.getOpposite())) {
						rfNetwork = handler;
					}
				}
			}
		}
		*/
		boolean isConnected = (rfNetwork != null);
		if(wasConnected != isConnected && worldObj.isRemote) {
			// Re-render on clients
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
		}
	}

	/** This will be called by the Reactor Controller when this tap should be providing power.
	 * @return Power units remaining after consumption.
	 */
	public int onProvidePower(int units) {
		if(rfNetwork == null) {
			return units;
		}

		// TODO Commented temporarily to allow this thing to compile...
		/*
		ForgeDirection approachDirection = getOutwardsDir().getOpposite();
		int energyConsumed = rfNetwork.receiveEnergy(approachDirection, (int)units, false);
		units -= energyConsumed;
		*/
		return units;
	}

	// IEnergyConnection
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		if(!this.isConnected()) { return false; }

		// TODO Commented temporarily to allow this thing to compile...
		//return from == getOutwardsDir();
		return false;
	}
	
	// IEnergyProvider
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if(!this.isConnected()) { return 0; }

		return getTurbine().extractEnergy(from, maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		if(!this.isConnected()) { return 0; }
		
		return getTurbine().getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		if(!this.isConnected()) { return 0; }

		return getTurbine().getMaxEnergyStored(from);
	}

}
