package erogenousbeef.bigreactors.common.block;

import erogenousbeef.bigreactors.common.BigReactors;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

public class BlockBRMetal extends ModBlock {

	public BlockBRMetal(@Nonnull final String blockName, @Nonnull final String oreDictionaryName) {

		super(blockName, Material.IRON, oreDictionaryName);
        this.setCreativeTab(BigReactors.TAB);
        this.setHardness(2.0f);
	}

	@Override
    public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {

		//TODO add recipes
		BigReactors.getLogger().warn("ADD RECIPES for Metals");

		// Metal blocks & ingots
		/*
		ItemStack block, ingot;
		final ResourceLocation group = BigReactors.createResourceLocation("metals");

		for (MetalType metal : MetalType.VALUES) {
			
			block = this.createItemStack(metal, 1);
			ingot = BrItems.ingotMetals.createItemStack(metal, 1);

			RecipeHelper2.addShapeless(registry, block,
					ingot, ingot, ingot, ingot, ingot, ingot, ingot, ingot, ingot);

			ingot = ItemHelper.stackFrom(ingot, 9);
			RecipeHelper2.addShapeless(registry, ingot, block);
        }

		// Ludicrite block. Because.

		final ItemStack ludicriteBlock = this.createItemStack(MetalType.Ludicrite, 1);

		RecipeHelper2.addShaped(registry, ludicriteBlock, "BPB", "ENE", "BPB",
				'N', Items.NETHER_STAR, 'P', Items.ENDER_PEARL, 'E', Blocks.EMERALD_BLOCK,
				'B', BigReactors.CONFIG.recipeBlutoniumIngotName);


		if (OreDictionaryHelper.doesOreNameExist("blockEnderium")) {

			// Ok, how about some ludicrous shit here. Enderium and blaze rods. Have fun, bucko.
			RecipeHelper2.addShaped(registry, ludicriteBlock, "BRB", "E E", "BRB",
					'B', BigReactors.CONFIG.recipeBlutoniumIngotName, 'R', Items.BLAZE_ROD, 'E', "blockEnderium");
		}
		*/
	}
}
