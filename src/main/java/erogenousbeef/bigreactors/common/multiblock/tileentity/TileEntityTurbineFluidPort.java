package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityTurbineFluidPort extends TileEntityTurbinePart implements INeighborUpdatableEntity,
		ITickableMultiblockPart, IInputOutputPort {

	public TileEntityTurbineFluidPort() {

		this._direction = Direction.Input;
		pumpDestination = null;
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

		if (!worldObj.isRemote) {

			if (markForUpdate)
				this.markDirty();
			else
				this.notifyNeighborsOfTileChange();

		} else
			this.notifyNeighborsOfTileChange();

		WorldHelper.notifyBlockUpdate(this.worldObj, this.getWorldPosition(), null, null);
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
	}

	@Override
	public void onMachineBroken()
	{
		super.onMachineBroken();
		pumpDestination = null;
	}

	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataFrom(data, syncReason);

		if (data.hasKey("isInlet"))
			this._direction = Direction.from(data.getBoolean("isInlet"));
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataTo(data, syncReason);
		data.setBoolean("isInlet", this._direction.isInput());
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY == capability && this.isConnected()) ||
				super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		MultiblockTurbine turbine;

		if (CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY == capability && null != (turbine = this.getTurbine()))
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(turbine.getFluidHandler(this._direction));

		return super.getCapability(capability, facing);
	}

	/*

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {

		MultiblockTurbine turbine = this.getTurbine();

		return null == turbine || !turbine.isAssembled() || from != this.getOutwardFacing() || !this._direction.isInput() ?
				0 : turbine.fill(getTankIndex(), resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {

		MultiblockTurbine turbine = this.getTurbine();

		return null == resource || null == turbine || from != this.getOutwardFacing() ?
				resource : turbine.drain(getTankIndex(), resource, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {

		MultiblockTurbine turbine = this.getTurbine();

		return null == turbine || from != this.getOutwardFacing() ?
				null : turbine.drain(getTankIndex(), maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {

		MultiblockTurbine turbine = this.getTurbine();

		return !(null == turbine || from != this.getOutwardFacing()) &&
				this._direction.isInput() && turbine.canFill(getTankIndex(), fluid);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {

		MultiblockTurbine turbine = this.getTurbine();

		return !(null == turbine || from != this.getOutwardFacing()) &&
				turbine.canDrain(getTankIndex(), fluid);
	}

	protected static final FluidTankInfo[] emptyTankInfoArray = new FluidTankInfo[0];
	
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {

		MultiblockTurbine turbine = this.getTurbine();

		return null == turbine || from != this.getOutwardFacing() ?
				emptyTankInfoArray : new FluidTankInfo[] { turbine.getTankInfo(getTankIndex()) };
	}
	
	private int getTankIndex() {
		return this._direction.isInput() ?  MultiblockTurbine.TANK_INPUT : MultiblockTurbine.TANK_OUTPUT;
	}
	*/

	// ITickableMultiblockPart
	
	@Override
	public void onMultiblockServerTick() {

		// Try to pump steam out, if an outlet
		if (null == this.pumpDestination || this._direction.isInput())
			return;

		final IFluidHandler fluidHandler = this.getTurbine().getFluidHandler(Direction.Output);
		final FluidStack fluidToDrain = fluidHandler.drain(MultiblockTurbine.TANK_SIZE, false);
		
		if (fluidToDrain != null && fluidToDrain.amount > 0) {

			fluidToDrain.amount = this.pumpDestination.fill(fluidToDrain, true);
			fluidHandler.drain(fluidToDrain, true);
		}
	}
	
	// INeighborUpdatableEntity
	@Override
	public void onNeighborBlockChange(World world, BlockPos position, IBlockState stateAtPosition, Block neighborBlock) {

		if (WorldHelper.calledByLogicalServer(world))
			checkForAdjacentTank();
	}
	
	@Override
	public void onNeighborTileChange(IBlockAccess world, BlockPos position, BlockPos neighbor) {
		if(!worldObj.isRemote) {
			checkForAdjacentTank();
		}
	}
	
	// Private Helpers
	protected void checkForAdjacentTank()
	{
		pumpDestination = null;
		if (worldObj.isRemote || this._direction.isInput())
			return;

		// TODO Commented temporarily to allow this thing to compile...
		/*
		ForgeDirection outDir = getOutwardsDir();
		if(outDir == ForgeDirection.UNKNOWN) {
			return;
		}
		
		TileEntity neighbor = WORLD.getTileEntity(xCoord + outDir.offsetX, yCoord + outDir.offsetY, zCoord + outDir.offsetZ);
		if(neighbor instanceof IFluidHandler) {
			pumpDestination = (IFluidHandler)neighbor;
		}
		*/
	}

	Direction _direction;
	IFluidHandler pumpDestination;
}