package erogenousbeef.bigreactors.gui.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotRemoveOnly extends SlotItemHandler {

	public SlotRemoveOnly(IItemHandler itemHandler, int index, int xPosition, int yPosition) {

		super(itemHandler, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
