package erogenousbeef.bigreactors.common.config;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.block.OreType;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.lib.config.ConfigHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.ConfigCategory;

public class Config extends ConfigHandler {

    // GENERAL
    public boolean enableComedy;
    public float fuelUsageMultiplier;
    public float powerProductionMultiplier;
    public int ticksPerRedstoneUpdate;

    // REACTOR
    public int maxReactorHeight;
    public int maxReactorSize;
    public float reactorPowerProductionMultiplier;

    // TURBINE
    public int maxTurbineHeight;
    public int maxTurbineSize;
    public float turbineAeroDragMultiplier;
    public float turbineCoilDragMultiplier;
    public double turbineFluidPerBladeMultiplier;
    public float turbineMassDragMultiplier;
    public float turbinePowerProductionMultiplier;

    // COMPATIBILITY
    public boolean autoAddUranium;
    public boolean enableMetallurgyFantasyMetalsInTurbines;

    // RECIPES
    public boolean enableCyaniteFromYelloriumRecipe;
    public boolean enableReactorPowerTapRecipe;
    public boolean registerCreativeMultiblockParts;
    public boolean registerCharcoalForSmelting;
    public boolean registerCoalForSmelting;
    public boolean registerGraphiteCharcoalCraftingRecipes;
    public boolean registerGraphiteCoalCraftingRecipes;
    public boolean registerYelloriteSmeltToUranium;
    public boolean registerYelloriumAsUranium;
    public boolean requireObsidianGlass;
    //public boolean requireSteelInsteadOfIron;

    // WORLDGEN

    public boolean enableWorldGen;
    public boolean enableWorldRegeneration;
    public int[] dimensionWhitelist;
    public int userWorldGenVersion;
    public boolean yelloriteOreEnableWorldGen;
    public int yelloriteOreMaxClustersPerChunk;
    public int yelloriteOrePerCluster;
    public int yelloriteOreMaxY;
    public boolean anglesiteOreEnableWorldGen;
    public int anglesiteOreMaxClustersPerChunk;
    public int anglesiteOrePerCluster;
    public boolean benitoiteOreEnableWorldGen;
    public int benitoiteOreMaxClustersPerChunk;
    public int benitoiteOrePerCluster;

    // not persisted
    public String recipeYelloriumIngotName;
    public String recipeBlutoniumIngotName;

    public Config() {
        super(BigReactors.NAME + ".cfg", BigReactors.NAME);
    }

    @Override
    protected void loadConfigurationCategories() {

        this.GENERAL = this.getCategory("general", "General options");
        this.REACTOR = this.getCategory("reactor", "Define how Reactors works");
        this.TURBINE = this.getCategory("turbine", "Define how Turbines works");
        this.COMPATIBILITY = this.getCategory("compatibility", "Define how Big Reactor interact with other mods");
        this.RECIPES = this.getCategory("recipes", "Recipes options");
        this.WORLDGEN = this.getCategory("worldgen", "Define how ores generates in the world");
    }

