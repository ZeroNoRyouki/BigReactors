package erogenousbeef.bigreactors.common.compat;

import cofh.api.util.ThermalExpansionHelper;
import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.block.OreType;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import it.zerono.mods.zerocore.util.ItemHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ModThermalExpansion extends ModCompact {

    @Override
    public void onInit(FMLInitializationEvent fmlInitializationEvent) {

        final ItemStack sandStack = new ItemStack(Blocks.SAND, 1);
        final ItemStack yelloriteOre = BrBlocks.brOre.createItemStack(OreType.Yellorite, 1);
        final ItemStack doubleYelloriumIngots = BrItems.ingotMetals.createItemStack(MetalType.Yellorium, 2);
        final ItemStack doubledYelloriumDust = BrItems.dustMetals.createItemStack(MetalType.Yellorium, 2);

        final MetalType[] metals = MetalType.values();
        final int length = metals.length;
        final ItemStack[] ingots = new ItemStack[length];
        final ItemStack[] dusts = new ItemStack[length];

        for (int i = 0; i < length; ++i) {

            ingots[i] = BrItems.ingotMetals.createItemStack(metals[i], 1);
            dusts[i] = BrItems.dustMetals.createItemStack(metals[i], 1);
        }

        ThermalExpansionHelper.addFurnaceRecipe(400, yelloriteOre, ingots[MetalType.Yellorium.ordinal()]);
        ThermalExpansionHelper.addSmelterRecipe(1600, yelloriteOre, sandStack, doubleYelloriumIngots);

        ThermalExpansionHelper.addPulverizerRecipe(4000, yelloriteOre, doubledYelloriumDust);
        ThermalExpansionHelper.addSmelterRecipe(200, doubledYelloriumDust, sandStack, doubleYelloriumIngots);

        for (int i = 0; i < length; ++i) {

            ThermalExpansionHelper.addPulverizerRecipe(2400, ingots[i], dusts[i]);
            ThermalExpansionHelper.addSmelterRecipe(200, doubledYelloriumDust, sandStack, doubleYelloriumIngots);

            ItemStack doubleDust = ItemHelper.stackFrom(dusts[i]);
            ItemStack doubleIngot = ItemHelper.stackFrom(ingots[i]);

            ItemHelper.stackSetSize(doubleDust, 2);
            ItemHelper.stackSetSize(doubleIngot, 2);

            ThermalExpansionHelper.addSmelterRecipe(200, doubleDust, sandStack, doubleIngot);
        }
    }
}
