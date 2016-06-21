package erogenousbeef.bigreactors.common.block;

import java.util.ArrayList;
import java.util.List;

import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.init.BrItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import erogenousbeef.bigreactors.common.item.ItemBRMetal;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import zero.mods.zerocore.lib.MetalSize;
import zero.mods.zerocore.lib.client.ICustomModelsProvider;

public class BlockBRMetal extends BlockBR implements ICustomModelsProvider {
	/*
	public static final int METADATA_YELLORIUM 	= 0;
	public static final int METADATA_CYANITE 	= 1;
	public static final int METADATA_GRAPHITE 	= 2;
	public static final int METADATA_BLUTONIUM 	= 3;
	public static final int METADATA_LUDICRITE = 4;
	*/
	/*
	private static final String[] _subBlocks = new String[] { "blockYellorium", "blockCyanite", "blockGraphite", "blockBlutonium", "blockLudicrite" };
	private static final String[] _materials = new String[] { "Yellorium", "Cyanite", "Graphite", "Blutonium", "Ludicrite" };
	// TODO blockstate
	//private IIcon[] _icons = new IIcon[_subBlocks.length];
	private static final int NUM_BLOCKS = _subBlocks.length;
	*/
	
	public BlockBRMetal(String blockName) {

		super(blockName, Material.iron);
		this._subBlocks = null;
	}

	@Override
	public void onPostRegister() {

		GameRegistry.register(new ItemBlockMetal(this).setRegistryName(this.getRegistryName()));

		MetalType[] metals = MetalType.values();
		int length = metals.length;

		for (int i = 0; i < length; ++i)
			OreDictionary.registerOre(metals[i].getOreDictionaryName(MetalSize.Block), this.createItemStack(metals[i], 1));
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

		int length;

		if (null == this._subBlocks) {

			MetalType[] types = MetalType.values();

			length = types.length;
			this._subBlocks = new ItemStack[length];

			for (int i = 0; i < length; ++i)
				this._subBlocks[i] = new ItemStack(item, 1, types[i].toMeta());
		}

		length = this._subBlocks.length;

		for (int i = 0; i < length; ++i)
			list.add(this._subBlocks[i]);
	}

	@Override
	public ResourceLocation getCustomResourceLocation() {
		return null;
	}

	@Override
	public List<Pair<Integer, String>> getMetadataToModelMappings() {

		List<Pair<Integer, String>> mappings = new ArrayList();
		MetalType[] metals = MetalType.values();

		for (MetalType metal : metals)
			mappings.add(new ImmutablePair(Integer.valueOf(metal.toMeta()), String.format("metal=%s", metal.getName())));

		return mappings;
	}

	/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerCustomModels(Block newlyRegisteredBlock) {

		final ResourceLocation name = this.getRegistryName();
		final Item item = Item.getItemFromBlock(newlyRegisteredBlock);

		for (final MetalType type : MetalType.values())
			ModelLoader.setCustomModelResourceLocation(item, type.toMeta(),
					new ModelResourceLocation(name, String.format("metal=%s", type.getName())));
	}*/

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(METAL, MetalType.fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(METAL).toMeta();
	}

	@Override
	public int damageDropped(IBlockState state) {

		return state.getValue(METAL).toMeta();
	}

	public ItemStack createItemStack(MetalType type, int amount) {

		return new ItemStack(this, amount, type.toMeta());
	}

	// TODO move this out of this class
	public void registerIngotRecipes() {

		MetalType[] metals = MetalType.values();
		ItemStack block, ingot;

		for (MetalType metal : metals) {

			block = this.createItemStack(metal, 1);
			ingot = BrItems.ingotMetals.createItemStack(metal, 1);

			GameRegistry.addShapelessRecipe(block, ingot, ingot, ingot, ingot, ingot, ingot, ingot, ingot, ingot);
			ingot.stackSize = 9;
			GameRegistry.addShapelessRecipe(ingot, block);
		}
	}

	@Override
	protected void buildBlockState(BlockStateContainer.Builder builder) {

		builder.add(METAL);
	}

	private static class ItemBlockMetal extends ItemBlock {

		public ItemBlockMetal(Block block) {

			super(block);
			this.setHasSubtypes(true);
			this.setMaxDamage(0);
		}

		@Override
		public String getUnlocalizedName(ItemStack stack) {

			return super.getUnlocalizedName() + "." + MetalType.fromMeta(stack.getMetadata()).getName();
		}

		@Override
		public int getMetadata(int meta) {

			return meta;
		}
	}

	private ItemStack[] _subBlocks;

	private static final PropertyEnum<MetalType> METAL = PropertyEnum.create("metal", MetalType.class);
}
