package erogenousbeef.bigreactors.common.block;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.MineralType;
import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.init.BrItems;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

		ItemStack ore = this.createItemStack(OreType.Yellorite, 1);

		OreDictionary.registerOre("oreYellorite", ore);
		OreDictionary.registerOre("oreYellorium", ore); // For convenience of mods which fiddle with recipes
	}

	@Override
	public void registerRecipes() {

		// - Yellorium

		ItemStack product;
		ItemStack ore = this.createItemStack(OreType.Yellorite, 1);

		if (BigReactors.CONFIG.registerYelloriteSmeltToUranium) {

			product = OreDictionaryHelper.getOre("ingotUranium");

			if (null == product) {

				BRLog.warning("Config value registerYelloriteSmeltToUranium is set to True, but there are no ores registered as ingotUranium in the ore dictionary! Falling back to using standard yellorium only.");
				product = OreDictionaryHelper.getOre("ingotYellorium");
			}

		} else {

			product = OreDictionaryHelper.getOre("ingotYellorium");
		}

		GameRegistry.addSmelting(ore, product, 0.5f);
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

		if (null == this._subBlocks) {

			OreType[] types = OreType.VALUES;
			int length = types.length;

			this._subBlocks = new ArrayList<>(length);

			for (int i = 0; i < length; ++i)
				this._subBlocks.add(new ItemStack(item, 1, types[i].toMeta()));
		}

		list.addAll(this._subBlocks);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(Properties.ORE, OreType.fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(Properties.ORE).toMeta();
	}


	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {

		OreType type = state.getValue(Properties.ORE);
		MineralType mineral = type.getMineralDropped();

		return null == mineral ? super.getItemDropped(state, rand, fortune) : BrItems.minerals;
	}

	@Override
	public int damageDropped(IBlockState state) {

		OreType type = state.getValue(Properties.ORE);
		MineralType mineral = type.getMineralDropped();

		return null == mineral ? super.damageDropped(state) : mineral.toMeta();
	}

	/**
	 * Called when a user uses the creative pick block button on this block
	 *
	 * @param target The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
	 */
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return this.createItemStack(state.getValue(Properties.ORE), 1);
	}

	public ItemStack createItemStack(OreType type, int amount) {
		return new ItemStack(this, amount, type.toMeta());
	}

	public IBlockState getStateFromType(OreType type) {
		return this.getDefaultState().withProperty(Properties.ORE, type);
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

	private List<ItemStack> _subBlocks;
}
