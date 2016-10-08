package erogenousbeef.bigreactors.common;

import erogenousbeef.bigreactors.init.BrBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabBR extends CreativeTabs {

	public CreativeTabBR(String label)
	{
		super(label);
	}

	public Item getTabIconItem()
	{
		return Item.getItemFromBlock(BrBlocks.brOre);
	}
}