    @Override
    protected void loadConfigurationValues() {

        // GENERAL

        this.enableComedy = this.getValue("enableComedy", this.GENERAL, true, "If true, allows weird stuff inside reactors, like MFR sewage and pink slime. Default: true");
        this.fuelUsageMultiplier = this.getValue("fuelUsageMultiplier", this.GENERAL, 1.0f, "A multiplier for balancing fuel consumption. Default: 1.0");
        this.powerProductionMultiplier = this.getValue("powerProductionMultiplier", this.GENERAL, 1.0f, "A multiplier for balancing overall power production from Extreme Reactors. Default: 1.0");
        this.ticksPerRedstoneUpdate = this.getValue("ticksPerRedstoneUpdate", this.GENERAL, 20, "Number of ticks between updates for redstone/rednet ports");

        // REACTOR

        this.maxReactorHeight = this.getValue("maxReactorHeight", this.REACTOR, 48, "The maximum valid size of a reactor in the Y dimension, in blocks. Lower this if your server's players are building ginormous reactors. Bigger Y sizes have far less performance impact than X/Z sizes");
        this.maxReactorSize = this.getValue("maxReactorSize", this.REACTOR, 32, "The maximum valid size of a reactor in the X/Z plane, in blocks. Lower this if your server's players are building ginormous reactors");
        this.reactorPowerProductionMultiplier = this.getValue("reactorPowerProductionMultiplier", this.REACTOR, 1.0f, "A multiplier for balancing reactor power production. Stacks with powerProductionMultiplier. Default: 1.0");

        // TURBINE

        this.maxTurbineHeight = this.getValue("maxTurbineHeight", this.TURBINE, 32, "The maximum valid height of a turbine (Y axis), in blocks. Default: 32");
        this.maxTurbineSize = this.getValue("maxTurbineSize", this.TURBINE, 16, "The maximum valid size of a turbine in the X/Z plane, in blocks. Lower this for smaller turbines, which means lower max output. Default: 16");
        this.turbineAeroDragMultiplier = this.getValue("turbineAeroDragMultiplier", this.TURBINE, 1.0f, "A multiplier for balancing rotor sizes. Multiplies the amount of energy lost to aerodynamic drag per tick. Default: 1.0");
        this.turbineCoilDragMultiplier = this.getValue("turbineCoilDragMultiplier", this.TURBINE, 1.0f, "A multiplier for balancing coil size. Multiplies the amount of energy drawn per coil block per tick. Default: 1.0");
        this.turbineFluidPerBladeMultiplier = this.getValue("turbineFluidPerBladeMultiplier", this.TURBINE, 1.0, "A multiplier for balancing coil size. Multiplies the amount of fluid each blade block can process (base of 25 will be multiplied, then rounded down to the nearest integer). Default: 1.0");
        this.turbineMassDragMultiplier = this.getValue("turbineMassDragMultiplier", this.TURBINE, 1.0f, "A multiplier for balancing rotor sizes. Multiplies the amount of energy lost to friction per tick. Default: 1.0");
        this.turbinePowerProductionMultiplier = this.getValue("turbinePowerProductionMultiplier", this.TURBINE, 1.0f, "A multiplier for balancing turbine power production. Stacks with powerProductionMultiplier. Default: 1.0");

        // COMPATIBILITY

        this.autoAddUranium = this.getValue("autoAddUranium", this.COMPATIBILITY, true, "If true, automatically adds all unregistered uranium ingots found as clonesof standard yellorium fuel");
        this.enableMetallurgyFantasyMetalsInTurbines = this.getValue("enableMetallurgyFantasyMetalsInTurbines", this.COMPATIBILITY, true, "If true, allows Metallurgy's fantasy metals to be used as part of turbine coils. Default: true");

        // RECIPES

        this.enableReactorPowerTapRecipe = this.getValue("enableReactorPowerTapRecipe", this.RECIPES, true, "If set, reactor power taps can be crafted, allowing players to use passive-cooled reactors");
        this.registerCreativeMultiblockParts = this.getValue("registerCreativeMultiblockParts", this.RECIPES, true, "Enable creative mod-only multiblock parts. Default: true");
        this.registerCharcoalForSmelting = this.getValue("registerCharcoalForSmelting", this.RECIPES, true, "If set, charcoal will be smeltable into graphite bars. Disable this if other mods need to smelt charcoal into their own products. (Default: true)");
        this.registerCoalForSmelting = this.getValue("registerCoalForSmelting", this.RECIPES, true, "If set, coal will be smeltable into graphite bars. Disable this if other mods need to smelt coal into their own products. (Default: true)");
        this.registerGraphiteCharcoalCraftingRecipes = this.getValue("registerGraphiteCharcoalCraftingRecipes", this.RECIPES, false, "If set, graphite bars can be crafted from 2 gravel, 1 charcoal. Use this if other mods interfere with the smelting recipe. (Default: false)");
        this.registerGraphiteCoalCraftingRecipes = this.getValue("registerGraphiteCoalCraftingRecipes", this.RECIPES, false, "If set, graphite bars can be crafted from 2 gravel, 1 coal. Use this if other mods interfere with the smelting recipe. (Default: false)");
        this.registerYelloriteSmeltToUranium = this.getValue("registerYelloriteSmeltToUranium", this.RECIPES, true, "If set, yellorite ore will smelt into whichever item is registered as ingotUranium in the ore dictionary. If false, it will smelt into ingotYellorium. (Default: true)");
        this.registerYelloriumAsUranium = this.getValue("registerYelloriumAsUranium", this.RECIPES, true, "If set, then all Extreme Reactors components will require uranium ingots (ingotUranium) in place of yellorium ingots and plutonium ingots (ingotPlutonium) in place of blutonium ingots. Will be ignored if no other mod registers uranium ingots and/ore plutonium ingots. Default: true");
        this.enableCyaniteFromYelloriumRecipe = this.getValue("enableCyaniteFromYelloriumRecipe", this.RECIPES, true, "If set, cyanite will be craftable from yellorium ingots and sand");
        this.requireObsidianGlass = this.getValue("requireObsidianGlass", this.RECIPES, false, "If set, then Extreme Reactors will require hardened or reinforced glass (blockGlassHardened or glassReinforced) instead of plain glass. Will be ignored if no other mod registers those glass types. (default: false)");
        //this.requireSteelInsteadOfIron = this.getValue("requireSteelInsteadOfIron", this.RECIPES, false, "If set, then all Extreme Reactors components will require steel ingots (ingotSteel) in place of iron ingots. Will be ignored if no other mod registers steel ingots. (default: false)");

        // WORLDGEN

        this.enableWorldGen = this.getValue("enableWorldGen", this.WORLDGEN, true, "If false, disables all world gen from Extreme Reactors; all other worldgen settings are automatically overridden");
        this.enableWorldRegeneration = this.getValue("enableWorldRegeneration", this.WORLDGEN, false, "Run BR World Generation in chunks that have already been generated, but have not been modified by Extreme Reactors before. This is largely useful for worlds that existed before BigReactors was released");
        this.userWorldGenVersion = this.getValue("userWorldGenVersion", this.WORLDGEN, 0, "User-set world generation version. Increase this by 1 if you want Extreme Reactors to re-run world generation in your world");
        this.dimensionWhitelist = this.getValue("dimensionWhitelist", this.WORLDGEN, new int[] {0}, "World gen will be performed only in the dimensions listed here");
        this.yelloriteOreEnableWorldGen = this.getValue("yelloriteOreEnableWorldGen", this.WORLDGEN, true, "Enable generation of yellorite ore");
        this.yelloriteOreMaxClustersPerChunk = this.getValue("yelloriteOreMaxClustersPerChunk", this.WORLDGEN, 2, "Maximum number of yellorite clusters per chunk");
        this.yelloriteOrePerCluster = this.getValue("yelloriteOrePerCluster", this.WORLDGEN, 5, "Maximum number of yellorite ore to generate in each cluster");
        this.yelloriteOreMaxY = this.getValue("yelloriteOreMaxY", this.WORLDGEN, 32, "Maximum height (Y coordinate) in the world to generate yellorite ore");
        this.anglesiteOreEnableWorldGen = this.getValue("anglesiteOreEnableWorldGen", this.WORLDGEN, true, "Enable generation of Anglesite ore");
        this.anglesiteOreMaxClustersPerChunk = this.getValue("anglesiteOreMaxClustersPerChunk", this.WORLDGEN, 1, "Maximum number of Anglesite clusters per chunk");
        this.anglesiteOrePerCluster = this.getValue("anglesiteOrePerCluster", this.WORLDGEN, 4, "Maximum number of Anglesite ore to generate in each cluster");
        this.benitoiteOreEnableWorldGen = this.getValue("benitoiteOreEnableWorldGen", this.WORLDGEN, true, "Enable generation of Benitoite ore");
        this.benitoiteOreMaxClustersPerChunk = this.getValue("benitoiteOreMaxClustersPerChunk", this.WORLDGEN, 2, "Maximum number of Benitoite clusters per chunk");
        this.benitoiteOrePerCluster = this.getValue("benitoiteOrePerCluster", this.WORLDGEN, 5, "Maximum number of Benitoite ore to generate in each cluster");

        // not persisted...

        this.recipeYelloriumIngotName = this.registerYelloriumAsUranium ? "ingotUranium" : "ingotYellorium";
        this.recipeBlutoniumIngotName = this.registerYelloriumAsUranium ? "ingotPlutonium" : "ingotBlutonium";
    }

