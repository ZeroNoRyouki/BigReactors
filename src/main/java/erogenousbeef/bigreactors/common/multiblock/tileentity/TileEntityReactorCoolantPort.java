package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort;
import erogenousbeef.bigreactors.common.multiblock.helpers.CoolantContainer;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityReactorCoolantPort extends TileEntityReactorPart implements IFluidHandler, INeighborUpdatableEntity,
		ITickableMultiblockPart, IInputOutputPort {

	public TileEntityReactorCoolantPort() {

		super();
		this._direction = Direction.Input;
		this.pumpDestination = null;
	}

	@Override
	public Direction getDirection() {
		return this._direction;
	}

	@Override
	public void setDirection(Direction direction, boolean markForUpdate) {

		if (direction == this._direction)
			return;

		this._direction = direction;
		WorldHelper.notifyBlockUpdate(worldObj, this.getWorldPosition(), null, null);

		if (!worldObj.isRemote) {

			if (!direction.isInput())
				this.checkForAdjacentTank();

			if (markForUpdate)
				this.markDirty();
			else
				this.notifyNeighborsOfTileChange();

		} else
			this.notifyNeighborsOfTileChange();
	}

	@Override
	public void toggleDirection(boolean markForUpdate) {
		this.setDirection(this._direction.opposite(), markForUpdate);
	}

	@Override
	public void onMachineAssembled(MultiblockControllerBase multiblockControllerBase)
	{
		super.onMachineAssembled(multiblockControllerBase);
		checkForAdjacentTank();

		this.notifyNeighborsOfTileChange();

		// Re-render on the client
		if(worldObj.isRemote) {
			WorldHelper.notifyBlockUpdate(worldObj, this.getPos(), null, null);
		}
	}
	
	@Override
	public void onMachineBroken()
	{
		super.onMachineBroken();
		pumpDestination = null;
		
		if(worldObj.isRemote) {
			WorldHelper.notifyBlockUpdate(worldObj, this.getPos(), null, null);
		}
	}

	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataFrom(data, syncReason);

		if (!data.hasKey("inlet"))
			return;

		if (SyncReason.FullSync == syncReason)
			this._direction = Direction.from(data.getBoolean("inlet"));
		else
			this.setDirection(Direction.from(data.getBoolean("inlet")), false);
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataTo(data, syncReason);
		data.setBoolean("inlet", this._direction.isInput());
	}

	// IFluidHandler
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {

		if (!isConnected() || !this._direction.isInput() || null == from || from != this.getOutwardFacing())
			return 0;
		
		CoolantContainer cc = getReactorController().getCoolantContainer();
		return cc.fill(getConnectedTank(), resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {

		if (!isConnected() || null == from || from != this.getOutwardFacing())
			return null;

		CoolantContainer cc = getReactorController().getCoolantContainer();
		return cc.drain(getConnectedTank(), resource, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {

		if (!isConnected() || null == from || from != this.getOutwardFacing())
			return null;

		CoolantContainer cc = getReactorController().getCoolantContainer();
		return cc.drain(getConnectedTank(), maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {

		if (!isConnected() || null == from || from != this.getOutwardFacing() || !this._direction.isInput())
			// Also prevent pipes from filling up the output tank inadvertently
			return false;

		CoolantContainer cc = getReactorController().getCoolantContainer();
		return cc.canFill(getConnectedTank(), fluid);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {

		if (!isConnected() || null == from || from != this.getOutwardFacing())
			return false;

		CoolantContainer cc = getReactorController().getCoolantContainer();
		return cc.canDrain(getConnectedTank(), fluid);
	}

	private static FluidTankInfo[] emptyTankArray = new FluidTankInfo[0];
	
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {

		if (!isConnected() || null == from || from != this.getOutwardFacing())
			return emptyTankArray;

		CoolantContainer cc = getReactorController().getCoolantContainer();
		return cc.getTankInfo(getConnectedTank());
	}
	
	// ITickableMultiblockPart
	
	@Override
	public void onMultiblockServerTick() {
		// Try to pump steam out, if an outlet
		if(pumpDestination == null || this._direction.isInput())
			return;

		CoolantContainer cc = getReactorController().getCoolantContainer();
		FluidStack fluidToDrain = cc.drain(CoolantContainer.HOT, cc.getCapacity(), false);
		EnumFacing out = this.getOutwardFacing();

		if (fluidToDrain != null && fluidToDrain.amount > 0 && null != out) {

			fluidToDrain.amount = pumpDestination.fill(out.getOpposite(), fluidToDrain, true);
			cc.drain(CoolantContainer.HOT, fluidToDrain, true);
		}
	}

	// INeighborUpdatableEntity
	@Override
	public void onNeighborBlockChange(World world, BlockPos position, IBlockState stateAtPosition, Block neighborBlock) {
		checkForAdjacentTank();
	}
	
	@Override
	public void onNeighborTileChange(IBlockAccess world, BlockPos position, BlockPos neighbor) {
		checkForAdjacentTank();
	}

	// Private Helpers
	private int getConnectedTank() {
		return this._direction.isInput() ? CoolantContainer.COLD : CoolantContainer.HOT;
	}

	protected void checkForAdjacentTank() {

		this.pumpDestination = null;

		if (this.worldObj.isRemote || this._direction.isInput())
			return;

		EnumFacing out = this.getOutwardFacing();

		if (null == out)
			return;

		TileEntity neighbor = this.worldObj.getTileEntity(this.getPos().offset(out));

		if (neighbor instanceof IFluidHandler)
			this.pumpDestination = (IFluidHandler)neighbor;
	}

	private Direction _direction;
	private IFluidHandler pumpDestination;
}
