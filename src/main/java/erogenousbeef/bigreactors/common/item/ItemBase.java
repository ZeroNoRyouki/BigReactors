package erogenousbeef.bigreactors.common.item;

import it.zerono.mods.zerocore.lib.IGameObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBase extends Item implements IGameObject {

	public ItemBase(String itemName) {

		this.setRegistryName(itemName);
		this.setUnlocalizedName(this.getRegistryName().toString());
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

	@Override
	public void onPostRegister() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onPostClientRegister() {
	}

	@Override
	public void registerOreDictionaryEntries() {
	}

	@Override
	public void registerRecipes() {
	}

	@Override
	public int getMetadata(int metadata) {
		return metadata;
	}
}
