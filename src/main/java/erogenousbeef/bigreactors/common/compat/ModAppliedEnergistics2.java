package erogenousbeef.bigreactors.common.compat;

import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ModAppliedEnergistics2 extends ModCompact {

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onInit(FMLInitializationEvent fmlInitializationEvent) {

        this.addGrinderRecipe(BrBlocks.oreYellorite.createItemStack(), BrItems.dustYellorium.createItemStack(2), 4);

        this.addGrinderRecipe(BrItems.ingotYellorium.createItemStack(), BrItems.dustYellorium.createItemStack(), 2);
        this.addGrinderRecipe(BrItems.ingotCyanite.createItemStack(), BrItems.dustCyanite.createItemStack(), 2);
        this.addGrinderRecipe(BrItems.ingotGraphite.createItemStack(), BrItems.dustGraphite.createItemStack(), 2);
        this.addGrinderRecipe(BrItems.ingotBlutonium.createItemStack(), BrItems.dustBlutonium.createItemStack(), 2);
        this.addGrinderRecipe(BrItems.ingotLudicrite.createItemStack(), BrItems.dustLudicrite.createItemStack(), 2);
        this.addGrinderRecipe(BrItems.ingotSteel.createItemStack(), BrItems.dustSteel.createItemStack(), 2);
    }

    private void addGrinderRecipe(ItemStack input, ItemStack output, int turns) {

        NBTTagCompound msg = new NBTTagCompound();
        NBTTagCompound in = new NBTTagCompound();
        NBTTagCompound out = new NBTTagCompound();

        input.writeToNBT(in);
        output.writeToNBT(out);

        msg.setTag("in", in);
        msg.setTag("out", out);
        msg.setInteger("turns", turns);

        sendInterModMessage(IdReference.MODID_APPLIEDENERGISTICS2, "add-grindable", msg);
    }
}