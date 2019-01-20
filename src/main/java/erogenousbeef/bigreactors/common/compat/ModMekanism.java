package erogenousbeef.bigreactors.common.compat;

import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nonnull;

public class ModMekanism extends ModCompact {

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onInit(FMLInitializationEvent fmlInitializationEvent) {

        final ItemStack yelloriteOre = BrBlocks.oreYellorite.createItemStack();

        // yellorite ore -> 2x yellorium dust
        this.addEnrichmentChamberRecipe(yelloriteOre, BrItems.dustYellorium.createItemStack(2));

        // yellorium ingot -> yellorium dust
        this.addCrusherRecipe(BrItems.ingotYellorium.createItemStack(), BrItems.dustYellorium.createItemStack());

        // cyanite ingot -> cyanite dust
        this.addCrusherRecipe(BrItems.ingotCyanite.createItemStack(), BrItems.dustCyanite.createItemStack());

        // graphite ingot -> graphite dust
        this.addCrusherRecipe(BrItems.ingotGraphite.createItemStack(), BrItems.dustGraphite.createItemStack());

        // blutonium ingot -> blutonium dust
        this.addCrusherRecipe(BrItems.ingotBlutonium.createItemStack(), BrItems.dustBlutonium.createItemStack());
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
