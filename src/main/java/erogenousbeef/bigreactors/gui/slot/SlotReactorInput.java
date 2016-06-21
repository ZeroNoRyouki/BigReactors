package erogenousbeef.bigreactors.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import erogenousbeef.bigreactors.api.registry.Reactants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotReactorInput extends /*Slot*/SlotItemHandler {

	boolean fuel = true;
	
	public SlotReactorInput(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean fuel) {

		super(itemHandler, index, xPosition, yPosition);
		this.fuel = fuel;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if(stack == null) { return false; }
		
		if(fuel) {
			return Reactants.isFuel(stack);
		}
		else {
			return Reactants.isWaste(stack);
		}
	}
}
