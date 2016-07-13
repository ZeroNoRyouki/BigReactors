package erogenousbeef.bigreactors.common.multiblock.tileentity;

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
import erogenousbeef.bigreactors.common.multiblock.helpers.CoolantContainer;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.util.WorldHelper;

public class TileEntityReactorCoolantPort extends TileEntityReactorPart implements IFluidHandler, INeighborUpdatableEntity, ITickableMultiblockPart {

	boolean inlet;
	IFluidHandler pumpDestination;
	
	public TileEntityReactorCoolantPort() {
		super();
		
		inlet = true;
		pumpDestination = null;
	}
	
	public boolean isInlet() { return inlet; }

	public void setInlet(boolean shouldBeInlet, boolean markDirty) {
		if(inlet == shouldBeInlet) { return; }

		inlet = shouldBeInlet;
		WorldHelper.notifyBlockUpdate(worldObj, this.getPos(), null, null);
		
		if(!worldObj.isRemote) {
			if(!inlet) {
				checkForAdjacentTank();
			}

			if(markDirty) {
				markDirty();
			}
			else {
				notifyNeighborsOfTileChange();
			}
		}
		else {
			notifyNeighborsOfTileChange();
		}
	}

	public void toggleInlet(boolean markDirty) {

		this.setInlet(!this.inlet, markDirty);
	}

	/*
	// MultiblockTileEntityBase
	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		
		packetData.setBoolean("inlet", inlet);
	}
	
	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		
		if(packetData.hasKey("inlet")) {
			setInlet(packetData.getBoolean("inlet"), false);
		}
	}*/
	
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

	/*
	// TileEntity
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if(tag.hasKey("inlet")) {
			inlet = tag.getBoolean("inlet");
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("inlet", inlet);
	}*/

	protected void loadFromNBT(NBTTagCompound data, boolean fromPacket) {

		super.loadFromNBT(data, fromPacket);

		if (!fromPacket) {

			if (data.hasKey("inlet"))
				this.inlet = data.getBoolean("inlet");

		} else {

			if (data.hasKey("inlet"))
				setInlet(data.getBoolean("inlet"), false);
		}
	}

	protected void saveToNBT(NBTTagCompound data, boolean toPacket) {

		super.saveToNBT(data, toPacket);

		if (!toPacket) {

			data.setBoolean("inlet", this.inlet);

		} else {

			data.setBoolean("inlet", this.inlet);

		}
	}

	// IFluidHandler
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {

		if (!isConnected() || !inlet || null == from || from != this.getOutwardFacing())
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

		if (!isConnected() || null == from || from != this.getOutwardFacing() || !inlet)
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
		if(pumpDestination == null || isInlet())
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
		if(inlet) {
			return CoolantContainer.COLD;
		}
		else {
			return CoolantContainer.HOT;
		}
	}

	protected void checkForAdjacentTank() {

		this.pumpDestination = null;

		if (this.worldObj.isRemote || this.isInlet())
			return;

		EnumFacing out = this.getOutwardFacing();

		if (null == out)
			return;

		TileEntity neighbor = this.worldObj.getTileEntity(this.getPos().offset(out));

		if (neighbor instanceof IFluidHandler)
			this.pumpDestination = (IFluidHandler)neighbor;
	}
}
