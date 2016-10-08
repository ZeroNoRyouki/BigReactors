package erogenousbeef.bigreactors.common.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import it.zerono.mods.zerocore.lib.MetalSize;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class BlockBRMetal extends BlockBR {

	public BlockBRMetal(String blockName) {

		super(blockName, Material.IRON);
		this._subBlocks = null;
	}

	@Override
	public void onPostRegister() {
		GameRegistry.register(new ItemBlockMetal(this).setRegistryName(this.getRegistryName()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onPostClientRegister() {

		ResourceLocation location = this.getRegistryName();
		Item item = Item.getItemFromBlock(this);

		for (MetalType metal : MetalType.values())
			ModelLoader.setCustomModelResourceLocation(item, metal.toMeta(),
					new ModelResourceLocation(location, String.format("metal=%s", metal.getName())));
	}

	@Override
	public void registerOreDictionaryEntries() {

		MetalType[] metals = MetalType.values();
		int length = metals.length;

		for (int i = 0; i < length; ++i)
			OreDictionary.registerOre(metals[i].getOreDictionaryName(MetalSize.Block), this.createItemStack(metals[i], 1));
	}

	@Override
	public void registerRecipes() {

		// Metal blocks & ingots

		ItemStack block, ingot;

		for (MetalType metal : MetalType.VALUES) {

			block = this.createItemStack(metal, 1);
			ingot = BrItems.ingotMetals.createItemStack(metal, 1);

			GameRegistry.addShapelessRecipe(block, ingot, ingot, ingot, ingot, ingot, ingot, ingot, ingot, ingot);
			ingot.stackSize = 9;
			GameRegistry.addShapelessRecipe(ingot, block);
		}

		// Ludicrite block. Because.

		final ItemStack ludicriteBlock = this.createItemStack(MetalType.Ludicrite, 1);

		GameRegistry.addRecipe(new ShapedOreRecipe(ludicriteBlock, "BPB", "ENE", "BPB",
				'N', Items.NETHER_STAR, 'P', Items.ENDER_PEARL, 'E', Blocks.EMERALD_BLOCK,
				'B', BigReactors.CONFIG.recipeBlutoniumIngotName));

		if (OreDictionaryHelper.doesOreNameExist("blockEnderium")) {

			// Ok, how about some ludicrous shit here. Enderium and blaze rods. Have fun, bucko.
			GameRegistry.addRecipe(new ShapedOreRecipe(ludicriteBlock, "BRB", "E E", "BRB",
					'B', BigReactors.CONFIG.recipeBlutoniumIngotName, 'R', Items.BLAZE_ROD, 'E', "blockEnderium"));
		}
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

		if (null == this._subBlocks) {

			MetalType[] types = MetalType.VALUES;
			int length = types.length;

			this._subBlocks = new ArrayList<>(length);

			for (int i = 0; i < length; ++i)
				this._subBlocks.add(new ItemStack(item, 1, types[i].toMeta()));
		}

		list.addAll(this._subBlocks);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(Properties.METAL, MetalType.fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(Properties.METAL).toMeta();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}

	public ItemStack createItemStack(MetalType type, int amount) {
		return new ItemStack(this, amount, type.toMeta());
	}

	public static boolean isMetal(IBlockState state, MetalType metal) {
		return BrBlocks.blockMetals == state.getBlock() && state.getValue(Properties.METAL) == metal;
	}

	@Override
	protected void buildBlockState(BlockStateContainer.Builder builder) {
		builder.add(Properties.METAL);
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

	private List<ItemStack> _subBlocks;
}
