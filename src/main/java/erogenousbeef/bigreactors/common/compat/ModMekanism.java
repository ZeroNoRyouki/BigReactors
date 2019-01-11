package erogenousbeef.bigreactors.common.compat;

import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.block.OreType;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nonnull;

public class ModMekanism extends ModCompact {

    @Override
    public void onInit(FMLInitializationEvent fmlInitializationEvent) {

        final ItemStack yelloriteOre = BrBlocks.oreYellorite.createItemStack();

        // yellorite ore -> 2x yellorium dust
        this.addEnrichmentChamberRecipe(yelloriteOre, BrItems.dustMetals.createItemStack(MetalType.Yellorium, 2));

        // yellorium ingot -> yellorium dust
        this.addCrusherRecipe(BrItems.ingotMetals.createItemStack(MetalType.Yellorium, 1), BrItems.dustMetals.createItemStack(MetalType.Yellorium, 1));

        // cyanite ingot -> cyanite dust
        this.addCrusherRecipe(BrItems.ingotMetals.createItemStack(MetalType.Cyanite, 1), BrItems.dustMetals.createItemStack(MetalType.Cyanite, 1));

        // graphite ingot -> graphite dust
        this.addCrusherRecipe(BrItems.ingotMetals.createItemStack(MetalType.Graphite, 1), BrItems.dustMetals.createItemStack(MetalType.Graphite, 1));

        // blutonium ingot -> blutonium dust
        this.addCrusherRecipe(BrItems.ingotMetals.createItemStack(MetalType.Blutonium, 1), BrItems.dustMetals.createItemStack(MetalType.Blutonium, 1));
    }

    private void addEnrichmentChamberRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output) {
        this.addRecipe("EnrichmentChamberRecipe", input, output);
    }

    private void addCrusherRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output) {
        this.addRecipe("CrusherRecipe", input, output);
    }

    private void addRecipe(@Nonnull String type, @Nonnull ItemStack input, @Nonnull ItemStack output) {

        NBTTagCompound recipe = new NBTTagCompound();

        recipe.setTag("input", input.writeToNBT(new NBTTagCompound()));
        recipe.setTag("output", output.writeToNBT(new NBTTagCompound()));

        FMLInterModComms.sendMessage(IdReference.MODID_MEKANISM, type, recipe);
    }
}
