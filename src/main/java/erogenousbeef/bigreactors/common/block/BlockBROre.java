package erogenousbeef.bigreactors.common.block;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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

import java.util.List;

public class BlockBROre extends BlockBR {

	public BlockBROre(String blockName) {

		super(blockName, Material.ROCK);
		this._subBlocks = null;
	}

	@Override
	public void onPostRegister() {
		GameRegistry.register(new ItemBlockOre(this).setRegistryName(this.getRegistryName()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onPostClientRegister() {

		ResourceLocation location = this.getRegistryName();
		Item item = Item.getItemFromBlock(this);

		for (OreType ore : OreType.values())
			ModelLoader.setCustomModelResourceLocation(item, ore.toMeta(),
					new ModelResourceLocation(location, String.format("ore=%s", ore.getName())));
	}

	@Override
	public void registerOreDictionaryEntries() {

		ItemStack stack = this.createItemStack();

		OreDictionary.registerOre("oreYellorite", stack);
		OreDictionary.registerOre("oreYellorium", stack); // For convenience of mods which fiddle with recipes
	}

	@Override
	public void registerRecipes() {

		// - Yellorium

		ItemStack product;

		if (BigReactors.CONFIG.registerYelloriteSmeltToUranium) {

			product = OreDictionaryHelper.getOre("ingotUranium");

			if (null == product) {

				BRLog.warning("Config value registerYelloriteSmeltToUranium is set to True, but there are no ores registered as ingotUranium in the ore dictionary! Falling back to using standard yellorium only.");
				product = OreDictionaryHelper.getOre("ingotYellorium");
			}

		} else {

			product = OreDictionaryHelper.getOre("ingotYellorium");
		}

		GameRegistry.addSmelting(this, product, 0.5f);
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

		// so far we only have Yellorite ...
		if (null == this._subBlocks) {

			this._subBlocks = new ItemStack(item, 1, OreType.Yellorite.toMeta());
		}

		list.add(this._subBlocks);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(Properties.ORE, OreType.fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(Properties.ORE).toMeta();
	}

	@Override
	public int damageDropped(IBlockState state) {

		return state.getValue(Properties.ORE).toMeta();
	}

	@Override
	protected void buildBlockState(BlockStateContainer.Builder builder) {

		builder.add(Properties.ORE);
	}

	private static class ItemBlockOre extends ItemBlock {

		public ItemBlockOre(Block block) {

			super(block);
			this.setHasSubtypes(true);
			this.setMaxDamage(0);
		}

		@Override
		public String getUnlocalizedName(ItemStack stack) {

			return super.getUnlocalizedName() + "." + OreType.fromMeta(stack.getMetadata()).getName();
		}

		@Override
		public int getMetadata(int meta) {

			return meta;
		}
	}

	private ItemStack _subBlocks;
}
