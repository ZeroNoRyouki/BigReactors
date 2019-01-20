package erogenousbeef.bigreactors.common.compat;

import erogenousbeef.bigreactors.init.BrBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ModMineFactoryReloaded extends ModCompact {

    @Override
    public void onInit(FMLInitializationEvent fmlInitializationEvent) {


        final ItemStack yelloriteOre = BrBlocks.oreYellorite.createItemStack();

        // Add yellorite to yellow focus list.
        addOreToMiningLaserFocus(yelloriteOre, 2);

        /*
        // Make Yellorite the 'preferred' ore for lime focus
        setMiningLaserFocusPreferredOre(yelloriteOre, 9);
        */
    }

    private void addOreToMiningLaserFocus(ItemStack stack, int color) {

        NBTTagCompound laserOreMsg = new NBTTagCompound();

        stack.writeToNBT(laserOreMsg);
        laserOreMsg.setInteger("value", color);
        sendInterModMessage(IdReference.MODID_MINEFACTORYRELOADED, "registerLaserOre", laserOreMsg);
    }
    /*
    private static void setMiningLaserFocusPreferredOre(ItemStack stack, int color) {
    }*/
}
