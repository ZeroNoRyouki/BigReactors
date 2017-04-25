package erogenousbeef.bigreactors.common.compat;

import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.block.OreType;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ModAppliedEnergistics2 extends ModCompact {

    @Override
    public void onInit(FMLInitializationEvent fmlInitializationEvent) {

        this.addGrinderRecipe(BrBlocks.brOre.createItemStack(OreType.Yellorite, 1),
                BrItems.dustMetals.createItemStack(MetalType.Yellorium, 2), 4);

        for (MetalType metal: MetalType.VALUES)
            this.addGrinderRecipe(BrItems.ingotMetals.createItemStack(metal, 1),
                    BrItems.dustMetals.createItemStack(metal, 1), 2);
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