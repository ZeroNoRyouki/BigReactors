package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.item.ItemBRMetal;
import erogenousbeef.bigreactors.common.item.ItemBeefDebugTool;
import erogenousbeef.bigreactors.common.item.ItemTieredComponent;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import it.zerono.mods.zerocore.lib.MetalSize;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public final class BrItems {

    // Ingots & dusts
    public static final ItemBRMetal ingotMetals;
    public static final ItemBRMetal dustMetals;

    // Reactor components
    public static final ItemTieredComponent reactorCasingCores;

    // Miscellanea
    public static final ItemBeefDebugTool beefDebugTool;

    public static void initialize() {
    }

    static {

        final InitHandler init = InitHandler.INSTANCE;

        // register items

        // - Ingots & dusts
        ingotMetals = (ItemBRMetal)init.register(new ItemBRMetal("ingotMetals", MetalSize.Ingot));

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

        // Reactor components
        reactorCasingCores = (ItemTieredComponent)init.register(new ItemTieredComponent("reactorCasingCores") {

            @Override
            public void registerRecipes() {

                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 1), "IGI", "ARA", "IGI",
                        'I', "ingotIron", 'G', "ingotGraphite",
                        'A', "ingotGold", 'R', Items.REDSTONE));

                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 1), "IGI", "ARA", "IGI",
                        'I', "ingotSteel", 'G', "ingotGraphite",
                        'A', "ingotGold", 'R', Items.REDSTONE));
            }
        });

        // - Miscellanea
        beefDebugTool = (ItemBeefDebugTool)init.register(new ItemBeefDebugTool("beefDebugTool"));
    }
}
