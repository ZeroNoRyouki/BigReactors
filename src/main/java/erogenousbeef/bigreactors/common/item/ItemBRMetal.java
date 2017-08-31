package erogenousbeef.bigreactors.common.item;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.MetalType;
import it.zerono.mods.zerocore.lib.MetalSize;
import it.zerono.mods.zerocore.lib.item.ModItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ItemBRMetal extends ModItem {

	public ItemBRMetal(String itemName, MetalSize size) {

		super(itemName);
        this.setCreativeTab(BigReactors.TAB);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this._size = size;
		this._subItems = null;
	}

    @Override
	@SideOnly(Side.CLIENT)
    public void onRegisterModels() {

		ResourceLocation location = this.getRegistryName();

		for (MetalType metal : MetalType.values())
			ModelLoader.setCustomModelResourceLocation(this, metal.toMeta(),
					new ModelResourceLocation(location, String.format("metal=%s", metal.getName())));
	}

    @Override
    public void onRegisterOreDictionaryEntries() {

		// Register all generic ingots & dusts

		MetalType[] types = MetalType.values();
		int length = types.length;

		for (int i = 0; i < length; ++i)
			OreDictionary.registerOre(types[i].getOreDictionaryName(this._size), this.createItemStack(types[i], 1));

		// Add aliases, if appropriate

		if (BigReactors.CONFIG.registerYelloriumAsUranium) {

			OreDictionary.registerOre(this._size.oreDictionaryPrefix + "Uranium", this.createItemStack(MetalType.Yellorium, 1));
			OreDictionary.registerOre(this._size.oreDictionaryPrefix + "Plutonium", this.createItemStack(MetalType.Blutonium, 1));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + MetalType.fromMeta(stack.getMetadata()).getName();
	}

	@Override
	public void getSubItems(CreativeTabs creativeTabs, NonNullList<ItemStack> list) {

		if (creativeTabs != this.getCreativeTab())
			return;

		if (null == this._subItems) {

			MetalType[] types = MetalType.VALUES;
			int length = types.length;

			this._subItems = new ArrayList<>(length);

			for (int i = 0; i < length; ++i)
				this._subItems.add(new ItemStack(this, 1, types[i].toMeta()));
		}

		list.addAll(this._subItems);
	}

	/*
	public static boolean isFuel(int itemDamage) {
		return itemDamage == 0 || itemDamage == 3;
	}

	public static boolean isWaste(int itemDamage) {
		return itemDamage == 1;
	}
	
	public static boolean isGraphite(int itemDamage) {
		return itemDamage == 2;
	}
	*/

	public ItemStack createItemStack(MetalType metal, int amount) {

		return new ItemStack(this, amount, metal.toMeta());
	}

	private MetalSize _size;
	private List<ItemStack> _subItems;
}