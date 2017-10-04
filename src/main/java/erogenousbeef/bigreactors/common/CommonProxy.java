package erogenousbeef.bigreactors.common;

import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.common.compat.CompatManager;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import it.zerono.mods.zerocore.lib.IModInitializationHandler;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommonProxy implements IModInitializationHandler {

	@Override
	public void onPreInit(FMLPreInitializationEvent event) {
		CompatManager.INSTANCE.onPreInit(event);
	}

	@Override
	public void onInit(FMLInitializationEvent event) {
		CompatManager.INSTANCE.onInit(event);
	}

	@Override
	public void onPostInit(FMLPostInitializationEvent event) {

		if (BigReactors.CONFIG.autoAddUranium)
			Reactants.registerSolid("ingotUranium", StandardReactants.yellorium);

		CompatManager.INSTANCE.onPostInit(event);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void setIcons(TextureStitchEvent.Post event) {
	}
}
