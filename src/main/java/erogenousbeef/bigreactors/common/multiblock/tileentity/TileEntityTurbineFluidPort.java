package erogenousbeef.bigreactors.common.multiblock.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.util.WorldHelper;

public class TileEntityTurbineFluidPort extends TileEntityTurbinePartStandard implements IFluidHandler, INeighborUpdatableEntity, ITickableMultiblockPart {

	public enum FluidFlow {
		In,
		Out
	}

	FluidFlow flowSetting;
	IFluidHandler pumpDestination;
	
	public TileEntityTurbineFluidPort() {
		super();
		flowSetting = FluidFlow.In;
		pumpDestination = null;
	}

	public void setFluidFlowDirection(FluidFlow newDirection, boolean markDirty) {
		flowSetting = newDirection;

		if(!worldObj.isRemote) {
			if(markDirty) {
				this.markDirty();
			}
			else {
				notifyNeighborsOfTileChange();
			}
		}
		else {
			notifyNeighborsOfTileChange();
		}

		WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
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

		if (data.hasKey("flowSetting"))
			this.flowSetting = FluidFlow.values()[data.getInteger("flowSetting")];
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataTo(data, syncReason);
		data.setInteger("flowSetting", this.flowSetting.ordinal());
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		// TODO Commented temporarily to allow this thing to compile...
		/*
		if(!isConnected() || from != getOutwardsDir()) { return 0; }
		*/

		if(flowSetting != FluidFlow.In) {
			return 0;
		}
		
		return getTurbine().fill(getTankIndex(), resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		// TODO Commented temporarily to allow this thing to compile...
		/*
		if(resource == null || !isConnected() || from != getOutwardsDir()) { return resource; }
		*/
		return getTurbine().drain(getTankIndex(), resource, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		// TODO Commented temporarily to allow this thing to compile...
		/*
		if(!isConnected() || from != getOutwardsDir()) { return null; }
		*/
		return getTurbine().drain(getTankIndex(), maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		// TODO Commented temporarily to allow this thing to compile...
		/*
		if(!isConnected() || from != getOutwardsDir()) { return false; }
		*/
		if(flowSetting != FluidFlow.In) {
			return false;
		}
		
		return getTurbine().canFill(getTankIndex(), fluid);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		// TODO Commented temporarily to allow this thing to compile...
		/*
		if(!isConnected() || from != getOutwardsDir()) { return false; }
		*/
		return getTurbine().canDrain(getTankIndex(), fluid);
	}

	protected static final FluidTankInfo[] emptyTankInfoArray = new FluidTankInfo[0];
	
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		// TODO Commented temporarily to allow this thing to compile...
		/*
		if(!isConnected() || from != getOutwardsDir()) { return emptyTankInfoArray; }
		*/
		return new FluidTankInfo[] { getTurbine().getTankInfo(getTankIndex()) };
	}
	
	private int getTankIndex() {
		if(flowSetting == FluidFlow.In) { return MultiblockTurbine.TANK_INPUT; }
		else { return MultiblockTurbine.TANK_OUTPUT; }
	}
	
	public FluidFlow getFlowDirection() { return flowSetting; }
	
	// ITickableMultiblockPart
	
	@Override
	public void onMultiblockServerTick() {
		// Try to pump steam out, if an outlet
		if(pumpDestination == null || flowSetting != FluidFlow.Out)
			return;

		MultiblockTurbine turbine = getTurbine();
		FluidStack fluidToDrain = turbine.drain(MultiblockTurbine.TANK_OUTPUT, turbine.TANK_SIZE, false);
		
		if(fluidToDrain != null && fluidToDrain.amount > 0)
		{
			// TODO Commented temporarily to allow this thing to compile...
			/*
			fluidToDrain.amount = pumpDestination.fill(getOutwardsDir().getOpposite(), fluidToDrain, true);
			turbine.drain(MultiblockTurbine.TANK_OUTPUT, fluidToDrain, true);
			*/
		}
	}
	
	// INeighborUpdatableEntity
	@Override
	public void onNeighborBlockChange(World world, BlockPos position, IBlockState stateAtPosition, Block neighborBlock) {
		if(!world.isRemote) {
			checkForAdjacentTank();
		}
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
		if(worldObj.isRemote || flowSetting == FluidFlow.In) {
			return;
		}

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
}
