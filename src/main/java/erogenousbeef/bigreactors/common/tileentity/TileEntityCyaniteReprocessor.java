package erogenousbeef.bigreactors.common.tileentity;

import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.client.gui.GuiCyaniteReprocessor;
import erogenousbeef.bigreactors.common.tileentity.base.TileEntityPoweredInventoryFluid;
import erogenousbeef.bigreactors.gui.container.ContainerCyaniteReprocessor;
import erogenousbeef.bigreactors.utils.StaticUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class TileEntityCyaniteReprocessor extends TileEntityPoweredInventoryFluid {

	public static final int SLOT_INLET = 0;
	public static final int SLOT_OUTLET = 1;
	public static final int NUM_SLOTS = 2;

	public static final int FLUIDTANK_WATER = 0;
	public static final int NUM_TANKS = 1;
	
	protected static final int FLUID_CONSUMED = FluidContainerRegistry.BUCKET_VOLUME * 1;
	protected static final int INGOTS_CONSUMED = 2;
	
	public TileEntityCyaniteReprocessor() {
		super();
		
		// Do not transmit energy from the internal buffer.
		m_ProvidesEnergy = false;
	}

	@Override
	public int getSizeInventory() {
		return NUM_SLOTS;
	}

	// TODO Commented temporarily to allow this thing to compile...
	/*
	@Override
	public String getInventoryName() {
		return "Cyanite Reprocessor";
	}
	*/

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		if(itemstack == null) { return true; }
		
		if(slot == SLOT_OUTLET) {
			return Reactants.isFuel(itemstack);
		}
		else if(slot == SLOT_INLET) {
			return Reactants.isWaste(itemstack);
		}

		return false;
	}

	@Override
	public int getMaxEnergyStored() {
		return 10000;
	}

	@Override
	public int getCycleEnergyCost() {
		return 2000;
	}

	@Override
	public int getCycleLength() {
		return 200; // 10 seconds / 20tps
	}

	@Override
	public boolean canBeginCycle() {
		FluidStack fluid = drain(0, FLUID_CONSUMED, false);
		if(fluid == null || fluid.amount < FLUID_CONSUMED) {
			return false;
		}

		if(_inventories[SLOT_INLET] != null && _inventories[SLOT_INLET].stackSize >= INGOTS_CONSUMED) {
			if(_inventories[SLOT_OUTLET] != null && _inventories[SLOT_OUTLET].stackSize >= getInventoryStackLimit()) {
				return false;
			}
			return true;
		}

		return false;
	}

	@Override
	public void onPoweredCycleBegin() {
	}

	@Override
	public void onPoweredCycleEnd() {
		if(_inventories[SLOT_OUTLET] != null) {
			if(consumeInputs()) {
				_inventories[SLOT_OUTLET].stackSize += 1;
			}
		}
		else {
			// TODO: Make this query the input for the right type of output to create.
			// TODO Commented temporarily to allow this thing to compile...
			ArrayList<ItemStack> candidates = null;// OreDictionaryArbiter.getOres("ingotBlutonium");
			if(candidates == null || candidates.isEmpty()) {
				// WTF?
				return;
			}
			
			if(consumeInputs()) {
				_inventories[SLOT_OUTLET] = candidates.get(0).copy();
				_inventories[SLOT_OUTLET].stackSize = 1;
			}
		}
		
		distributeItemsFromSlot(SLOT_OUTLET);
		markChunkDirty();
	}
	
	private boolean consumeInputs() {
		_inventories[SLOT_INLET] = StaticUtils.Inventory.consumeItem(_inventories[SLOT_INLET], INGOTS_CONSUMED);
		drain(0, FLUID_CONSUMED, true);
		
		return true;
	}

	@Override
	public int getNumTanks() {
		return NUM_TANKS;
	}

	@Override
	public int getTankSize(int tankIndex) {
		return FluidContainerRegistry.BUCKET_VOLUME * 5;
	}

	@Override
	protected boolean isFluidValidForTank(int tankIdx, FluidStack type) {
		if(type == null) { return false; }
		return type.getFluid() == FluidRegistry.WATER;
	}
	
	/// BeefGUI
	@SideOnly(Side.CLIENT)
	@Override
	public GuiScreen getGUI(EntityPlayer player) {
		return new GuiCyaniteReprocessor(getContainer(player), this);
	}

	@Override
	public Container getContainer(EntityPlayer player) {
		return new ContainerCyaniteReprocessor(this, player);
	}
	
	@Override
	protected int getDefaultTankForFluid(Fluid fluid) {
		if(FluidRegistry.WATER == fluid)
			return 0;
		else
			return FLUIDTANK_NONE;
	}
	
	// IReconfigurableSides & IBeefReconfigurableSides
	//TODO textures
	/*
	@SideOnly(Side.CLIENT)
	public IIcon getIconForSide(int side) {
		if(side == facing) {
			// This should never happen
			return getBlockType().getIcon(side, getBlockMetadata());
		}

		int exposure = getExposure(side);
		
		switch(exposure) {
		case 0:
			return ClientProxy.CommonBlockIcons.getIcon(ClientProxy.CommonBlockIcons.ITEM_RED);
		case 1:
			return ClientProxy.CommonBlockIcons.getIcon(ClientProxy.CommonBlockIcons.ITEM_GREEN);
		case 2:
			return ClientProxy.CommonBlockIcons.getIcon(ClientProxy.CommonBlockIcons.FLUID_BLUE);
		default:
			return ClientProxy.CommonBlockIcons.getIcon(ClientProxy.CommonBlockIcons.DEFAULT);
		}
	}
	*/
	
	@Override
	public int getNumConfig(EnumFacing side) {
		if(facing == side) {
			return 0;
		}
		else {
			return 3;
		}
	}

	@Override
	public int getExposedTankFromSide(int side) {
		int exposure = getExposure(EnumFacing.VALUES[side]);
		if(exposure == 2) { return FLUIDTANK_WATER; }
		return FLUIDTANK_NONE;
	}

	@Override
	protected int getExposedInventorySlotFromSide(int side) {
		int exposure = getExposure(EnumFacing.VALUES[side]);
		if(exposure == 0) { return SLOT_INLET; }
		if(exposure == 1) { return SLOT_OUTLET; }
		return SLOT_NONE;
	}


	// TODO fake imp! (start)

	@Override
	public Object getIconForSide(int referenceSide) {
		return null;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}


	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

	}

	// fake imp (end)
}