    @Override
    public void onConfigChanged() {

        // update world-gen configs
        if (this.enableWorldGen) {

            if (this.yelloriteOreEnableWorldGen) {

                BigReactors.WORLDGEN_ORES.clearOres();
                BigReactors.WORLDGEN_ORES.addOre(BrBlocks.brOre.getStateFromType(OreType.Yellorite),
                        Blocks.STONE.getDefaultState(), 11, this.yelloriteOreMaxY,
                        this.yelloriteOrePerCluster, this.yelloriteOreMaxClustersPerChunk);

                BigReactors.WHITELIST_WORLDGEN_ORES.clearWhiteList();
                BigReactors.WHITELIST_WORLDGEN_ORES.whiteListDimensions(this.dimensionWhitelist);
            }

            if (this.benitoiteOreEnableWorldGen) {

                final IBlockState ore = BrBlocks.brOre.getStateFromType(OreType.Benitoite);
                final IBlockState netherrack = Blocks.NETHERRACK.getDefaultState();

                BigReactors.NETHER_ORES.clearOres();
                BigReactors.NETHER_ORES.addOre(ore, netherrack , 2, 21, this.benitoiteOrePerCluster, this.benitoiteOreMaxClustersPerChunk);
                BigReactors.NETHER_ORES.addOre(ore, netherrack, 104, 123, this.benitoiteOrePerCluster, this.benitoiteOreMaxClustersPerChunk);
            }

            if (this.anglesiteOreEnableWorldGen) {

                BigReactors.END_ORES.clearOres();
                BigReactors.END_ORES.addOre(BrBlocks.brOre.getStateFromType(OreType.Anglesite),
                        Blocks.END_STONE.getDefaultState(), 10, 90, this.anglesiteOrePerCluster, this.anglesiteOreMaxClustersPerChunk);
            }
        }
    }

    protected ConfigCategory COMPATIBILITY;
    protected ConfigCategory GENERAL;
    protected ConfigCategory REACTOR;
    protected ConfigCategory TURBINE;
    protected ConfigCategory RECIPES;
    protected ConfigCategory WORLDGEN;
}
