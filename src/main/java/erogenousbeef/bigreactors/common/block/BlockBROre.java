package erogenousbeef.bigreactors.common.block;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import zero.temp.RecipeHelper2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockBROre extends ModBlock {

	public BlockBROre(String blockName) {

		super(blockName, Material.ROCK);
        this.setCreativeTab(BigReactors.TAB);
        this.setHardness(2.0f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRegisterModels() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}

	@SuppressWarnings("ConstantConditions")
    @Override
    public void onRegisterOreDictionaryEntries() {

		if (null != BrBlocks.oreYellorite) {

            final ItemStack ore = BrBlocks.oreYellorite.createItemStack();

            OreDictionary.registerOre("oreYellorite", ore);
            OreDictionary.registerOre("oreYellorium", ore); // For convenience of mods which fiddle with recipes
        }

		if (null != BrBlocks.oreAnglesite) {
            OreDictionary.registerOre("oreAnglesite", BrBlocks.oreAnglesite.createItemStack());
        }

		if (null != BrBlocks.oreBenitoite) {
            OreDictionary.registerOre("oreBenitoite", BrBlocks.oreBenitoite.createItemStack());
        }
	}

    @Override
    public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {

		// - Yellorium

		ItemStack product;
		final ItemStack ore = BrBlocks.oreYellorite.createItemStack();

		if (BigReactors.CONFIG.registerYelloriteSmeltToUranium) {

			product = OreDictionaryHelper.getOre("ingotUranium");

			if (null == product) {

				BRLog.warning("Config value registerYelloriteSmeltToUranium is set to True, but there are no ores registered as ingotUranium in the ore dictionary! Falling back to using standard yellorium only.");
				product = OreDictionaryHelper.getOre("ingotYellorium");
			}

		} else {

			product = OreDictionaryHelper.getOre("ingotYellorium");
		}

		RecipeHelper2.addSmelting(ore, product, 0.5f);
	}

	@SuppressWarnings("ConstantConditions")
	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {

		final Block ore = state.getBlock();

		if (BrBlocks.oreAnglesite == ore) {
			return BrItems.mineralAnglesite;
		} else if (BrBlocks.oreBenitoite == ore) {
			return BrItems.mineralBenitoite;
		}

		return super.getItemDropped(state, rand, fortune);
	}

	/**
	 * Called when a user uses the creative pick block button on this block
	 *
	 * @param target The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
	 */
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return this.createItemStack();
	}
}
