package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.helpers.CoolantContainer;
import erogenousbeef.bigreactors.utils.FluidHelper;
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
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityReactorCoolantPort extends TileEntityReactorPart implements /* INeighborUpdatableEntity,*/
		ITickableMultiblockPart, IInputOutputPort {

	public TileEntityReactorCoolantPort() {

		this._direction = Direction.Input;
		//this._pumpDestination = null;
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

		final World world = this.getWorld();

		if (WorldHelper.calledByLogicalServer(world)) {

			WorldHelper.notifyBlockUpdate(world, this.getWorldPosition(), null, null);
			this.notifyOutwardNeighborsOfStateChange();
			/*
			if (direction.isOutput())
				this.checkForAdjacentTank();
			*/
			if (markForUpdate)
				this.markDirty();
			else
				this.notifyNeighborsOfTileChange();

		} else {
			world.markBlockRangeForRenderUpdate(this.getWorldPosition(), this.getWorldPosition());
			this.notifyNeighborsOfTileChange();
		}
	}

	@Override
	public void toggleDirection(boolean markForUpdate) {
		this.setDirection(this._direction.opposite(), markForUpdate);
	}

	@Override
	public void onPostMachineAssembled(MultiblockControllerBase multiblockControllerBase) {

		super.onPostMachineAssembled(multiblockControllerBase);
		this.notifyOutwardNeighborsOfStateChange();
		//this.checkForAdjacentTank();
	}
	
	@Override
	public void onPostMachineBroken() {

		super.onPostMachineBroken();
		this.notifyOutwardNeighborsOfStateChange();
		//this._pumpDestination = null;
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

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (null != CAPAB_FLUID_HANDLER && CAPAB_FLUID_HANDLER == capability && this.isMachineAssembled()) ||
				super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		MultiblockReactor reactor;

		if (null != CAPAB_FLUID_HANDLER && CAPAB_FLUID_HANDLER == capability &&
				null != (reactor = this.getReactorController()) && reactor.isAssembled())
			return CAPAB_FLUID_HANDLER.cast(reactor.getFluidHandler(this._direction));

		return super.getCapability(capability, facing);
	}

	// ITickableMultiblockPart
	@Override
	public void onMultiblockServerTick() {
		/*
		// Try to pump steam out, if an outlet
		if(_pumpDestination == null || this._direction.isInput())
			return;

		CoolantContainer cc = getReactorController().getCoolantContainer();
		FluidStack fluidToDrain = cc.drain(CoolantContainer.HOT, cc.getCapacity(), false);
		EnumFacing out = this.getOutwardFacing();

		if (fluidToDrain != null && fluidToDrain.amount > 0 && null != out) {

			fluidToDrain.amount = _pumpDestination.fill(fluidToDrain, true);
			cc.drain(CoolantContainer.HOT, fluidToDrain, true);
		}
		///
		*/

		if (this._direction.isInput())
			return;

		CoolantContainer cc = this.getReactorController().getCoolantContainer();
		FluidStack fluidToDrain = cc.drain(CoolantContainer.HOT, cc.getCapacity(), false);
		EnumFacing targetFacing = this.getOutwardFacing();

		if (fluidToDrain != null && fluidToDrain.amount > 0 && null != targetFacing) {

			fluidToDrain.amount = FluidHelper.fillAdjacentHandler(this, targetFacing, fluidToDrain, true);
			cc.drain(CoolantContainer.HOT, fluidToDrain, true);
		}


	}

	/*
	// INeighborUpdatableEntity
	@Override
	public void onNeighborBlockChange(World world, BlockPos position, IBlockState stateAtPosition, Block neighborBlock) {

		if (WorldHelper.calledByLogicalServer(this.getWorld()))
			this.checkForAdjacentTank();
	}

	@Override
	public void onNeighborTileChange(IBlockAccess world, BlockPos position, BlockPos neighbor) {

		if (WorldHelper.calledByLogicalServer(this.getWorld()))
			this.checkForAdjacentTank();
	}


	private void checkForAdjacentTank() {

		final World world = this.getWorld();
		EnumFacing facing = this.getOutwardFacing();

		this._pumpDestination = null;

		if (null == facing || WorldHelper.calledByLogicalClient(world) ||
				!this.isMachineAssembled() || this._direction.isInput())
			return;

		TileEntity neighbor = world.getTileEntity(this.getWorldPosition().offset(facing));

		if (null != neighbor) {

			facing = facing.getOpposite();

			if (neighbor.hasCapability(CAPAB_FLUID_HANDLER, facing))
				this._pumpDestination = neighbor.getCapability(CAPAB_FLUID_HANDLER, facing);
		}
	}
	*/

	@CapabilityInject(IFluidHandler.class)
	private static Capability<IFluidHandler> CAPAB_FLUID_HANDLER = null;

	private Direction _direction;
	//private IFluidHandler _pumpDestination;
}
