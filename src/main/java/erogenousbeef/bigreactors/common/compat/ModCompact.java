package erogenousbeef.bigreactors.common.compat;

import it.zerono.mods.zerocore.lib.IModInitializationHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

abstract class ModCompact implements IModInitializationHandler {

    @Override
    public void onPreInit(FMLPreInitializationEvent fmlPreInitializationEvent) {
    }

    @Override
    public void onInit(FMLInitializationEvent fmlInitializationEvent) {
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent fmlPostInitializationEvent) {
    }

    protected static void sendInterModMessage(String to, String type, NBTTagCompound message) {
        FMLInterModComms.sendMessage(to, type, message);
    }
}
