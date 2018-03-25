package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.utils.FluidHelper;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.lib.fluid.FluidHandlerForwarder;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

public class TileEntityTurbineFluidPort extends TileEntityTurbinePart implements /*INeighborUpdatableEntity,*/
		ITickableMultiblockPart, IInputOutputPort {

	public TileEntityTurbineFluidPort() {

		this._direction = Direction.Input;
		this._capabilityForwarder = new FluidHandlerForwarder(EmptyFluidHandler.INSTANCE);
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
		this.updateCapabilityForwarder();

		final World world = this.getWorld();

		if (WorldHelper.calledByLogicalServer(world)) {

			WorldHelper.notifyBlockUpdate(world, this.getWorldPosition(), null, null);
			this.notifyOutwardNeighborsOfStateChange();

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
	}

	@Override
	public void onPostMachineBroken() {

		super.onPostMachineBroken();
		this.notifyOutwardNeighborsOfStateChange();
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
		return (null != CAPAB_FLUID_HANDLER && CAPAB_FLUID_HANDLER == capability) ||
				super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (null != CAPAB_FLUID_HANDLER && CAPAB_FLUID_HANDLER == capability) {
			return CAPAB_FLUID_HANDLER.cast(this._capabilityForwarder);
		}

		return super.getCapability(capability, facing);
	}

	// ITickableMultiblockPart
	@Override
	public void onMultiblockServerTick() {

		if (this._direction.isInput())
			return;

		final IFluidHandler fluidHandler = this.getTurbine().getFluidHandler(Direction.Output);
		final FluidStack fluidToDrain = fluidHandler.drain(MultiblockTurbine.TANK_SIZE, false);
		EnumFacing targetFacing = this.getOutwardFacing();

		if (fluidToDrain != null && fluidToDrain.amount > 0 && null != targetFacing) {

			fluidToDrain.amount = FluidHelper.fillAdjacentHandler(this, targetFacing, fluidToDrain, true);
			fluidHandler.drain(fluidToDrain, true);
		}
	}

	@Override
	public void onAttached(MultiblockControllerBase newController) {

		super.onAttached(newController);
		this.updateCapabilityForwarder();
	}

	@Override
	public void onAssimilated(MultiblockControllerBase newController) {

		super.onAssimilated(newController);
		this.updateCapabilityForwarder();
	}

	@Override
	public void onDetached(MultiblockControllerBase oldController) {

		super.onDetached(oldController);
		this.updateCapabilityForwarder();
	}

	private void updateCapabilityForwarder() {

		final MultiblockTurbine turbine = this.getTurbine();

		this._capabilityForwarder.setHandler(null != turbine ?
				turbine.getFluidHandler(this.getDirection()) : EmptyFluidHandler.INSTANCE);
	}

	@CapabilityInject(IFluidHandler.class)
	private static Capability<IFluidHandler> CAPAB_FLUID_HANDLER = null;

	private Direction _direction;
	private final FluidHandlerForwarder _capabilityForwarder;
}