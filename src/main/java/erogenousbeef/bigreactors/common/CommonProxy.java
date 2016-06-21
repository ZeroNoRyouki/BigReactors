package erogenousbeef.bigreactors.common;

import java.util.Calendar;

import erogenousbeef.bigreactors.common.block.BlockBR;
import erogenousbeef.bigreactors.common.item.ItemBRMetal;
import erogenousbeef.bigreactors.common.item.ItemBase;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.TextureStitchEvent;
//import cofh.api.modhelpers.ThermalExpansionHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import erogenousbeef.bigreactors.utils.intermod.IMCHelper;
import erogenousbeef.bigreactors.utils.intermod.ModHelperBase;
//import erogenousbeef.bigreactors.utils.intermod.ModHelperComputerCraft;
import erogenousbeef.bigreactors.utils.intermod.ModHelperMekanism;
import zero.mods.zerocore.api.multiblock.MultiblockServerTickHandler;
import zero.mods.zerocore.lib.gui.ModGuiHandler;

public class CommonProxy {

	public BlockBR register(BlockBR block) {

		GameRegistry.register(block);
		block.onPostRegister();
		return block;
	}

	public ItemBase register(ItemBase item) {

		GameRegistry.register(item);
		item.onPostRegister();
		return item;
	}

	public void register(Class<? extends TileEntity> tileEntityClass) {

		GameRegistry.registerTileEntity(tileEntityClass, BigReactors.MODID + tileEntityClass.getSimpleName());
	}

	public void preInit() {
	}

	public void init() {
		//BigReactors.registerTileEntities();
		
		CommonPacketHandler.init();

		//NetworkRegistry.INSTANCE.registerGuiHandler(BRLoader.instance, new BigReactorsGUIHandler());
		new ModGuiHandler(BRLoader.instance);


		BigReactors.tickHandler = new BigReactorsTickHandler();
		FMLCommonHandler.instance().bus().register(BigReactors.tickHandler);
        FMLCommonHandler.instance().bus().register(new MultiblockServerTickHandler());
		
		sendInterModAPIMessages();

		if(Loader.isModLoaded("VersionChecker")) {
			FMLInterModComms.sendRuntimeMessage(BRLoader.MOD_ID, "VersionChecker", "addVersionCheck", "http://big-reactors.com/version.json");
		}
	}

	private void sendInterModAPIMessages() {

		ItemStack yelloriteOre = new ItemStack(BrBlocks.brOre, 1);

		MetalType[] metals = MetalType.values();
		int length = metals.length;
		ItemStack[] ingots = new ItemStack[length];
		ItemStack[] dusts = new ItemStack[length];
		
		for(int i = 0; i < length; ++i) {

			ingots[i] = BrItems.ingotMetals.createItemStack(metals[i], 1);
			dusts[i] = BrItems.dustMetals.createItemStack(metals[i], 1);
		}

		ItemStack doubledYelloriumDust = BrItems.dustMetals.createItemStack(MetalType.Yellorium, 2);

		// TODO disabled as there is no ThermalExpansion for 1.9.x
		/*
		if(Loader.isModLoaded("ThermalExpansion")) {

			ItemStack sandStack = new ItemStack(Blocks.sand, 1);
			ItemStack doubleYelloriumIngots = BrItems.ingotMetals.createItemStack(MetalType.Yellorium, 2);

			// TODO: Remove ThermalExpansionHelper once addSmelterRecipe and addPulverizerRecipe aren't broken
			if(ingots[YELLORIUM] != null) {

				ThermalExpansionHelper.addFurnaceRecipe(400, yelloriteOre, ingots[yelloriumIndex]);
				ThermalExpansionHelper.addSmelterRecipe(1600, yelloriteOre, sandStack, doubleYelloriumIngots);
			}

			if(doubledYelloriumDust != null) {

				ThermalExpansionHelper.addPulverizerRecipe(4000, yelloriteOre, doubledYelloriumDust);
				ThermalExpansionHelper.addSmelterRecipe(200, doubledYelloriumDust, sandStack, doubleYelloriumIngots);
			}

			for(int i = 0; i < ingots.length; i++) {
				if(ingots[i] == null || dusts[i] == null) { continue; }

				ThermalExpansionHelper.addPulverizerRecipe(2400, ingots[i], dusts[i]);
				ThermalExpansionHelper.addSmelterRecipe(200, doubledYelloriumDust, sandStack, doubleYellorium);

				ItemStack doubleDust = dusts[i].copy();
				doubleDust.stackSize = 2;
				ItemStack doubleIngot = ingots[i].copy();
				doubleIngot.stackSize = 2;

				ThermalExpansionHelper.addSmelterRecipe(200, doubleDust, sandStack, doubleIngot);
			}
		} // END: IsModLoaded - ThermalExpansion
		*/
		
		if(Loader.isModLoaded("MineFactoryReloaded")) {
			// Add yellorite to yellow focus list.
			IMCHelper.MFR.addOreToMiningLaserFocus(yelloriteOre, 2);
            
            // Make Yellorite the 'preferred' ore for lime focus
            IMCHelper.MFR.setMiningLaserFocusPreferredOre(yelloriteOre, 9);
		} // END: IsModLoaded - MineFactoryReloaded
		
		if(Loader.isModLoaded("appliedenergistics2")) {
			if(doubledYelloriumDust != null) {
				IMCHelper.AE2.addGrinderRecipe(yelloriteOre, doubledYelloriumDust, 4);
			}
		
			for(int i = 0; i < ingots.length; i++) {
				if(ingots[i] == null || dusts[i] == null) { continue; }
				IMCHelper.AE2.addGrinderRecipe(ingots[i], dusts[i], 2);
			}
		} // END: IsModLoaded - AE2
	}

	public void postInit() {
		BRConfig.CONFIGURATION.load();
		boolean autoAddUranium = BRConfig.CONFIGURATION.get("Compatibility", "autoAddUranium",
															true,
															"If true, automatically adds all "
															+"unregistered ingots found as clones"
															+"of standard yellorium fuel").getBoolean(true);
		if(autoAddUranium) {
			Reactants.registerSolid("ingotUranium", StandardReactants.yellorium);
		}

		BRConfig.CONFIGURATION.save();
		
		registerWithOtherMods();
		
		// Easter Egg - Check if today is valentine's day. If so, change all particles to hearts.
		Calendar calendar = Calendar.getInstance();
		BigReactors.isValentinesDay = (calendar.get(Calendar.MONTH) == 1 && calendar.get(Calendar.DAY_OF_MONTH) == 14);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void setIcons(TextureStitchEvent.Post event) {
	}
	
	/// Mod Interoperability ///
	void registerWithOtherMods() {
		ModHelperBase modHelper;
		
		ModHelperBase.detectMods();

		// TODO Removing support for ComputerCraft and MineFactory Reloaded until they are updated to 1.9.x
		/*
		modHelper = new ModHelperComputerCraft();
		modHelper.register();
		*/
		
		modHelper = new ModHelperMekanism();
		modHelper.register();
	}
}
