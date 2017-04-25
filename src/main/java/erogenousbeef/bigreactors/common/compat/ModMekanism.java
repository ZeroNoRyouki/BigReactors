package erogenousbeef.bigreactors.common.compat;

import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.item.ItemBRMetal;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import it.zerono.mods.zerocore.util.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.lang.reflect.Method;

public class ModMekanism extends ModCompact {

    @Override
    public void onPostInit(FMLPostInitializationEvent fmlPostInitializationEvent) {

        ItemBRMetal ingotGeneric = BrItems.ingotMetals;

        ItemStack yelloriteOre 	= new ItemStack(BrBlocks.brOre, 1);
        ItemStack ingotYellorium= BrItems.ingotMetals.createItemStack(MetalType.Yellorium, 1);
        ItemStack ingotCyanite 	= BrItems.ingotMetals.createItemStack(MetalType.Cyanite, 1);
        ItemStack ingotGraphite = BrItems.ingotMetals.createItemStack(MetalType.Graphite, 1);
        ItemStack ingotBlutonium= BrItems.ingotMetals.createItemStack(MetalType.Blutonium, 1);
        ItemStack dustYellorium = BrItems.dustMetals.createItemStack(MetalType.Yellorium, 1);
        ItemStack dustCyanite 	= BrItems.dustMetals.createItemStack(MetalType.Cyanite, 1);
        ItemStack dustGraphite 	= BrItems.dustMetals.createItemStack(MetalType.Graphite, 1);
        ItemStack dustBlutonium = BrItems.dustMetals.createItemStack(MetalType.Blutonium, 1);

        // Some mods make me do this myself. :V
        ItemStack doubledYelloriumDust = ItemHelper.stackEmpty();

        if (ItemHelper.stackIsValid(dustYellorium))
            doubledYelloriumDust = ItemHelper.stackFrom(dustYellorium, 2);

        if (ItemHelper.stackIsValid(yelloriteOre) && ItemHelper.stackIsValid(doubledYelloriumDust)) {

            addMekanismEnrichmentChamberRecipe(yelloriteOre.copy(), ItemHelper.stackFrom(doubledYelloriumDust));
            addMekanismCombinerRecipe(ItemHelper.stackFrom(dustYellorium, 8), yelloriteOre.copy());
        }

        if (ItemHelper.stackIsValid(ingotYellorium) && ItemHelper.stackIsValid(dustYellorium))
            addMekanismCrusherRecipe(ItemHelper.stackFrom(ingotYellorium), ItemHelper.stackFrom(dustYellorium));

        if (ItemHelper.stackIsValid(ingotCyanite) && ItemHelper.stackIsValid(dustCyanite))
            addMekanismCrusherRecipe(ItemHelper.stackFrom(ingotCyanite), ItemHelper.stackFrom(dustCyanite));

        if (ItemHelper.stackIsValid(ingotGraphite) && ItemHelper.stackIsValid(dustGraphite))
            addMekanismCrusherRecipe(ItemHelper.stackFrom(ingotGraphite), ItemHelper.stackFrom(dustGraphite));

        if (ItemHelper.stackIsValid(ingotBlutonium) && ItemHelper.stackIsValid(dustBlutonium))
            addMekanismCrusherRecipe(ItemHelper.stackFrom(ingotBlutonium), ItemHelper.stackFrom(dustBlutonium));
    }


    // TODO remove once Mekanism maven is available
    // https://github.com/aidancbrady/Mekanism/issues/4309

    /// Mekanism Compat - taken from Mekanism's API. Extracted to allow compat with last known green build.
    /**
     * Add an Enrichment Chamber recipe. (Ore -> 2 Dust)
     * @param input - input ItemStack
     * @param output - output ItemStack
     */
    public static void addMekanismEnrichmentChamberRecipe(ItemStack input, ItemStack output)
    {
        try {
            Class recipeClass = Class.forName("mekanism.api.RecipeHelper");
            Method m = recipeClass.getMethod("addEnrichmentChamberRecipe", ItemStack.class, ItemStack.class);
            m.invoke(null, input, output);
        } catch(Exception e) {
            System.err.println("[Mekanism] Error while adding recipe: " + e.getMessage());
        }
    }

    /**
     * Add a Combiner recipe. (8 Dust + Cobble -> Ore)
     * @param input - input ItemStack
     * @param output - output ItemStack
     */
    public static void addMekanismCombinerRecipe(ItemStack input, ItemStack output)
    {
        try {
            Class recipeClass = Class.forName("mekanism.api.RecipeHelper");
            Method m = recipeClass.getMethod("addCombinerRecipe", ItemStack.class, ItemStack.class);
            m.invoke(null, input, output);
        } catch(Exception e) {
            System.err.println("[Mekanism] Error while adding recipe: " + e.getMessage());
        }
    }

    /**
     * Add a Crusher recipe. (Ingot -> Dust)
     * @param input - input ItemStack
     * @param output - output ItemStack
     */
    public static void addMekanismCrusherRecipe(ItemStack input, ItemStack output)
    {
        try {
            Class recipeClass = Class.forName("mekanism.api.RecipeHelper");
            Method m = recipeClass.getMethod("addCrusherRecipe", ItemStack.class, ItemStack.class);
            m.invoke(null, input, output);
        } catch(Exception e) {
            System.err.println("[Mekanism] Error while adding recipe: " + e.getMessage());
        }
    }
}
