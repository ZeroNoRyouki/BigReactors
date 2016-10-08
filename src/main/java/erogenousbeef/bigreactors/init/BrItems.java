package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.config.Config;
import erogenousbeef.bigreactors.common.item.ItemBRMetal;
import erogenousbeef.bigreactors.common.item.ItemMineral;
import erogenousbeef.bigreactors.common.item.ItemTieredComponent;
import erogenousbeef.bigreactors.common.item.ItemWrench;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import it.zerono.mods.zerocore.lib.MetalSize;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class BrItems {

    // Ingots & dusts
    public static final ItemBRMetal ingotMetals;
    public static final ItemBRMetal dustMetals;
    public static final ItemMineral minerals;

    // Reactor components
    public static final ItemTieredComponent reactorCasingCores;

    // Turbine components
    public static final ItemTieredComponent turbineHousingCores;

    // Miscellanea
    public static final ItemWrench wrench;

    public static void initialize() {
    }

    static {

        final InitHandler init = InitHandler.INSTANCE;

        // register items

        // - Ingots & dusts
        ingotMetals = (ItemBRMetal)init.register(new ItemBRMetal("ingotMetals", MetalSize.Ingot) {

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
                     GameRegistry.addRecipe(new ShapelessOreRecipe(ingotCyanite, configs.recipeYelloriumIngotName, Blocks.SAND));


                 // TEMPORARY recipe for the blutonium ingot

                 GameRegistry.addRecipe(BrItems.ingotMetals.createItemStack(MetalType.Blutonium, 1), "CCC", "C C", "CCC",
                         'C', ingotCyanite);
             }
        });

        dustMetals = (ItemBRMetal)init.register(new ItemBRMetal("dustMetals", MetalSize.Dust) {

            @Override
            public void registerRecipes() {

                for (MetalType metal : MetalType.VALUES) {

                    // smelt dust into ingot
                    GameRegistry.addSmelting(BrItems.dustMetals.createItemStack(metal, 1),
                            BrItems.ingotMetals.createItemStack(metal, 1), 0.0f);
                }
            }
        });

        minerals = (ItemMineral)init.register(new ItemMineral("minerals"));

        // Reactor components
        reactorCasingCores = (ItemTieredComponent)init.register(new ItemTieredComponent("reactorCasingCores") {

            @Override
            public void registerRecipes() {

                if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
                    GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 1), "IGI", "ARA", "IGI",
                            'I', "ingotIron", 'G', "ingotGraphite",
                            'A', "ingotGold", 'R', Items.REDSTONE));

                if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
                    GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 1), "IGI", "ARA", "IGI",
                            'I', "ingotSteel", 'G', "ingotGraphite",
                            'A', "ingotGold", 'R', Items.REDSTONE));
            }
        });

        // Turbine components
        turbineHousingCores = (ItemTieredComponent)init.register(new ItemTieredComponent("turbineHousingCores") {

            @Override
            public void registerRecipes() {

                if (PartTier.TURBINE_TIERS.contains(PartTier.Legacy))
                    GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 1), "IGI", "ARA", "IGI",
                            'I', "ingotIron", 'G', "ingotGraphite",
                            'A', "ingotGold", 'R', Items.COMPARATOR));

                if (PartTier.TURBINE_TIERS.contains(PartTier.Basic))
                    GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 1), "IGI", "ARA", "IGI",
                            'I', "ingotSteel", 'G', "ingotGraphite",
                            'A', "ingotGold", 'R', Items.COMPARATOR));
            }
        });

        // Miscellanea
        wrench = (ItemWrench)init.register(new ItemWrench("wrench"));
    }
}
