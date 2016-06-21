package erogenousbeef.bigreactors.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.common.BigReactors;

public class ItemBase extends Item {

	public ItemBase(String itemName) {

		this.setRegistryName(itemName);
		this.setUnlocalizedName(this.getRegistryName().toString());
		this.setCreativeTab(BigReactors.TAB);
	}

	public ItemStack createItemStack() {

		return this.createItemStack(1, 0);
	}
	public ItemStack createItemStack(int amount) {

		return this.createItemStack(amount, 0);
	}

	public ItemStack createItemStack(int amount, int meta) {

		return new ItemStack(this, amount, meta);
	}

	public void onPostRegister() {
	}

	@Override
	public int getMetadata(int metadata) {
		return metadata;
	}
}
