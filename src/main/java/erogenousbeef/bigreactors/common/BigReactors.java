package erogenousbeef.bigreactors.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import erogenousbeef.bigreactors.common.block.*;
import erogenousbeef.bigreactors.common.multiblock.block.*;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
//import cofh.core.util.oredict.OreDictionaryArbiter;
//import cofh.lib.util.helpers.ItemHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import erogenousbeef.bigreactors.api.IHeatEntity;
import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.api.registry.ReactorConversions;
import erogenousbeef.bigreactors.api.registry.ReactorInterior;
import erogenousbeef.bigreactors.api.registry.TurbineCoil;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import erogenousbeef.bigreactors.common.item.ItemBeefDebugTool;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
//import erogenousbeef.bigreactors.common.multiblock.block.BlockReactorRedstonePort;
import erogenousbeef.bigreactors.common.multiblock.helpers.RadiationHelper;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import zero.mods.zerocore.util.OreDictionaryHelper;
//import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorComputerPort;
//import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorRedNetPort;
//import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorRedstonePort;
//import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineComputerPort;

// TODO Commented temporarily to allow this thing to compile...
//import erogenousbeef.bigreactors.world.BRSimpleOreGenerator;
//import erogenousbeef.bigreactors.world.BRWorldGenerator;

public class BigReactors {

	public static final String NAME = "Big Reactors";
	public static final String MODID = "bigreactors";
	public static final String CHANNEL = MODID.toLowerCase();
	public static final String RESOURCE_PATH = "/assets/bigreactors/";

	public static final CreativeTabs TAB = new CreativeTabBR(MODID);

	public static final String TEXTURE_NAME_PREFIX = "bigreactors:";

	public static final String TEXTURE_DIRECTORY = RESOURCE_PATH + "textures/";
	//public static final String GUI_DIRECTORY = TEXTURE_NAME_PREFIX + "textures/gui/";
	public static final String BLOCK_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
	public static final String ITEM_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "items/";
	public static final String MODEL_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "models/";

	public static final String LANGUAGE_PATH = RESOURCE_PATH + "languages/";
	private static final String[] LANGUAGES_SUPPORTED = new String[]{"de_DE", "en_US", "es_SP", "nl_NL", "pl_PL", "pt_BR", "ru_RU", "sv_SE", "zh_CN"};

	// TODO move all to BrBlocks
	public static BlockTurbineRotorPart blockTurbineRotorPart;
	public static Block blockRadiothermalGen;
	public static Block fluidYelloriumStill;
	public static Block fluidCyaniteStill;
	public static Block fluidFuelColumnStill;

	// TODO move all to BrItems
	// Buckets for bucketing reactor fluids
	public static Item fluidYelloriumBucketItem;
	public static Item fluidCyaniteBucketItem;

	public static Fluid fluidYellorium;
	public static Fluid fluidCyanite;
	public static Fluid fluidFuelColumn;

	public static Fluid fluidSteam;
	public static boolean registeredOwnSteam;

	public static final int defaultFluidColorFuel = 0xbcba50;
	public static final int defaultFluidColorWaste = 0x4d92b5;


	// TODO Commented temporarily to allow this thing to compile...
	//public static BRSimpleOreGenerator yelloriteOreGeneration;

	public static boolean INITIALIZED = false;
	public static boolean enableWorldGen = true;
	public static boolean enableWorldGenInNegativeDimensions = false;
	public static boolean enableWorldRegeneration = true;
	public static int userWorldGenVersion = 0;

	public static BREventHandler eventHandler = null;
	public static BigReactorsTickHandler tickHandler = null;
	// TODO Commented temporarily to allow this thing to compile...
	//public static BRWorldGenerator worldGenerator = null;
	public static HashSet<Integer> dimensionWhitelist = new HashSet<Integer>();

	public static int maximumReactorSize = MultiblockReactor.DIMENSION_UNBOUNDED;
	public static int maximumReactorHeight = MultiblockReactor.DIMENSION_UNBOUNDED;
	public static int ticksPerRedstoneUpdate = 20; // Once per second, roughly

	public static int maximumTurbineSize = 16;
	public static int maximumTurbineHeight = 32;

	public static float powerProductionMultiplier = 1.0f;
	public static float fuelUsageMultiplier = 1.0f;

	public static float reactorPowerProductionMultiplier = 1.0f;
	public static float turbinePowerProductionMultiplier = 1.0f;

	public static float turbineCoilDragMultiplier = 1.0f;
	public static float turbineAeroDragMultiplier = 1.0f;
	public static float turbineMassDragMultiplier = 1.0f;
	public static float turbineFluidPerBladeMultiplier = 1.0f;

	public static boolean isValentinesDay = false; // Easter Egg :)

	// Game Balance values

	protected static ResourceLocation iconSteamStill = BigReactors.createResourceLocation("fluids\\fluid.steam.still");
	protected static ResourceLocation iconSteamFlowing = BigReactors.createResourceLocation("fluids\\fluid.steam.flowing");
	protected static ResourceLocation iconFuelColumnStill = BigReactors.createResourceLocation("fluids\\fluid.fuelColumn.still");
	protected static ResourceLocation iconFuelColumnFlowing = BigReactors.createResourceLocation("fluids\\fluid.fuelColumn.flowing");

	private static boolean registerYelloriteSmeltToUranium = true;
	private static boolean registerYelloriumAsUranium = true;

