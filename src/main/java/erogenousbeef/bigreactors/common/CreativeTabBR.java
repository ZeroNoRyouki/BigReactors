package erogenousbeef.bigreactors.common;

import erogenousbeef.bigreactors.common.block.OreType;
import erogenousbeef.bigreactors.init.BrBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabBR extends CreativeTabs {

	public CreativeTabBR(String label) {
		super(label);
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem() {
		return BrBlocks.brOre.createItemStack(OreType.Yellorite, 1);
	}
}
