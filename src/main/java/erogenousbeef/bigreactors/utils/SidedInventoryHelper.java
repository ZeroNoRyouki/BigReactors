package erogenousbeef.bigreactors.utils;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class SidedInventoryHelper extends InventoryHelper {

	private ISidedInventory sidedInventory;
	private EnumFacing side;
	
	public SidedInventoryHelper(ISidedInventory inventory, EnumFacing side) {
		super(inventory);
		
		this.sidedInventory = inventory;
		this.side = side;
	}
	
	@Override
	protected boolean canAdd(ItemStack stack, int slot) {
		return sidedInventory.canInsertItem(slot, stack, this.side);
	}
	
	@Override
	protected boolean canRemove(ItemStack stack, int slot) {
		return sidedInventory.canExtractItem(slot, stack, this.side);
	}
	
	@Override
	public int[] getSlots() {
		return sidedInventory.getSlotsForFace(this.side);
	}


}
