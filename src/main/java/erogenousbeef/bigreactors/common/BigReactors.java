package erogenousbeef.bigreactors.common;

import erogenousbeef.bigreactors.api.IHeatEntity;
import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.api.registry.ReactorConversions;
import erogenousbeef.bigreactors.api.registry.ReactorInterior;
import erogenousbeef.bigreactors.api.registry.TurbineCoil;
import erogenousbeef.bigreactors.common.config.Config;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import erogenousbeef.bigreactors.common.multiblock.helpers.RadiationHelper;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrFluids;
import erogenousbeef.bigreactors.init.InitHandler;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import it.zerono.mods.zerocore.lib.IModInitializationHandler;
import it.zerono.mods.zerocore.lib.gui.ModGuiHandler;
import it.zerono.mods.zerocore.lib.world.IWorldGenWhiteList;
import it.zerono.mods.zerocore.lib.world.WorldGenMinableOres;
import it.zerono.mods.zerocore.lib.world.WorldGenWhiteList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Calendar;

@Mod(modid = BigReactors.MODID, name = BigReactors.NAME, version = "0.0.0.0",
		acceptedMinecraftVersions = "", dependencies = "required-after:Forge;required-after:zerocore",
		guiFactory = "erogenousbeef.bigreactors.client.config.ConfigFactory")
public class BigReactors implements IModInitializationHandler {

	public static final String NAME = "Extreme Reactors";
	public static final String MODID = "bigreactors";
	public static final int WORLDGEN_VERSION = 1; // Bump this when changing world generation so the world regens
	public static final Config CONFIG;
	public static final CreativeTabs TAB;
	public static final IWorldGenWhiteList WHITELIST_WORLDGEN_ORES;
	public static final WorldGenMinableOres WORLDGEN_ORES;
	public static final WorldGenMinableOres NETHER_ORES;
	public static final WorldGenMinableOres END_ORES;
	public static final BigReactorsTickHandler TICK_HANDLER;
	public static final boolean VALENTINES_DAY; // Easter Egg :)

	public static final int defaultFluidColorFuel = 0xbcba50;
	public static final int defaultFluidColorWaste = 0x4d92b5;

	public static BigReactors getInstance() {
		return BigReactors.s_instance;
	}

	public static CommonProxy getProxy() {
		return BigReactors.s_proxy;
	}

	@Mod.EventHandler
	@Override
	public void onPreInit(FMLPreInitializationEvent event) {

		CONFIG.onPreInit(event);
		InitHandler.INSTANCE.onPreInit(event);
		StandardReactants.register();
		MinecraftForge.EVENT_BUS.register(new BREventHandler());
		MinecraftForge.EVENT_BUS.register(BigReactors.s_proxy);
		BigReactors.s_proxy.onPreInit(event);
	}

	@Mod.EventHandler
	@Override
	public void onInit(FMLInitializationEvent event) {

		CONFIG.onInit(event);
		InitHandler.INSTANCE.onInit(event);

		// add world generator for our ores
		if (CONFIG.enableWorldGen) {

			GameRegistry.registerWorldGenerator(WORLDGEN_ORES, 0);
			GameRegistry.registerWorldGenerator(NETHER_ORES, 0);
			GameRegistry.registerWorldGenerator(END_ORES, 0);
			MinecraftForge.EVENT_BUS.register(TICK_HANDLER);
		}

		CommonPacketHandler.init();
		new ModGuiHandler(BigReactors.s_instance);
		BigReactors.s_proxy.onInit(event);
		this.registerGameBalanceData();
	}

	@Mod.EventHandler
	@Override
	public void onPostInit(FMLPostInitializationEvent event) {

		CONFIG.onPostInit(event);
		InitHandler.INSTANCE.onPostInit(event);
		BigReactors.s_proxy.onPostInit(event);
	}

