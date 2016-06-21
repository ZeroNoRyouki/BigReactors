package erogenousbeef.bigreactors.common.item;

import java.util.ArrayList;
import java.util.List;

import erogenousbeef.bigreactors.common.BRConfig;
import erogenousbeef.bigreactors.common.MetalType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import zero.mods.zerocore.lib.MetalSize;
import zero.mods.zerocore.lib.client.ICustomModelsProvider;

public class ItemBRMetal extends ItemBase implements ICustomModelsProvider {

	public ItemBRMetal(String itemName, MetalSize size) {

		super(itemName);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this._size = size;
		this._subItems = null;
	}

	public void onPostRegister() {

		super.onPostRegister();

		BRConfig.CONFIGURATION.load();

		boolean registerYelloriumAsUranium = BRConfig.CONFIGURATION.get("Recipes", "registerYelloriumAsUranium", true, "If set, yellorium will be registered in the ore dictionary as ingotUranium as well as ingotYellorium. Otherwise, it will only be registered as ingotYellorium. (Default: true)").getBoolean(true);

		// Register all generic ingots & dusts

		MetalType[] types = MetalType.values();
		int length = types.length;

		for (int i = 0; i < length; ++i)
			OreDictionary.registerOre(types[i].getOreDictionaryName(this._size), this.createItemStack(types[i], 1));

		// Add aliases, if appropriate

		if (registerYelloriumAsUranium) {

			ItemStack yellorium = this.createItemStack(MetalType.Yellorium, 1);
			OreDictionary.registerOre(this._size.oreDictionaryPrefix + "Uranium", yellorium);

			OreDictionary.registerOre(this._size.oreDictionaryPrefix + "Plutonium", yellorium);
		}

		BRConfig.CONFIGURATION.save();
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
	public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {

		int length;

		if (null == this._subItems) {

			MetalType[] types = MetalType.values();

			length = types.length;
			this._subItems = new ItemStack[length];

			for (int i = 0; i < length; ++i)
				this._subItems[i] = new ItemStack(item, 1, types[i].toMeta());
		}

		length = this._subItems.length;

		for (int i = 0; i < length; ++i)
			list.add(this._subItems[i]);
	}

	@Override
	public List<Pair<Integer, String>> getMetadataToModelMappings() {

		List<Pair<Integer, String>> mappings = new ArrayList();
		MetalType[] metals = MetalType.values();

		for (MetalType metal : metals)
			mappings.add(new ImmutablePair(Integer.valueOf(metal.toMeta()), String.format("metal=%s", metal.getName())));

		return mappings;
	}

	@Override
	public ResourceLocation getCustomResourceLocation() {
		return null;
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
	private ItemStack[] _subItems;
}