	/**
	 * Call this function in your mod init stage.
	 */
	public static void register(Object modInstance) {

		if (!INITIALIZED) {

			// General config loading
			BRConfig.CONFIGURATION.load();
			enableWorldGen = BRConfig.CONFIGURATION.get("WorldGen", "enableWorldGen", true, "If false, disables all world gen from Big Reactors; all other worldgen settings are automatically overridden").getBoolean(true);
			enableWorldGenInNegativeDimensions = BRConfig.CONFIGURATION.get("WorldGen", "enableWorldGenInNegativeDims", false, "Run BR world generation in negative dimension IDs? (default: false) If you don't know what this is, leave it alone.").getBoolean(false);
			enableWorldRegeneration = BRConfig.CONFIGURATION.get("WorldGen", "enableWorldRegeneration", false, "Run BR World Generation in chunks that have already been generated, but have not been modified by Big Reactors before. This is largely useful for worlds that existed before BigReactors was released.").getBoolean(false);
			userWorldGenVersion = BRConfig.CONFIGURATION.get("WorldGen", "userWorldGenVersion", 0, "User-set world generation version. Increase this by 1 if you want Big Reactors to re-run world generation in your world.").getInt();
			int[] worldGenDimensionWhitelist = BRConfig.CONFIGURATION.get("WorldGen", "dimensionWhitelist", new int[] {}, "If enableWorldGenInNegativeDimensions is false, you may add negative dimensions to this whitelist to selectively enable worldgen in them.").getIntList();
			for(int i : worldGenDimensionWhitelist) {
				dimensionWhitelist.add(i);
			}

			boolean registerCoalFurnaceRecipe = BRConfig.CONFIGURATION.get("Recipes", "registerCoalForSmelting", true, "If set, coal will be smeltable into graphite bars. Disable this if other mods need to smelt coal into their own products. (Default: true)").getBoolean(true);
			boolean registerCharcoalFurnaceRecipe = BRConfig.CONFIGURATION.get("Recipes", "registerCharcoalForSmelting", true, "If set, charcoal will be smeltable into graphite bars. Disable this if other mods need to smelt charcoal into their own products. (Default: true)").getBoolean(true);
			boolean registerCoalCraftingRecipe = BRConfig.CONFIGURATION.get("Recipes", "registerGraphiteCoalCraftingRecipes", false, "If set, graphite bars can be crafted from 2 gravel, 1 coal. Use this if other mods interfere with the smelting recipe. (Default: false)").getBoolean(false);
			boolean registerCharcoalCraftingRecipe = BRConfig.CONFIGURATION.get("Recipes", "registerGraphiteCharcoalCraftingRecipes", false, "If set, graphite bars can be crafted from 2 gravel, 1 charcoal. Use this if other mods interfere with the smelting recipe. (Default: false)").getBoolean(false);
			registerYelloriteSmeltToUranium = BRConfig.CONFIGURATION.get("Recipes", "registerYelloriteSmeltToUranium", true, "If set, yellorite ore will smelt into whichever item is registered as ingotUranium in the ore dictionary. If false, it will smelt into ingotYellorium. (Default: true)").getBoolean(true);

			boolean useSteelForIron = BRConfig.CONFIGURATION.get("Recipes", "requireSteelInsteadOfIron", false, "If set, then all Big Reactors components will require steel ingots (ingotSteel) in place of iron ingots. Will be ignored if no other mod registers steel ingots. (default: false)").getBoolean(false);
			boolean useExpensiveGlass = BRConfig.CONFIGURATION.get("Recipes", "requireObsidianGlass", false, "If set, then Big Reactors will require hardened or reinforced glass (blockGlassHardened or glassReinforced) instead of plain glass. Will be ignored if no other mod registers those glass types. (default: false)").getBoolean(false);

			boolean enableReactorPowerTapRecipe = BRConfig.CONFIGURATION.get("Recipes", "enableReactorPowerTapRecipe", true, "If set, reactor power taps can be crafted, allowing players to use passive-cooled reactors.").getBoolean(true);
			boolean enableCyaniteFromYelloriumRecipe = BRConfig.CONFIGURATION.get("Recipes", "enableCyaniteFromYelloriumRecipe", true, "If set, cyanite will be craftable from yellorium ingots and sand.").getBoolean(true);

			maximumReactorSize = BRConfig.CONFIGURATION.get("General", "maxReactorSize", 32, "The maximum valid size of a reactor in the X/Z plane, in blocks. Lower this if your server's players are building ginormous reactors.").getInt();
			maximumReactorHeight = BRConfig.CONFIGURATION.get("General", "maxReactorHeight", 48, "The maximum valid size of a reactor in the Y dimension, in blocks. Lower this if your server's players are building ginormous reactors. Bigger Y sizes have far less performance impact than X/Z sizes.").getInt();
			ticksPerRedstoneUpdate = BRConfig.CONFIGURATION.get("General", "ticksPerRedstoneUpdate", 20, "Number of ticks between updates for redstone/rednet ports.").getInt();
			powerProductionMultiplier = (float) BRConfig.CONFIGURATION.get("General", "powerProductionMultiplier", 1.0f, "A multiplier for balancing overall power production from Big Reactors. Defaults to 1.").getDouble(1.0);
			fuelUsageMultiplier = (float) BRConfig.CONFIGURATION.get("General", "fuelUsageMultiplier", 1.0f, "A multiplier for balancing fuel consumption. Defaults to 1.").getDouble(1.0);

			reactorPowerProductionMultiplier = (float) BRConfig.CONFIGURATION.get("General", "reactorPowerProductionMultiplier", 1.0f, "A multiplier for balancing reactor power production. Stacks with powerProductionMultiplier. Defaults to 1.").getDouble(1.0);
			turbinePowerProductionMultiplier = (float) BRConfig.CONFIGURATION.get("General", "turbinePowerProductionMultiplier", 1.0f, "A multiplier for balancing turbine power production. Stacks with powerProductionMultiplier. Defaults to 1.").getDouble(1.0);

			maximumTurbineSize = BRConfig.CONFIGURATION.get("General", "maxTurbineSize", 16, "The maximum valid size of a turbine in the X/Z plane, in blocks. Lower this for smaller turbines, which means lower max output.").getInt();
			maximumTurbineHeight = BRConfig.CONFIGURATION.get("General", "maxTurbineHeight", 32, "The maximum valid height of a turbine (Y axis), in blocks. (Default: 32)").getInt();

			turbineCoilDragMultiplier = (float) BRConfig.CONFIGURATION.get("General", "turbineCoilDragMultiplier", 1.0, "A multiplier for balancing coil size. Multiplies the amount of energy drawn per coil block per tick. (Default: 1)").getDouble(1.0);
			turbineAeroDragMultiplier = (float) BRConfig.CONFIGURATION.get("General", "turbineAeroDragMultiplier", 1.0, "A multiplier for balancing rotor sizes. Multiplies the amount of energy lost to aerodynamic drag per tick. (Default: 1)").getDouble(1.0);
			turbineMassDragMultiplier = (float) BRConfig.CONFIGURATION.get("General", "turbineMassDragMultiplier", 1.0, "A multiplier for balancing rotor sizes. Multiplies the amount of energy lost to friction per tick. (Default: 1)").getDouble(1.0);
			turbineFluidPerBladeMultiplier = (float) BRConfig.CONFIGURATION.get("General", "turbineFluidPerBladeMultiplier", 1.0, "A multiplier for balancing coil size. Multiplies the amount of fluid each blade block can process (base of 25 will be multiplied, then rounded down to the nearest integer). (Default: 1)").getDouble(1.0);


			MultiblockTurbine.inputFluidPerBlade = (int) Math.floor(MultiblockTurbine.inputFluidPerBlade * turbineFluidPerBladeMultiplier);
			MultiblockTurbine.inductorBaseDragCoefficient *= turbineCoilDragMultiplier;

			BRConfig.CONFIGURATION.save();

			// TODO Commented temporarily to allow this thing to compile...
			/*

			if(enableWorldGen) {
				worldGenerator = new BRWorldGenerator();
				GameRegistry.registerWorldGenerator(worldGenerator, 0);
			}
			*/

			// Patch up vanilla being stupid - most mods already do this, so it's usually a no-op
			if(!OreDictionaryHelper.doesOreNameExist("ingotIron")) {
				OreDictionary.registerOre("ingotIron", new ItemStack(Items.iron_ingot, 1));
			}
			
			if(!OreDictionaryHelper.doesOreNameExist("ingotGold")) {
				OreDictionary.registerOre("ingotGold", new ItemStack(Items.gold_ingot, 1));
			}
			
			if(!OreDictionaryHelper.doesOreNameExist("blockSnow")) {
				OreDictionary.registerOre("blockSnow", new ItemStack(Blocks.snow, 1));
			}
			
			if(!OreDictionaryHelper.doesOreNameExist("blockIce")) {
				OreDictionary.registerOre("blockIce", new ItemStack(Blocks.ice, 1));
			}

			if(!OreDictionaryHelper.doesOreNameExist("blockGlassColorless")) {
				OreDictionary.registerOre("blockGlassColorless", new ItemStack(Blocks.glass, 1));
			}

			// Use steel if the players are masochists and someone else has supplied steel.
			String ironOrSteelIngot = "ingotIron";
			if (useSteelForIron) {
				ironOrSteelIngot = "ingotSteel";
			}

			String yelloriumIngot = "ingotYellorium";
			String blutoniumIngot = "ingotBlutonium";
			if (registerYelloriumAsUranium) {
				yelloriumIngot = "ingotUranium";
				blutoniumIngot = "ingotPlutonium";
			}
			
			/*
			 * Register Recipes
			 */
			// Recipe Registry

			// Yellorium
			if (BrBlocks.brOre != null) {

				ItemStack product;

				if (registerYelloriteSmeltToUranium) {

					List<ItemStack> candidateOres = OreDictionary.getOres("ingotUranium");

					if (candidateOres == null || candidateOres.size() <= 0) {

						BRLog.warning("Config value registerYelloriteSmeltToUranium is set to True, but there are no ores registered as ingotUranium in the ore dictionary! Falling back to using standard yellorium only.");
						candidateOres = OreDictionary.getOres("ingotYellorium");
					}
					product = candidateOres.get(0).copy();
				}
				else {
					product = OreDictionary.getOres("ingotYellorium").get(0).copy();
				}

				GameRegistry.addSmelting(erogenousbeef.bigreactors.init.BrBlocks.brOre, product, 0.5f);
			}


			// Metal blocks
			if (BrBlocks.blockMetals != null && BrItems.ingotMetals != null) {
				BrBlocks.blockMetals.registerIngotRecipes();
			}

			if (BrBlocks.blockMetals != null) {

				// Ludicrite block. Because.

				ItemStack ludicriteBlock = BrBlocks.blockMetals.createItemStack(MetalType.Ludicrite, 1);

				GameRegistry.addRecipe(new ShapedOreRecipe(ludicriteBlock, "BPB", "ENE", "BPB", 'N',
						Items.nether_star, 'P', Items.ender_pearl, 'E', Blocks.emerald_block, 'B', blutoniumIngot));

				if (OreDictionaryHelper.doesOreNameExist("blockEnderium")) {

					// Ok, how about some ludicrous shit here. Enderium and blaze rods. Have fun, bucko.
					GameRegistry.addRecipe(new ShapedOreRecipe(ludicriteBlock, "BRB", "E E", "BRB", 'B',
							blutoniumIngot, 'R', Items.blaze_rod, 'E', "blockEnderium"));
				}
			}

			if (BrItems.ingotMetals != null && BrItems.dustMetals != null) {

				// Map all dusts to ingots.

				MetalType[] metals = MetalType.values();

				for (int i = 0; i < metals.length; ++i) {

					ItemStack ingotStack = BrItems.ingotMetals.createItemStack(metals[i], 1);
					ItemStack dustStack = BrItems.dustMetals.createItemStack(metals[i], 1);

					GameRegistry.addSmelting(dustStack, ingotStack, 0f);
				}
			}

			ItemStack ingotGraphite = OreDictionary.getOres("ingotGraphite").get(0).copy();
			ItemStack ingotCyanite = OreDictionary.getOres("ingotCyanite").get(0).copy();
			
			if(registerCoalFurnaceRecipe) {
				// Coal -> Graphite
				GameRegistry.addSmelting(Items.coal, ingotGraphite, 1);
			}
			
			if(registerCharcoalFurnaceRecipe) {
				// Charcoal -> Graphite
				GameRegistry.addSmelting(new ItemStack(Items.coal, 1, 1), ingotGraphite, 1);
			}
			
			if(registerCoalCraftingRecipe) {
				GameRegistry.addRecipe(new ShapedOreRecipe(ingotGraphite, "GCG", 'G', Blocks.gravel, 'C', new ItemStack(Items.coal, 1, 0)));
			}
			
			if(registerCharcoalCraftingRecipe) {
				GameRegistry.addRecipe(new ShapedOreRecipe( ingotGraphite, "GCG", 'G', Blocks.gravel, 'C', new ItemStack(Items.coal, 1, 1)));
			}
			
			if(enableCyaniteFromYelloriumRecipe) {
				GameRegistry.addRecipe(new ShapelessOreRecipe(ingotCyanite, yelloriumIngot, Blocks.sand ));
			}

			// reactor parts

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorFuelRod.createItemStack(), "ICI", "IUI", "ICI", 'I',
					ironOrSteelIngot, 'C', "ingotGraphite", 'U', yelloriumIngot));

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorCasing.createItemStack(4), "ICI", "CUC", "ICI", 'I',
					ironOrSteelIngot, 'C', "ingotGraphite", 'U', yelloriumIngot));

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorController.createItemStack(), "C C", "GDG", "CRC", 'D',
					Items.diamond, 'G', yelloriumIngot, 'C', "reactorCasing", 'R', Items.redstone));

			if (enableReactorPowerTapRecipe) {
				GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorPowerTap.createItemStack(), "CRC", "R R", "CRC", 'C',
						"reactorCasing", 'R', Items.redstone));
			}

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorAccessPort.createItemStack(), "C C", " V ", "CPC", 'C',
					"reactorCasing", 'V', Blocks.chest, 'P', Blocks.piston));

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorCoolantPort.createItemStack(), "C C", "IVI", "CPC", 'C',
					"reactorCasing", 'V', Items.bucket, 'P', Blocks.piston, 'I', ironOrSteelIngot));

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorControlRod.createItemStack(), "CGC", "GRG", "CUC", 'G',
					"ingotGraphite", 'C', "reactorCasing", 'R', Items.redstone, 'U', yelloriumIngot));

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorRedstonePort.createItemStack(), "CRC", "RGR", "CRC", 'C',
					"reactorCasing", 'R', Items.redstone, 'G', Items.gold_ingot));

			if (Loader.isModLoaded("MineFactoryReloaded")) {
				GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorRedNetPort.createItemStack(), "CRC", "RGR", "CRC", 'C',
						"reactorCasing", 'R', "cableRedNet", 'G', "ingotGold"));
			}

			if (Loader.isModLoaded("ComputerCraft") || Loader.isModLoaded("OpenComputers"))
				GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorComputerPort, "CRC", "GPG", "CRC", 'C',
						"reactorCasing", 'R', Items.redstone, 'G', "ingotGold", 'P', Items.repeater));

			// reactor and turbine glass

			ItemStack reactorGlassStack = BrBlocks.reactorGlass.createItemStack();
			ItemStack turbineGlassStack = BrBlocks.turbineGlass.createItemStack();

			if (useExpensiveGlass && (OreDictionaryHelper.doesOreNameExist("glassReinforced") ||
					OreDictionaryHelper.doesOreNameExist("blockGlassHardened"))) {

				GameRegistry.addRecipe(new ShapedOreRecipe(reactorGlassStack, "GCG", 'G', "glassReinforced", 'C', "reactorCasing"));
				GameRegistry.addRecipe(new ShapedOreRecipe(reactorGlassStack, "GCG", 'G', "blockGlassHardened", 'C', "reactorCasing"));

				GameRegistry.addRecipe(new ShapedOreRecipe(turbineGlassStack, "GCG", 'G', "glassReinforced", 'C', "turbineHousing"));
				GameRegistry.addRecipe(new ShapedOreRecipe(turbineGlassStack, "GCG", 'G', "blockGlassHardened", 'C', "turbineHousing"));
			}
			else
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(reactorGlassStack, "GCG", 'G', "blockGlassColorless", 'C', "reactorCasing"));
				GameRegistry.addRecipe(new ShapedOreRecipe(turbineGlassStack, "GCG", 'G', "blockGlassColorless", 'C', "turbineHousing"));
			}

			// generic devices

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.deviceCyaniteRep.createItemStack(), "CIC", "PFP", "CRC", 'C',
					"reactorCasing", 'I', ironOrSteelIngot, 'F', BrBlocks.reactorFuelRod, 'P', Blocks.piston, 'R', Items.redstone));

			// turbine parts

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbineHousing.createItemStack(4), "IGI", "QCQ", "IGI", 'C',
					"ingotCyanite", 'I', ironOrSteelIngot, 'Q', Items.quartz, 'G', "ingotGraphite"));

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbineController.createItemStack(), "H H", "BDB", "H H", 'H',
					"turbineHousing", 'D', Items.diamond, 'B', blutoniumIngot));

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbinePowerTap.createItemStack(), "HRH", "R R", "HRH", 'H',
					"turbineHousing", 'R', Items.redstone));

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbineFluidPort.createItemStack(), "H H", "IVI", "HPH", 'H',
					"turbineHousing", 'I', ironOrSteelIngot, 'V', Items.bucket, 'P', Blocks.piston));

			GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbineBearing.createItemStack(), "HRH", "DDD", "HRH", 'H',
					"turbineHousing", 'D', Items.diamond, 'R', "turbineRotorShaft"));

			if (Loader.isModLoaded("ComputerCraft") || Loader.isModLoaded("OpenComputers"))
				GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.turbineComputerPort.createItemStack(), "HRH", "GPG", "HRH", 'H',
						"turbineHousing", 'G', "ingotGold", 'R', "turbineRotorShaft"));

			if (blockTurbineRotorPart != null) {
				ItemStack rotorShaft = blockTurbineRotorPart.getItemStack("rotor");
				ItemStack rotorBlade = blockTurbineRotorPart.getItemStack("blade");

				GameRegistry.addRecipe(new ShapedOreRecipe(rotorShaft, "ICI", 'C', "ingotCyanite", 'I', ironOrSteelIngot));
				GameRegistry.addRecipe(new ShapedOreRecipe(rotorBlade, "CII", 'C', "ingotCyanite", 'I', ironOrSteelIngot));
			}

			registerGameBalanceData();
		}

		INITIALIZED = true;
	}

	public static ItemStack registerOres(int i, boolean b) {
		BRConfig.CONFIGURATION.load();



		boolean genYelloriteOre = BRConfig.CONFIGURATION.get("WorldGen", "GenerateYelloriteOre", true, "Add yellorite ore during world generation?").getBoolean(true);
		// TODO Commented temporarily to allow this thing to compile...
		/*
		if (yelloriteOreGeneration == null && genYelloriteOre) {
			// Magic number: 1 = stone
			int clustersPerChunk;
			int orePerCluster;
			int maxY;

			clustersPerChunk = BRConfig.CONFIGURATION.get("WorldGen", "MaxYelloriteClustersPerChunk", 5, "Maximum number of clusters per chunk; will generate at least half this number, rounded down").getInt();
			orePerCluster = BRConfig.CONFIGURATION.get("WorldGen", "MaxYelloriteOrePerCluster", 10, "Maximum number of blocks to generate in each cluster; will usually generate at least half this number").getInt();
			maxY = BRConfig.CONFIGURATION.get("WorldGen", "YelloriteMaxY", 50, "Maximum height (Y coordinate) in the world to generate yellorite ore").getInt();
			int[] dimensionBlacklist = BRConfig.CONFIGURATION.get("WorldGen", "YelloriteDimensionBlacklist", new int[]{}, "Dimensions in which yellorite ore should not be generated; Nether/End automatically included").getIntList();


			yelloriteOreGeneration = new BRSimpleOreGenerator(erogenousbeef.bigreactors.init.Blocks.brOre, 0, Blocks.stone,
											clustersPerChunk/2, clustersPerChunk, 4, maxY, orePerCluster);

			// Per KingLemming's request, bonus yellorite around y12. :)
			BRSimpleOreGenerator yelloriteOreGeneration2 = new BRSimpleOreGenerator(erogenousbeef.bigreactors.init.Blocks.brOre, 0, Blocks.stone,
					1, 2, 11, 13, orePerCluster);

			if(dimensionBlacklist != null) {
				for(int dimension : dimensionBlacklist) {
					yelloriteOreGeneration.blacklistDimension(dimension);
					yelloriteOreGeneration2.blacklistDimension(dimension);
				}
			}

			BRWorldGenerator.addGenerator(BigReactors.yelloriteOreGeneration);
			BRWorldGenerator.addGenerator(yelloriteOreGeneration2);
		}
		*/

		BRConfig.CONFIGURATION.save();


		return new ItemStack(BrBlocks.brOre);
	}

	public static void registerFluids(int id, boolean require) {
		// TODO Commented temporarily to allow this thing to compile...
		/*
		if(BigReactors.fluidYelloriumStill == null) {
			BRConfig.CONFIGURATION.load();
			
			BigReactors.fluidYellorium = FluidRegistry.getFluid("yellorium");
			if(fluidYellorium == null) {
				fluidYellorium = new Fluid("yellorium");
				fluidYellorium.setDensity(100);
				fluidYellorium.setGaseous(false);
				fluidYellorium.setLuminosity(10);
				fluidYellorium.setRarity(EnumRarity.UNCOMMON);
				fluidYellorium.setTemperature(295);
				fluidYellorium.setViscosity(100);
				fluidYellorium.setUnlocalizedName("bigreactors.yellorium.still");
				FluidRegistry.registerFluid(fluidYellorium);
			}

			BlockBRGenericFluid liqY = new BlockBRGenericFluid(BigReactors.fluidYellorium, "yellorium");
			BigReactors.fluidYelloriumStill = liqY;
			
			GameRegistry.registerBlock(BigReactors.fluidYelloriumStill, ItemBlock.class, BigReactors.fluidYelloriumStill.getUnlocalizedName());

			fluidYelloriumBucketItem = (new ItemBRBucket(liqY)).setUnlocalizedName("bucket.yellorium").setMaxStackSize(1).setContainerItem(Items.bucket);
            GameRegistry.registerItem(fluidYelloriumBucketItem, "bucketYellorium");
			
			BRConfig.CONFIGURATION.save();
		}
		*/

		if (BigReactors.fluidCyaniteStill == null) {
			// TODO Commented temporarily to allow this thing to compile...
			/*
			BRConfig.CONFIGURATION.load();
			
			BigReactors.fluidCyanite = FluidRegistry.getFluid("cyanite");
			if(fluidCyanite == null) {
				fluidCyanite = new Fluid("cyanite");
				fluidCyanite.setDensity(100);
				fluidCyanite.setGaseous(false);
				fluidCyanite.setLuminosity(6);
				fluidCyanite.setRarity(EnumRarity.UNCOMMON);
				fluidCyanite.setTemperature(295);
				fluidCyanite.setViscosity(100);
				fluidCyanite.setUnlocalizedName("bigreactors.cyanite.still");
				FluidRegistry.registerFluid(fluidCyanite);
			}

			BlockBRGenericFluid liqDY = new BlockBRGenericFluid(fluidCyanite, "cyanite");
			BigReactors.fluidCyaniteStill = liqDY;
			GameRegistry.registerBlock(BigReactors.fluidCyaniteStill, ItemBlock.class, BigReactors.fluidCyaniteStill.getUnlocalizedName());
			
			fluidCyaniteBucketItem = (new ItemBRBucket(liqDY)).setUnlocalizedName("bucket.cyanite").setMaxStackSize(1).setContainerItem(Items.bucket);
            GameRegistry.registerItem(fluidCyaniteBucketItem, "bucketCyanite");
			
			BRConfig.CONFIGURATION.save();
			*/
		}

		// TODO fix config and null checks
		if (BigReactors.fluidFuelColumnStill == null) {

			BRConfig.CONFIGURATION.load();
			
			BigReactors.fluidFuelColumn = FluidRegistry.getFluid("fuelColumn");
			if(fluidFuelColumn == null) {
				fluidFuelColumn = new Fluid("fuelColumn", iconFuelColumnStill, iconFuelColumnFlowing);
				fluidFuelColumn.setUnlocalizedName("bigreactors.fuelColumn.still");
				FluidRegistry.registerFluid(fluidFuelColumn);				
			}

			BRConfig.CONFIGURATION.save();
		}

		// TODO fix config and null checks
		fluidSteam = FluidRegistry.getFluid("steam");
		registeredOwnSteam = false;
		if (fluidSteam == null) {
			// FINE THEN

			BRConfig.CONFIGURATION.load();
			
			fluidSteam = new Fluid("steam", iconSteamStill, iconSteamFlowing);
			fluidSteam.setUnlocalizedName("steam");
			fluidSteam.setTemperature(1000); // For consistency with TE
			fluidSteam.setGaseous(true);
			fluidSteam.setLuminosity(0);
			fluidSteam.setRarity(EnumRarity.COMMON);
			fluidSteam.setDensity(6);
			
			registeredOwnSteam = true;
			
			FluidRegistry.registerFluid(fluidSteam);

			BRConfig.CONFIGURATION.save();
		}

	}

	// This must be done in init or later
	protected static void registerGameBalanceData() {
		// Register ingot & block => reactant mappings
		StandardReactants.yelloriumMapping = Reactants.registerSolid("ingotYellorium", StandardReactants.yellorium);
		StandardReactants.cyaniteMapping = Reactants.registerSolid("ingotCyanite", StandardReactants.cyanite);

		Reactants.registerSolid("ingotBlutonium", StandardReactants.blutonium);

		ItemStack blockYellorium = BrBlocks.blockMetals.createItemStack(MetalType.Yellorium, 1);
		Reactants.registerSolid(blockYellorium, StandardReactants.yellorium, Reactants.standardSolidReactantAmount * 9);

		ItemStack blockBlutonium = BrBlocks.blockMetals.createItemStack(MetalType.Blutonium, 1);
		Reactants.registerSolid(blockBlutonium, StandardReactants.blutonium, Reactants.standardSolidReactantAmount * 9);

		// Register fluid => reactant mappings
		Reactants.registerFluid(fluidYellorium, StandardReactants.yellorium);
		Reactants.registerFluid(fluidCyanite, StandardReactants.cyanite);

		// Register reactant => reactant conversions for making cyanite
		ReactorConversions.register(StandardReactants.yellorium, StandardReactants.cyanite);
		ReactorConversions.register(StandardReactants.blutonium, StandardReactants.cyanite);

		BRConfig.CONFIGURATION.load();
		boolean enableFantasyMetals = BRConfig.CONFIGURATION.get("General", "enableMetallurgyFantasyMetalsInTurbines", true, "If true, allows Metallurgy's fantasy metals to be used as part of turbine coils. Default: true").getBoolean(true);
		boolean enableComedy = BRConfig.CONFIGURATION.get("General", "enableComedy", true, "If true, allows weird stuff inside reactors, like MFR sewage and pink slime. Default: true").getBoolean(true);
		BRConfig.CONFIGURATION.save();

		TurbineCoil.registerBlock("blockIron", 1f, 1f, 1f);
		TurbineCoil.registerBlock("blockGold", 2f, 1f, 1.75f);

		TurbineCoil.registerBlock("blockCopper", 1.2f, 1f, 1.2f);    // TE, lots of mods
		TurbineCoil.registerBlock("blockOsmium", 1.2f, 1f, 1.2f);    // Mekanism
		TurbineCoil.registerBlock("blockZinc", 1.35f, 1f, 1.3f);
		TurbineCoil.registerBlock("blockLead", 1.35f, 1.01f, 1.3f);// TE, Mekanism, some others
		TurbineCoil.registerBlock("blockBrass", 1.4f, 1f, 1.2f);    // Metallurgy
		TurbineCoil.registerBlock("blockBronze", 1.4f, 1f, 1.2f);    // Mekanism, many others
		TurbineCoil.registerBlock("blockAluminum", 1.5f, 1f, 1.3f);    // TiCo, couple others
		TurbineCoil.registerBlock("blockSteel", 1.5f, 1f, 1.3f);    // Metallurgy, Mek, etc.
		TurbineCoil.registerBlock("blockInvar", 1.5f, 1f, 1.4f);    // TE
		TurbineCoil.registerBlock("blockSilver", 1.7f, 1f, 1.5f);    // TE, lots of mods
		TurbineCoil.registerBlock("blockElectrum", 2.5f, 1f, 2.0f);    // TE, lots of mods
		TurbineCoil.registerBlock("blockElectrumFlux", 2.5f, 1.01f, 2.2f);    // Redstone Arsenal, note small energy bonus (7% at 1000RF/t output)
		TurbineCoil.registerBlock("blockPlatinum", 3.0f, 1f, 2.5f);    // TE, lots of mods
		TurbineCoil.registerBlock("blockShiny", 3.0f, 1f, 2.5f);    // TE
		TurbineCoil.registerBlock("blockTitanium", 3.1f, 1f, 2.7f);    // Mariculture
		TurbineCoil.registerBlock("blockEnderium", 3.0f, 1.02f, 3.0f);    // TE, note tiny energy bonus!	(14% at 1000RF/t output)

		TurbineCoil.registerBlock("blockLudicrite", 3.5f, 1.02f, 3.5f);

		if (enableFantasyMetals) {
			// Metallurgy fantasy metals
			TurbineCoil.registerBlock("blockMithril", 2.2f, 1f, 1.5f);
			TurbineCoil.registerBlock("blockOrichalcum", 2.3f, 1f, 1.7f);
			TurbineCoil.registerBlock("blockQuicksilver", 2.6f, 1f, 1.8f);
			TurbineCoil.registerBlock("blockHaderoth", 3.0f, 1f, 2.0f);
			TurbineCoil.registerBlock("blockCelenegil", 3.3f, 1f, 2.25f);
			TurbineCoil.registerBlock("blockTartarite", 3.5f, 1f, 2.5f);
			TurbineCoil.registerBlock("blockManyullyn", 3.5f, 1f, 2.5f);
		}

		ReactorInterior.registerBlock("blockIron", 0.50f, 0.75f, 1.40f, IHeatEntity.conductivityIron);
		ReactorInterior.registerBlock("blockGold", 0.52f, 0.80f, 1.45f, IHeatEntity.conductivityGold);
		ReactorInterior.registerBlock("blockDiamond", 0.55f, 0.85f, 1.50f, IHeatEntity.conductivityDiamond);
		ReactorInterior.registerBlock("blockEmerald", 0.55f, 0.85f, 1.50f, IHeatEntity.conductivityEmerald);
		ReactorInterior.registerBlock("blockGraphite", 0.10f, 0.50f, 2.00f, IHeatEntity.conductivityGold); // Graphite: a great moderator!
		ReactorInterior.registerBlock("blockGlassColorless", 0.20f, 0.25f, 1.10f, IHeatEntity.conductivityGlass);
		ReactorInterior.registerBlock("blockIce", 0.33f, 0.33f, 1.15f, IHeatEntity.conductivityWater);
		ReactorInterior.registerBlock("blockSnow", 0.15f, 0.33f, 1.05f, IHeatEntity.conductivityWater / 2f);

		// Mod blocks
		ReactorInterior.registerBlock("blockCopper", 0.50f, 0.75f, 1.40f, IHeatEntity.conductivityCopper);
		ReactorInterior.registerBlock("blockOsmium", 0.51f, 0.77f, 1.41f, IHeatEntity.conductivityCopper);
		ReactorInterior.registerBlock("blockBrass", 0.51f, 0.77f, 1.41f, IHeatEntity.conductivityCopper);
		ReactorInterior.registerBlock("blockBronze", 0.51f, 0.77f, 1.41f, IHeatEntity.conductivityCopper);
		ReactorInterior.registerBlock("blockZinc", 0.51f, 0.77f, 1.41f, IHeatEntity.conductivityCopper);
		ReactorInterior.registerBlock("blockAluminum", 0.50f, 0.78f, 1.42f, IHeatEntity.conductivityIron);
		ReactorInterior.registerBlock("blockSteel", 0.50f, 0.78f, 1.42f, IHeatEntity.conductivityIron);
		ReactorInterior.registerBlock("blockInvar", 0.50f, 0.79f, 1.43f, IHeatEntity.conductivityIron);
		ReactorInterior.registerBlock("blockSilver", 0.51f, 0.79f, 1.43f, IHeatEntity.conductivitySilver);
		ReactorInterior.registerBlock("blockLead", 0.75f, 0.75f, 1.75f, IHeatEntity.conductivitySilver);
		ReactorInterior.registerBlock("blockElectrum", 0.53f, 0.82f, 1.47f, 2.2f); // Between gold and emerald
		ReactorInterior.registerBlock("blockElectrumFlux", 0.54f, 0.83f, 1.48f, 2.4f); // Between gold and emerald
		ReactorInterior.registerBlock("blockPlatinum", 0.57f, 0.86f, 1.58f, IHeatEntity.conductivityEmerald);
		ReactorInterior.registerBlock("blockShiny", 0.57f, 0.86f, 1.58f, IHeatEntity.conductivityEmerald);
		ReactorInterior.registerBlock("blockTitanium", 0.58f, 0.87f, 1.59f, 2.7f); // Mariculture
		ReactorInterior.registerBlock("blockEnderium", 0.60f, 0.88f, 1.60f, IHeatEntity.conductivityDiamond);

		if (enableFantasyMetals) {
			ReactorInterior.registerBlock("blockMithril", 0.53f, 0.81f, 1.45f, IHeatEntity.conductivitySilver);
			ReactorInterior.registerBlock("blockOrichalcum", 0.52f, 0.83f, 1.46f, 1.7f);    // Between silver and gold
			ReactorInterior.registerBlock("blockQuicksilver", 0.53f, 0.84f, 1.48f, IHeatEntity.conductivityGold);
			ReactorInterior.registerBlock("blockHaderoth", 0.54f, 0.84f, 1.49f, IHeatEntity.conductivityEmerald);
			ReactorInterior.registerBlock("blockCelenegil", 0.54f, 0.84f, 1.49f, IHeatEntity.conductivityDiamond);
			ReactorInterior.registerBlock("blockTartarite", 0.65f, 0.90f, 1.62f, 4f); // Between diamond and graphene
			ReactorInterior.registerBlock("blockManyullyn", 0.68f, 0.88f, 1.75f, 4.5f);
		}

		//Water: 0.33f, 0.5f, 1.33f
		ReactorInterior.registerFluid("water", RadiationHelper.waterData.absorption, RadiationHelper.waterData.heatEfficiency, RadiationHelper.waterData.moderation, IHeatEntity.conductivityWater);
		ReactorInterior.registerFluid("redstone", 0.75f, 0.55f, 1.60f, IHeatEntity.conductivityEmerald);
		ReactorInterior.registerFluid("glowstone", 0.20f, 0.60f, 1.75f, IHeatEntity.conductivityCopper);
		ReactorInterior.registerFluid("cryotheum", 0.66f, 0.95f, 6.00f, IHeatEntity.conductivityDiamond); // Cryotheum: an amazing moderator!
		ReactorInterior.registerFluid("ender", 0.90f, 0.75f, 2.00f, IHeatEntity.conductivityGold);
		ReactorInterior.registerFluid("pyrotheum", 0.66f, 0.90f, 1.00f, IHeatEntity.conductivityIron);

		ReactorInterior.registerFluid("life essence", 0.70f, 0.55f, 1.75f, IHeatEntity.conductivityGold); // From Blood Magic

		if (enableComedy) {
			ReactorInterior.registerBlock("blockMeat", 0.50f, 0.33f, 1.33f, IHeatEntity.conductivityStone);
			ReactorInterior.registerBlock("blockMeatRaw", 0.40f, 0.50f, 1.50f, IHeatEntity.conductivityStone);
			ReactorInterior.registerFluid("meat", 0.40f, 0.60f, 1.33f, IHeatEntity.conductivityStone);
			ReactorInterior.registerFluid("pinkSlime", 0.45f, 0.70f, 1.50f, IHeatEntity.conductivityIron);
			ReactorInterior.registerFluid("sewage", 0.50f, 0.65f, 1.44f, IHeatEntity.conductivityIron);
		}
	}

	public static ResourceLocation createResourceLocation(String path) {

		return new ResourceLocation(BigReactors.MODID, path);
	}

	public static ResourceLocation createGuiResourceLocation(String path) {

		return BigReactors.createResourceLocation("textures/gui/" + path);
	}




	// Thanks KingLemming!
	@SideOnly(Side.CLIENT)
	public static void registerNonBlockFluidIcons(TextureMap map) {

		map.registerSprite(BigReactors.iconFuelColumnStill);
		map.registerSprite(BigReactors.iconFuelColumnFlowing);
		
		if (registeredOwnSteam) {
			map.registerSprite(BigReactors.iconSteamStill);
			map.registerSprite(BigReactors.iconSteamFlowing);
		}
	}
}
