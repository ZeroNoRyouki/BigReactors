package erogenousbeef.bigreactors.utils;

import it.zerono.mods.zerocore.util.ItemHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

// Strongly based off Powercrystals' InventoryManager
public class InventoryHelper {
	private IInventory inventory;

	public InventoryHelper(IInventory inventory) {
		this.inventory = inventory;
	}

	protected boolean canAdd(ItemStack stack, int slot) {
		return ItemHelper.stackIsValid(stack) && inventory.isItemValidForSlot(slot, stack);
	}

	protected boolean canRemove(ItemStack stack, int slot) {
		return inventory != null;
	}

	/**
	 * Add an item to a wrapped inventory
	 * 
	 * @param stack
	 *            Item stack to place into the wrapped inventory
	 * @return Stack representing the remaining items
	 */
	public ItemStack addItem(ItemStack stack) {

		if (ItemHelper.stackIsEmpty(stack))
			return ItemHelper.stackEmpty();

		int quantitytoadd = ItemHelper.stackGetSize(stack);
		ItemStack remaining = ItemHelper.stackFrom(stack);
		int[] candidates = getSlots();
		
		if(candidates.length == 0) {
			return stack;
		}

		for (int candidateSlot : candidates) {

			int maxStackSize = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
			ItemStack s = inventory.getStackInSlot(candidateSlot);

			if (ItemHelper.stackIsEmpty(s)) {

				ItemStack add = ItemHelper.stackFrom(stack, Math.min(quantitytoadd, maxStackSize));

				if (canAdd(add, candidateSlot)) {

					quantitytoadd -= ItemHelper.stackGetSize(add);
					inventory.setInventorySlotContents(candidateSlot, add);
					inventory.markDirty();
				}
			} else if (StaticUtils.Inventory.areStacksEqual(s, stack)) {

				ItemStack add = ItemHelper.stackFrom(stack, Math.min(quantitytoadd, maxStackSize - ItemHelper.stackGetSize(s)));
				final int addSize = ItemHelper.stackGetSize(add);

				if (addSize > 0 && canAdd(add, candidateSlot)) {

					ItemHelper.stackAdd(s, addSize);
					quantitytoadd -= addSize;
					inventory.setInventorySlotContents(candidateSlot, s);
					inventory.markDirty();
				}
			}
			if (quantitytoadd == 0) {
				break;
			}
		}

		ItemHelper.stackSetSize(remaining, quantitytoadd);

		return ItemHelper.stackGetSize(remaining) == 0 ? ItemHelper.stackEmpty() : remaining;
	}

	private final static int[] noSlots = new int[0];
	protected int[] getSlots() {
		if(inventory == null) { return noSlots; }
		int[] slots = new int[inventory.getSizeInventory()];
		for (int i = 0; i < slots.length; i++) {
			slots[i] = i;
		}
		return slots;
	}
}
