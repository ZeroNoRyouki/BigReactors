package erogenousbeef.bigreactors.gui.slot;

import erogenousbeef.bigreactors.api.registry.Reactants;
import it.zerono.mods.zerocore.util.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotReactorFuel extends SlotItemHandler {

	boolean fuel = true;
	
	public SlotReactorFuel(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean fuel) {

		super(itemHandler, index, xPosition, yPosition);
		this.fuel = fuel;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		if (ItemHelper.stackIsEmpty(stack))
			return false;

		return fuel ? Reactants.isFuel(stack) : Reactants.isWaste(stack);
	}
}
