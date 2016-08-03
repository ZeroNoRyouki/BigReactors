package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.config.Config;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.List;

public final class BrRecipes {

    public static void initialize() {

        final Config configs = BigReactors.CONFIG;
        final String ironOrSteelIngot = configs.requireSteelInsteadOfIron ? "ingotSteel" : "ingotIron";
        final String yelloriumIngot = configs.registerYelloriumAsUranium ? "ingotUranium" : "ingotYellorium";
        final String blutoniumIngot = configs.registerYelloriumAsUranium ? "ingotPlutonium" : "ingotBlutonium";
        final ItemStack ingotGraphite = OreDictionary.getOres("ingotGraphite").get(0).copy();
        final ItemStack ingotCyanite = OreDictionary.getOres("ingotCyanite").get(0).copy();
        final boolean computerSupport = Loader.isModLoaded("ComputerCraft") || Loader.isModLoaded("OpenComputers");
        final MetalType[] metals = MetalType.values();

        // - Patch up vanilla being stupid - most mods already do this, so it's usually a no-op

        if (!OreDictionaryHelper.doesOreNameExist("ingotIron"))
            OreDictionary.registerOre("ingotIron", new ItemStack(Items.IRON_INGOT, 1));

        if (!OreDictionaryHelper.doesOreNameExist("ingotGold"))
            OreDictionary.registerOre("ingotGold", new ItemStack(Items.GOLD_INGOT, 1));

        if (!OreDictionaryHelper.doesOreNameExist("blockSnow"))
            OreDictionary.registerOre("blockSnow", new ItemStack(Blocks.SNOW, 1));

        if (!OreDictionaryHelper.doesOreNameExist("blockIce"))
            OreDictionary.registerOre("blockIce", new ItemStack(Blocks.ICE, 1));

        if (!OreDictionaryHelper.doesOreNameExist("blockGlassColorless"))
            OreDictionary.registerOre("blockGlassColorless", new ItemStack(Blocks.GLASS, 1));

        // - Yellorium

        ItemStack product;

        if (configs.registerYelloriteSmeltToUranium) {

            List<ItemStack> candidateOres = OreDictionary.getOres("ingotUranium");

            if (candidateOres == null || candidateOres.size() <= 0) {

                BRLog.warning("Config value registerYelloriteSmeltToUranium is set to True, but there are no ores registered as ingotUranium in the ore dictionary! Falling back to using standard yellorium only.");
                candidateOres = OreDictionary.getOres("ingotYellorium");
            }
            product = candidateOres.get(0).copy();

        } else {
            product = OreDictionary.getOres("ingotYellorium").get(0).copy();
        }

        GameRegistry.addSmelting(BrBlocks.brOre, product, 0.5f);

        // - Metal blocks, ingots and dusts

        ItemStack block, ingot, dust;

        for (MetalType metal : metals) {

            block = BrBlocks.blockMetals.createItemStack(metal, 1);
            ingot = BrItems.ingotMetals.createItemStack(metal, 1);
            dust  = BrItems.dustMetals.createItemStack(metal, 1);

            GameRegistry.addShapelessRecipe(block, ingot, ingot, ingot, ingot, ingot, ingot, ingot, ingot, ingot);
            ingot.stackSize = 9;
            GameRegistry.addShapelessRecipe(ingot, block);

            GameRegistry.addSmelting(dust, ingot, 0.0f);
        }

        // - Ludicrite block. Because.

        ItemStack ludicriteBlock = BrBlocks.blockMetals.createItemStack(MetalType.Ludicrite, 1);

        GameRegistry.addRecipe(new ShapedOreRecipe(ludicriteBlock, "BPB", "ENE", "BPB", 'N',
                Items.NETHER_STAR, 'P', Items.ENDER_PEARL, 'E', Blocks.EMERALD_BLOCK, 'B', blutoniumIngot));

        if (OreDictionaryHelper.doesOreNameExist("blockEnderium")) {

            // Ok, how about some ludicrous shit here. Enderium and blaze rods. Have fun, bucko.
            GameRegistry.addRecipe(new ShapedOreRecipe(ludicriteBlock, "BRB", "E E", "BRB", 'B',
                    blutoniumIngot, 'R', Items.BLAZE_ROD, 'E', "blockEnderium"));
        }

        // - Graphite & Cyanite

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
            GameRegistry.addRecipe(new ShapedOreRecipe( ingotGraphite, "GCG", 'G', Blocks.GRAVEL, 'C',
                    new ItemStack(Items.COAL, 1, 1)));

        // -- Yellorium ingot + Sand -> Cyanite
        if (configs.enableCyaniteFromYelloriumRecipe)
            GameRegistry.addRecipe(new ShapelessOreRecipe(ingotCyanite, yelloriumIngot, Blocks.SAND ));

        // - Reactor parts

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorCasing.createItemStack(4), "IGI", "GUG", "IGI",
                'I', ironOrSteelIngot, 'G', "ingotGraphite", 'U', yelloriumIngot));


        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorFuelRod.createItemStack(), "ICI", "IUI", "ICI",
                'I', ironOrSteelIngot, 'C', "ingotGraphite", 'U', yelloriumIngot));


        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorController.createItemStack(), "C C", "GDG", "CRC",
                'D', Items.DIAMOND, 'G', yelloriumIngot, 'C', "reactorCasing", 'R', Items.REDSTONE));

        if (configs.enableReactorPowerTapRecipe) {

            GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorPowerTapRF.createItemStack(), "CRC", "R R", "CRC",
                    'C', "reactorCasing", 'R', Items.REDSTONE));

            // TODO add tesla power tap recipe
        }

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorAccessPort.createItemStack(), "C C", " V ", "CPC",
                'C', "reactorCasing", 'V', Blocks.CHEST, 'P', Blocks.PISTON));

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorCoolantPort.createItemStack(), "C C", "IVI", "CPC",
                'C', "reactorCasing", 'V', Items.BUCKET, 'P', Blocks.PISTON, 'I', ironOrSteelIngot));

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorControlRod.createItemStack(), "CGC", "GRG", "CUC",
                'G', "ingotGraphite", 'C', "reactorCasing", 'R', Items.REDSTONE, 'U', yelloriumIngot));

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorRedstonePort.createItemStack(), "CRC", "RGR", "CRC",
                'C', "reactorCasing", 'R', Items.REDSTONE, 'G', Items.GOLD_INGOT));

        /* TODO check
        if (Loader.isModLoaded("MineFactoryReloaded")) {
            GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorRedNetPort.createItemStack(), "CRC", "RGR", "CRC", 'C',
                    "reactorCasing", 'R', "cableRedNet", 'G', "ingotGold"));
        }
        */

        if (computerSupport)
            GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorComputerPort, "CRC", "GPG", "CRC",
                    'C', "reactorCasing", 'R', Items.REDSTONE, 'G', "ingotGold", 'P', Items.REPEATER));


        // - Reactor and turbine glass

        ItemStack reactorGlassStack = BrBlocks.reactorGlass.createItemStack();
        ItemStack turbineGlassStack = BrBlocks.turbineGlass.createItemStack();

        if (configs.requireObsidianGlass &&
                (OreDictionaryHelper.doesOreNameExist("glassReinforced") || OreDictionaryHelper.doesOreNameExist("blockGlassHardened"))) {

            GameRegistry.addRecipe(new ShapedOreRecipe(reactorGlassStack, "GCG", 'G', "glassReinforced", 'C', "reactorCasing"));
            GameRegistry.addRecipe(new ShapedOreRecipe(reactorGlassStack, "GCG", 'G', "blockGlassHardened", 'C', "reactorCasing"));

            GameRegistry.addRecipe(new ShapedOreRecipe(turbineGlassStack, "GCG", 'G', "glassReinforced", 'C', "turbineHousing"));
            GameRegistry.addRecipe(new ShapedOreRecipe(turbineGlassStack, "GCG", 'G', "blockGlassHardened", 'C', "turbineHousing"));

        } else {

            GameRegistry.addRecipe(new ShapedOreRecipe(reactorGlassStack, "GCG", 'G', "blockGlassColorless", 'C', "reactorCasing"));
            GameRegistry.addRecipe(new ShapedOreRecipe(turbineGlassStack, "GCG", 'G', "blockGlassColorless", 'C', "turbineHousing"));
        }

        // - Turbine parts

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbineHousing.createItemStack(4), "IGI", "QCQ", "IGI",
                'C', "ingotCyanite", 'I', ironOrSteelIngot, 'Q', Items.QUARTZ, 'G', "ingotGraphite"));

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbineController.createItemStack(), "H H", "BDB", "H H",
                'H', "turbineHousing", 'D', Items.DIAMOND, 'B', blutoniumIngot));

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbinePowerTap.createItemStack(), "HRH", "R R", "HRH",
                'H', "turbineHousing", 'R', Items.REDSTONE));

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbineFluidPort.createItemStack(), "H H", "IVI", "HPH",
                'H', "turbineHousing", 'I', ironOrSteelIngot, 'V', Items.BUCKET, 'P', Blocks.PISTON));

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbineBearing.createItemStack(), "HRH", "DDD", "HRH",
                'H', "turbineHousing", 'D', Items.DIAMOND, 'R', "turbineRotorShaft"));

        if (computerSupport)
            GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbineComputerPort.createItemStack(), "HRH", "GPG", "HRH",
                    'H', "turbineHousing", 'G', "ingotGold", 'R', "turbineRotorShaft"));

        /* TODO add back when turbine rotor is in
        if (blockTurbineRotorPart != null) {
            ItemStack rotorShaft = blockTurbineRotorPart.getItemStack("rotor");
            ItemStack rotorBlade = blockTurbineRotorPart.getItemStack("blade");

            GameRegistry.addRecipe(new ShapedOreRecipe(rotorShaft, "ICI", 'C', "ingotCyanite", 'I', ironOrSteelIngot));
            GameRegistry.addRecipe(new ShapedOreRecipe(rotorBlade, "CII", 'C', "ingotCyanite", 'I', ironOrSteelIngot));
        }
        */

        // - Generic devices

        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.deviceCyaniteRep.createItemStack(), "CIC", "PFP", "CRC",
                'C', "reactorCasing", 'I', ironOrSteelIngot, 'F', BrBlocks.reactorFuelRod, 'P', Blocks.PISTON, 'R', Items.REDSTONE));
    }
}