	// This must be done in init or later
	protected void registerGameBalanceData() {
		// Register ingot & block => reactant mappings
		StandardReactants.yelloriumMapping = Reactants.registerSolid("ingotYellorium", StandardReactants.yellorium);
		StandardReactants.cyaniteMapping = Reactants.registerSolid("ingotCyanite", StandardReactants.cyanite);

		Reactants.registerSolid("ingotBlutonium", StandardReactants.blutonium);

		ItemStack blockYellorium = BrBlocks.blockMetals.createItemStack(MetalType.Yellorium, 1);
		Reactants.registerSolid(blockYellorium, StandardReactants.yellorium, Reactants.standardSolidReactantAmount * 9);

		ItemStack blockBlutonium = BrBlocks.blockMetals.createItemStack(MetalType.Blutonium, 1);
		Reactants.registerSolid(blockBlutonium, StandardReactants.blutonium, Reactants.standardSolidReactantAmount * 9);

		// Register fluid => reactant mappings
		Reactants.registerFluid(BrFluids.fluidYellorium, StandardReactants.yellorium);
		Reactants.registerFluid(BrFluids.fluidCyanite, StandardReactants.cyanite);

		// Register reactant => reactant conversions for making cyanite
		ReactorConversions.register(StandardReactants.yellorium, StandardReactants.cyanite);
		ReactorConversions.register(StandardReactants.blutonium, StandardReactants.cyanite);


		boolean enableFantasyMetals = BigReactors.CONFIG.enableMetallurgyFantasyMetalsInTurbines;
		boolean enableComedy = BigReactors.CONFIG.enableComedy;

		TurbineCoil.registerBlock("blockIron", 1f, 1f, 1f);
		TurbineCoil.registerBlock("blockGold", 2f, 1f, 1.75f);

		TurbineCoil.registerBlock("blockCopper", 1.2f, 1f, 1.2f);    // TE/Modularity, lots of mods

		TurbineCoil.registerBlock("blockOsmium", 1.2f, 1f, 1.2f);    // Mekanism
		TurbineCoil.registerBlock("blockCobalt", 1.2f, 1f, 1.2f);	// Tinkers' Construct

		TurbineCoil.registerBlock("blockZinc", 1.35f, 1f, 1.3f);
		TurbineCoil.registerBlock("blockArdite", 1.35f, 1f, 1.3f);	// Tinkers' Construct

		TurbineCoil.registerBlock("blockLead", 1.35f, 1.01f, 1.3f);// TE/Modularity, Mekanism, some others

		TurbineCoil.registerBlock("blockBrass", 1.4f, 1f, 1.2f);    // Metallurgy
		TurbineCoil.registerBlock("blockAlubrass", 1.4f, 1f, 1.2f);    // Tinkers' Construct

		TurbineCoil.registerBlock("blockBronze", 1.4f, 1f, 1.2f);    // Mekanism, many others
		TurbineCoil.registerBlock("blockAluminum", 1.5f, 1f, 1.3f);    // TiCo, couple others
		TurbineCoil.registerBlock("blockSteel", 1.5f, 1f, 1.3f);    // Metallurgy, Mek, etc.
		TurbineCoil.registerBlock("blockInvar", 1.5f, 1f, 1.4f);    // TE/Modularity
		TurbineCoil.registerBlock("blockSilver", 1.7f, 1f, 1.5f);    // TE/Modularity, lots of mods
		TurbineCoil.registerBlock("blockElectrum", 2.5f, 1f, 2.0f);    // TE/Modularity, lots of mods
		TurbineCoil.registerBlock("blockElectrumFlux", 2.5f, 1.01f, 2.2f);    // Redstone Arsenal, note small energy bonus (7% at 1000RF/t output)
		TurbineCoil.registerBlock("blockPlatinum", 3.0f, 1f, 2.5f);    // TE/Modularity, lots of mods

		TurbineCoil.registerBlock("blockShiny", 3.0f, 1f, 2.5f);    // TE
		TurbineCoil.registerBlock("blockManyullyn", 3.0f, 1f, 2.5f);    // kers' Construct

		TurbineCoil.registerBlock("blockTitanium", 3.1f, 1f, 2.7f);    // Mariculture
		TurbineCoil.registerBlock("blockEnderium", 3.0f, 1.02f, 3.0f);    // TE/Modularity, note tiny energy bonus!	(14% at 1000RF/t output)

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
		ReactorInterior.registerFluid("fluidredstone", 0.75f, 0.55f, 1.60f, IHeatEntity.conductivityEmerald);
		ReactorInterior.registerFluid("fluidtesla", 0.75f, 0.55f, 1.60f, IHeatEntity.conductivityEmerald); // Modularity
		ReactorInterior.registerFluid("fluidglowstone", 0.20f, 0.60f, 1.75f, IHeatEntity.conductivityCopper);
		ReactorInterior.registerFluid("fluidcryotheum", 0.66f, 0.95f, 6.00f, IHeatEntity.conductivityDiamond); // Cryotheum: an amazing moderator!
		ReactorInterior.registerFluid("fluidender", 0.90f, 0.75f, 2.00f, IHeatEntity.conductivityGold);
		ReactorInterior.registerFluid("fluidpyrotheum", 0.66f, 0.90f, 1.00f, IHeatEntity.conductivityIron);
		ReactorInterior.registerFluid("lifeessence", 0.70f, 0.55f, 1.75f, IHeatEntity.conductivityGold); // From Blood Magic

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

	public static ResourceLocation createBlockResourceLocation(String path) {

		return BigReactors.createResourceLocation("blocks/" + path);
	}

	@Mod.Instance(MODID)
	private static BigReactors s_instance;

	@SidedProxy(clientSide = "erogenousbeef.bigreactors.client.ClientProxy", serverSide = "erogenousbeef.bigreactors.common.CommonProxy")
	private static CommonProxy s_proxy;

	/*
	@Mod.Metadata(MODID)
	private static ModMetadata s_metadata;
	*/

	static {

		FluidRegistry.enableUniversalBucket();

		CONFIG = new Config();

		WHITELIST_WORLDGEN_ORES = new WorldGenWhiteList();
		WORLDGEN_ORES = new WorldGenMinableOres(WHITELIST_WORLDGEN_ORES);
		TICK_HANDLER = new BigReactorsTickHandler(WORLDGEN_ORES);

		WorldGenWhiteList whiteList;

		whiteList = new WorldGenWhiteList();
		whiteList.whiteListDimension(-1);
		NETHER_ORES = new WorldGenMinableOres(whiteList);

		whiteList = new WorldGenWhiteList();
		whiteList.whiteListDimension(1);
		END_ORES = new WorldGenMinableOres(whiteList);

		TAB = new CreativeTabBR(MODID);

		// Easter Egg - Check if today is valentine's day. If so, change all particles to hearts.
		Calendar calendar = Calendar.getInstance();
		VALENTINES_DAY = (calendar.get(Calendar.MONTH) == 1 && calendar.get(Calendar.DAY_OF_MONTH) == 14);
	}
}
