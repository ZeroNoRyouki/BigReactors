package erogenousbeef.bigreactors.common.item;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.config.Config;
import erogenousbeef.bigreactors.init.BrItems;
import it.zerono.mods.zerocore.lib.MetalSize;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class ItemBRMetal extends ItemBase {

	public ItemBRMetal(String itemName, MetalSize size) {

		super(itemName);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this._size = size;
		this._subItems = null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onPostClientRegister() {

		ResourceLocation location = this.getRegistryName();

		for (MetalType metal : MetalType.values())
			ModelLoader.setCustomModelResourceLocation(this, metal.toMeta(),
					new ModelResourceLocation(location, String.format("metal=%s", metal.getName())));
	}

	@Override
	public void registerOreDictionaryEntries() {

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
	public void registerRecipes() {

		final Config configs = BigReactors.CONFIG;
		final ItemStack ingotGraphite = OreDictionaryHelper.getOre("ingotGraphite");
		final ItemStack ingotCyanite = OreDictionaryHelper.getOre("ingotCyanite");

		// Graphite & Cyanite

		// -- Coal -> Graphite
		if (configs.registerCoalForSmelting)
			GameRegistry.addSmelting(Items.COAL, ingotGraphite, 1);

		// -- Charcoal -> Graphite
		if (configs.registerCharcoalForSmelting)
			GameRegistry.addSmelting(new ItemStack(Items.COAL, 1, 1), ingotGraphite, 1);

		// -- Gravel + Coal -> Graphite
		if (configs.registerGraphiteCoalCraftingRecipes)
			GameRegistry.addRecipe(new ShapedOreRecipe(ingotGraphite, "GCG", 'G', Blocks.GRAVEL, 'C',
					new ItemStack(Items.COAL, 1, 0)));

		// -- Gravel + Charcoal -> Graphite
		if (configs.registerGraphiteCharcoalCraftingRecipes)
			GameRegistry.addRecipe(new ShapedOreRecipe(ingotGraphite, "GCG", 'G', Blocks.GRAVEL, 'C',
					new ItemStack(Items.COAL, 1, 1)));

		// -- Yellorium ingot + Sand -> Cyanite
		if (configs.enableCyaniteFromYelloriumRecipe)
			GameRegistry.addRecipe(new ShapelessOreRecipe(ingotCyanite, configs.recipeYelloriumIngotName, Blocks.SAND ));

	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return super.getUnlocalizedName() + "." + MetalType.fromMeta(stack.getMetadata()).getName();
	}

	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {

		if (null == this._subItems) {

			MetalType[] types = MetalType.VALUES;
			int length = types.length;

			this._subItems = new ArrayList<>(length);

			for (int i = 0; i < length; ++i)
				this._subItems.add(new ItemStack(item, 1, types[i].toMeta()));
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
