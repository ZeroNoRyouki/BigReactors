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

        if (true)
            return;

        final Config configs = BigReactors.CONFIG;
        final String ironOrSteelIngot = configs.requireSteelInsteadOfIron ? "ingotSteel" : "ingotIron";
        final String yelloriumIngot = configs.registerYelloriumAsUranium ? "ingotUranium" : "ingotYellorium";
        final String blutoniumIngot = configs.registerYelloriumAsUranium ? "ingotPlutonium" : "ingotBlutonium";
        final ItemStack ingotGraphite = null; // TODO fix //OreDictionary.getOres("ingotGraphite").get(0).copy();
        final ItemStack ingotCyanite = null; // TODO fix // = OreDictionary.getOres("ingotCyanite").get(0).copy();
        final boolean computerSupport = Loader.isModLoaded("ComputerCraft") || Loader.isModLoaded("OpenComputers");
        final MetalType[] metals = MetalType.values();


        // - Reactor parts

        /* TODO check
        if (Loader.isModLoaded("MineFactoryReloaded")) {
            GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorRedNetPort.createItemStack(), "CRC", "RGR", "CRC", 'C',
                    "reactorCasing", 'R', "cableRedNet", 'G', "ingotGold"));
        }
        */


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
