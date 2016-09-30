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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityTurbineFluidPort extends TileEntityTurbinePart implements INeighborUpdatableEntity,
		ITickableMultiblockPart, IInputOutputPort {

	public TileEntityTurbineFluidPort() {

		this._direction = Direction.Input;
		_pumpDestination = null;
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

		if (WorldHelper.calledByLogicalServer(this.worldObj)) {

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
	public void onMachineAssembled(MultiblockControllerBase multiblockControllerBase) {

		super.onMachineAssembled(multiblockControllerBase);
		checkForAdjacentTank();
		
		this.notifyNeighborsOfTileChange();
	}

	@Override
	public void onMachineBroken()
	{
		super.onMachineBroken();
		_pumpDestination = null;
	}

	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataFrom(data, syncReason);

		if (!data.hasKey("isInlet"))
			return;

		if (SyncReason.FullSync == syncReason)
			this._direction = Direction.from(data.getBoolean("isInlet"));
		else
			this.setDirection(Direction.from(data.getBoolean("isInlet")), false);
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

	// ITickableMultiblockPart
	@Override
	public void onMultiblockServerTick() {

		// Try to pump steam out, if an outlet
		if (null == this._pumpDestination || this._direction.isInput())
			return;

		final IFluidHandler fluidHandler = this.getTurbine().getFluidHandler(Direction.Output);
		final FluidStack fluidToDrain = fluidHandler.drain(MultiblockTurbine.TANK_SIZE, false);
		
		if (fluidToDrain != null && fluidToDrain.amount > 0) {

			fluidToDrain.amount = this._pumpDestination.fill(fluidToDrain, true);
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

		if(!worldObj.isRemote)
			checkForAdjacentTank();
	}

	private void checkForAdjacentTank() {

		EnumFacing facing = this.getOutwardFacing();

		this._pumpDestination = null;

		if (null == facing || WorldHelper.calledByLogicalClient(this.worldObj) || this._direction.isInput())
			return;

		TileEntity neighbor = this.worldObj.getTileEntity(this.getWorldPosition().offset(facing));

		facing = facing.getOpposite();

		if (null != neighbor && neighbor.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
			this._pumpDestination = neighbor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
	}

	private Direction _direction;
	private IFluidHandler _pumpDestination;
}