package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.block.BlockBRGenericFluid;
import erogenousbeef.bigreactors.common.block.BlockBRMetal;
import erogenousbeef.bigreactors.common.block.BlockBROre;
import erogenousbeef.bigreactors.common.item.ItemBRMetal;
import erogenousbeef.bigreactors.common.item.ItemMineral;
import erogenousbeef.bigreactors.common.item.ItemTieredComponent;
import erogenousbeef.bigreactors.common.item.ItemWrench;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import erogenousbeef.bigreactors.common.multiblock.block.*;
import erogenousbeef.bigreactors.common.multiblock.tileentity.*;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityReactorCreativeCoolantPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityTurbineCreativeSteamGenerator;
import erogenousbeef.bigreactors.init.flattening.BlockReplacer;
import erogenousbeef.bigreactors.init.flattening.ItemReplacer;
import it.zerono.mods.zerocore.lib.MetalSize;
import it.zerono.mods.zerocore.lib.config.ConfigHandler;
import it.zerono.mods.zerocore.lib.init.GameObjectsHandler;
import it.zerono.mods.zerocore.lib.item.ModItem;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import zero.temp.RecipeHelper2;

import javax.annotation.Nonnull;

public class ObjectsHandler extends GameObjectsHandler {

    public ObjectsHandler(ConfigHandler... configs) {

        super(configs);

        this._blockReplacer = new BlockReplacer(DATA_VERSION);
        this._itemReplacer = new ItemReplacer(DATA_VERSION);
        this.addBlockRemapper(this._blockReplacer);
        this.addItemRemapper(this._itemReplacer);
    }

    @Override
    protected void onRegisterBlocks(@Nonnull IForgeRegistry<Block> registry) {

        final boolean regCreativeParts = BigReactors.CONFIG.registerCreativeMultiblockParts;

        // make sure ore fluids are registered as well..
        BrFluids.initialize();

        // Ores
        registry.register(new BlockBROre("oreyellorite", "oreYellorite"));
        registry.register(new BlockBROre("oreanglesite", "oreAnglesite"));
        registry.register(new BlockBROre("orebenitoite", "oreBenitoite" ));

        // Metal blocks
        //registry.register(new BlockBRMetal("blockmetals"));
        registry.register(new BlockBRMetal("blockyellorium", "blockYellorium"));
        registry.register(new BlockBRMetal("blockcyanite", "blockCyanite"));
        registry.register(new BlockBRMetal("blockgraphite", "blockGraphite"));
        registry.register(new BlockBRMetal("blockblutonium", "blockBlutonium"));
        registry.register(new BlockBRMetal("blockludicrite", "blockLudicrite"));
        registry.register(new BlockBRMetal("blocksteel", "blockSteel"));

        // Reactor parts
        registry.register(new BlockMultiblockCasing(PartType.ReactorCasing, "reactorcasing"));
        registry.register(new BlockMultiblockGlass(PartType.ReactorGlass, "reactorglass"));
        registry.register(new BlockMultiblockController(PartType.ReactorController, "reactorcontroller"));
        registry.register(new BlockMultiblockPowerTap(PartType.ReactorPowerTap, "reactorpowertaprf", PowerSystem.RedstoneFlux));
        registry.register(new BlockMultiblockPowerTap(PartType.ReactorPowerTap, "reactorpowertaptesla", PowerSystem.Tesla));
        registry.register(new BlockMultiblockIOPort(PartType.ReactorAccessPort, "reactoraccessport"));
        registry.register(new BlockMultiblockIOPort(PartType.ReactorCoolantPort, "reactorcoolantport"));
        registry.register(new BlockReactorControlRod("reactorcontrolrod"));
        registry.register(new BlockReactorRedNetPort("reactorrednetport"));
        registry.register(new BlockMultiblockComputerPort(PartType.ReactorComputerPort, "reactorcomputerport"));
        registry.register(new BlockReactorRedstonePort("reactorredstoneport"));
        registry.register(new BlockReactorFuelRod("reactorfuelrod"));

        if (regCreativeParts)
            registry.register(new BlockMultiblockIOPort(PartType.ReactorCreativeCoolantPort, "reactorcreativecoolantport"));

        // Turbine parts
        registry.register(new BlockMultiblockGlass(PartType.TurbineGlass, "turbineglass"));
        registry.register(new BlockMultiblockCasing(PartType.TurbineHousing, "turbinehousing"));
        registry.register(new BlockMultiblockController(PartType.TurbineController, "turbinecontroller"));
        registry.register(new BlockMultiblockPowerTap(PartType.TurbinePowerPort, "turbinepowertaprf", PowerSystem.RedstoneFlux));
        registry.register(new BlockMultiblockPowerTap(PartType.TurbinePowerPort, "turbinepowertaptesla", PowerSystem.Tesla));
        registry.register(new BlockMultiblockComputerPort(PartType.TurbineComputerPort, "turbinecomputerport"));
        registry.register(new BlockMultiblockIOPort(PartType.TurbineFluidPort, "turbinefluidport"));
        registry.register(new BlockTurbineRotorBearing("turbinebearing"));
        registry.register(new BlockTurbineRotorShaft("turbinerotorshaft"));
        registry.register(new BlockTurbineRotorBlade("turbinerotorblade"));

        if (regCreativeParts)
            registry.register(new BlockMultiblockIOPort(PartType.TurbineCreativeSteamGenerator, "turbinecreativesteamgenerator"));

        // - devices
        //deviceCyaniteRep = (BlockBRDevice)init.register(new BlockBRDevice(DeviceType.CyaniteReprocessor, "deviceCyaniteRep"));

        // Fluid blocks
        registry.register(new BlockBRGenericFluid(BrFluids.fluidYellorium, "yellorium", new MaterialLiquid(MapColor.YELLOW)));
        registry.register(new BlockBRGenericFluid(BrFluids.fluidCyanite, "cyanite", Material.LAVA));
    }

    @Override
    protected void onRegisterTileEntities() {

        this.registerTileEntity(BigReactors.MODID, TileEntityReactorPart.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorGlass.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorController.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorPowerTapRedstoneFlux.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorPowerTapTesla.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorAccessPort.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorFuelRod.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorControlRod.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorRedstonePort.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorComputerPort.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorCoolantPort.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorCreativeCoolantPort.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityReactorRedNetPort.class);

        this.registerTileEntity(BigReactors.MODID, TileEntityTurbinePart.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityTurbinePowerTapRedstoneFlux.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityTurbinePowerTapTesla.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityTurbineFluidPort.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityTurbinePartGlass.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityTurbineRotorBearing.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityTurbineRotorShaft.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityTurbineRotorBlade.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityTurbineCreativeSteamGenerator.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityTurbineComputerPort.class);
        this.registerTileEntity(BigReactors.MODID, TileEntityTurbineController.class);
    }

    @Override
    protected void onRegisterItems(@Nonnull IForgeRegistry<Item> registry) {

        // Ingots
        registry.register(new ItemBRMetal("ingotyellorium", "ingotYellorium"));
        registry.register(new ItemBRMetal("ingotcyanite", "ingotCyanite"));
        registry.register(new ItemBRMetal("ingotgraphite", "ingotGraphite"));
        registry.register(new ItemBRMetal("ingotblutonium", "ingotBlutonium"));
        registry.register(new ItemBRMetal("ingotludicrite", "ingotLudicrite"));
        registry.register(new ItemBRMetal("ingotsteel", "ingotSteel"));

        /*TODO fix recipes!!!
        registry.register(new ItemBRMetal("ingotmetals", MetalSize.Ingot) {

            @Override
            public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {

                final Config configs = BigReactors.CONFIG;
                final ItemStack ingotGraphite = OreDictionaryHelper.getOre("ingotGraphite");
                final ItemStack ingotCyanite = OreDictionaryHelper.getOre("ingotCyanite");

                // Graphite & Cyanite

                // -- Coal -> Graphite
                if (configs.registerCoalForSmelting)
                    RecipeHelper2.addSmelting(new ItemStack(Items.COAL, 1, 0), ingotGraphite, 1);

                // -- Charcoal -> Graphite
                if (configs.registerCharcoalForSmelting)
                    RecipeHelper2.addSmelting(new ItemStack(Items.COAL, 1, 1), ingotGraphite, 1);

                // -- Gravel + Coal -> Graphite
                if (configs.registerGraphiteCoalCraftingRecipes) {

                    RecipeHelper2.addShaped(registry, ingotGraphite,
                            "GCG", RecipeHelper2.EMPTY_ROW3, RecipeHelper2.EMPTY_ROW3,
                            'G', Blocks.GRAVEL, 'C', new ItemStack(Items.COAL, 1, 0));
                }

                // -- Gravel + Charcoal -> Graphite
                if (configs.registerGraphiteCharcoalCraftingRecipes) {

                    RecipeHelper2.addShaped(registry, ingotGraphite,
                            "GCG", RecipeHelper2.EMPTY_ROW3, RecipeHelper2.EMPTY_ROW3,
                            'G', Blocks.GRAVEL, 'C', new ItemStack(Items.COAL, 1, 1));
                }

                // -- Yellorium ingot + Sand -> Cyanite
                if (configs.enableCyaniteFromYelloriumRecipe) {
                    RecipeHelper2.addShapeless(registry, ingotCyanite, configs.recipeYelloriumIngotName, Blocks.SAND);
                }

                // TEMPORARY recipe for the blutonium ingot
                RecipeHelper2.addShaped(registry, BrItems.ingotBlutonium.createItemStack(),
                        "CCC", "C C", "CCC",
                        'C', ingotCyanite);
            }
        });
        */

        // Dusts
        /*TODO fix recipes!!!
        registry.register(new ItemBRMetal("dustmetals", MetalSize.Dust) {

            @Override
            public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {

                for (MetalType metal : MetalType.VALUES) {

                    // smelt dust into ingot
                    RecipeHelper2.addSmelting(BrItems.dustMetals.createItemStack(metal, 1),
                            BrItems.ingotMetals.createItemStack(metal, 1), 0.0f);
                }
            }
        });
        */
        registry.register(new ItemBRMetal("dustyellorium", "dustYellorium"));
        registry.register(new ItemBRMetal("dustcyanite", "dustCyanite"));
        registry.register(new ItemBRMetal("dustgraphite", "dustGraphite"));
        registry.register(new ItemBRMetal("dustblutonium", "dustBlutonium"));
        registry.register(new ItemBRMetal("dustludicrite", "dustLudicrite"));
        registry.register(new ItemBRMetal("duststeel", "dustSteel"));

        // Minerals
        //registry.register(new ItemMineral("minerals"));
        registry.register(new ItemMineral("mineralanglesite"));
        registry.register(new ItemMineral("mineralbenitoite"));

        // Reactor components
        registry.register(new ItemTieredComponent("reactorcasingcores") {

            @Override
            public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {
                //TODO fix recipes!
                /*
                if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy)) {
                    RecipeHelper2.addShaped(registry, this.createItemStack(PartTier.Legacy, 1),
                            "IGI", "ARA", "IGI",
                            'I', "ingotIron", 'G', "ingotGraphite", 'A', "ingotGold", 'R', Items.REDSTONE);
                }

                if (PartTier.REACTOR_TIERS.contains(PartTier.Basic)) {
                    RecipeHelper2.addShaped(registry, this.createItemStack(PartTier.Basic, 1),
                            "IGI", "ARA", "IGI",
                            'I', "ingotSteel", 'G', "ingotGraphite", 'A', "ingotGold", 'R', Items.REDSTONE);
                }
                */
            }
        });

        // Turbine components
        registry.register(new ItemTieredComponent("turbinehousingcores") {

            @Override
            public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {
                //TODO fix recipes!
                /*
                if (PartTier.TURBINE_TIERS.contains(PartTier.Legacy)) {
                    RecipeHelper2.addShaped(registry, this.createItemStack(PartTier.Legacy, 1),
                            "IGI", "ARA", "IGI",
                            'I', "ingotIron", 'G', "ingotGraphite", 'A', "ingotGold", 'R', Items.COMPARATOR);
                }

                if (PartTier.TURBINE_TIERS.contains(PartTier.Basic)) {
                    RecipeHelper2.addShaped(registry, this.createItemStack(PartTier.Basic, 1),
                            "IGI", "ARA", "IGI",
                            'I', "ingotSteel", 'G', "ingotGraphite", 'A', "ingotGold", 'R', Items.COMPARATOR);
                }
                */
            }
        });

        // Miscellanea
        registry.register(new ItemWrench("wrench"));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onRegisterOreDictionaryEntries() {

        // oreYellorium : for convenience of mods which fiddle with recipes
        OreDictionary.registerOre("oreYellorium", BrBlocks.oreYellorite.createItemStack());

        // Uranium and Plutonium aliases
        if (BigReactors.CONFIG.registerYelloriumAsUranium) {

            OreDictionary.registerOre("ingotUranium", ((ItemBRMetal)this.getTrackedItem("ingotyellorium")).createItemStack());
            OreDictionary.registerOre("dustUranium", ((ItemBRMetal)this.getTrackedItem("dustyellorium")).createItemStack());

            OreDictionary.registerOre("ingotPlutonium", ((ItemBRMetal)this.getTrackedItem("ingotblutonium")).createItemStack());
            OreDictionary.registerOre("dustPlutonium", ((ItemBRMetal)this.getTrackedItem("dustblutonium")).createItemStack());
        }

        // Patch up vanilla being stupid - most mods already do this, so it's usually a no-op

        if (!OreDictionaryHelper.doesOreNameExist("blockSnow"))
            OreDictionary.registerOre("blockSnow", new ItemStack(Blocks.SNOW, 1));

        if (!OreDictionaryHelper.doesOreNameExist("blockIce"))
            OreDictionary.registerOre("blockIce", new ItemStack(Blocks.ICE, 1));
    }

    @Override
    public void onInit(FMLInitializationEvent event) {

        super.onInit(event);

        final ModFixs fixs = FMLCommonHandler.instance().getDataFixer().init(BigReactors.MODID, DATA_VERSION);

        fixs.registerFix(FixTypes.CHUNK, this._blockReplacer);
        fixs.registerFix(FixTypes.ITEM_INSTANCE, this._itemReplacer);
        this.registerMissingBlocksReplacements();
        this.registerMissingItemsReplacements();
    }

    @SuppressWarnings("ConstantConditions")
    private void registerMissingBlocksReplacements() {

        ResourceLocation missingId;

        missingId = BigReactors.createResourceLocation("brore");
        this._blockReplacer.addReplacement(missingId, 0, BrBlocks.oreYellorite);
        this._blockReplacer.addReplacement(missingId, 1, BrBlocks.oreAnglesite);
        this._blockReplacer.addReplacement(missingId, 2, BrBlocks.oreBenitoite);

        missingId = BigReactors.createResourceLocation("blockmetals");
        this._blockReplacer.addReplacement(missingId, 0, BrBlocks.blockYellorium);
        this._blockReplacer.addReplacement(missingId, 1, BrBlocks.blockCyanite);
        this._blockReplacer.addReplacement(missingId, 2, BrBlocks.blockGraphite);
        this._blockReplacer.addReplacement(missingId, 3, BrBlocks.blockBlutonium);
        this._blockReplacer.addReplacement(missingId, 4, BrBlocks.blockLudicrite);
        this._blockReplacer.addReplacement(missingId, 5, BrBlocks.blockSteel);
    }

    @SuppressWarnings("ConstantConditions")
    private void registerMissingItemsReplacements() {

        ResourceLocation missingId;

        missingId = BigReactors.createResourceLocation("brore");
        this._itemReplacer.addReplacement(missingId, 0, Item.getItemFromBlock(BrBlocks.oreYellorite));
        this._itemReplacer.addReplacement(missingId, 1, Item.getItemFromBlock(BrBlocks.oreAnglesite));
        this._itemReplacer.addReplacement(missingId, 2, Item.getItemFromBlock(BrBlocks.oreBenitoite));

        missingId = BigReactors.createResourceLocation("minerals");
        this._itemReplacer.addReplacement(missingId, 0, BrItems.mineralAnglesite);
        this._itemReplacer.addReplacement(missingId, 1, BrItems.mineralBenitoite);

        missingId = BigReactors.createResourceLocation("blockmetals");
        this._itemReplacer.addReplacement(missingId, 0, Item.getItemFromBlock(BrBlocks.blockYellorium));
        this._itemReplacer.addReplacement(missingId, 1, Item.getItemFromBlock(BrBlocks.blockCyanite));
        this._itemReplacer.addReplacement(missingId, 2, Item.getItemFromBlock(BrBlocks.blockGraphite));
        this._itemReplacer.addReplacement(missingId, 3, Item.getItemFromBlock(BrBlocks.blockBlutonium));
        this._itemReplacer.addReplacement(missingId, 4, Item.getItemFromBlock(BrBlocks.blockLudicrite));
        this._itemReplacer.addReplacement(missingId, 5, Item.getItemFromBlock(BrBlocks.blockSteel));

        missingId = BigReactors.createResourceLocation("ingotmetals");
        this._itemReplacer.addReplacement(missingId, 0, BrItems.ingotYellorium);
        this._itemReplacer.addReplacement(missingId, 1, BrItems.ingotCyanite);
        this._itemReplacer.addReplacement(missingId, 2, BrItems.ingotGraphite);
        this._itemReplacer.addReplacement(missingId, 3, BrItems.ingotBlutonium);
        this._itemReplacer.addReplacement(missingId, 4, BrItems.ingotLudicrite);
        this._itemReplacer.addReplacement(missingId, 5, BrItems.ingotSteel);

        missingId = BigReactors.createResourceLocation("dustmetals");
        this._itemReplacer.addReplacement(missingId, 0, BrItems.dustYellorium);
        this._itemReplacer.addReplacement(missingId, 1, BrItems.dustCyanite);
        this._itemReplacer.addReplacement(missingId, 2, BrItems.dustGraphite);
        this._itemReplacer.addReplacement(missingId, 3, BrItems.dustBlutonium);
        this._itemReplacer.addReplacement(missingId, 4, BrItems.dustLudicrite);
        this._itemReplacer.addReplacement(missingId, 5, BrItems.dustSteel);
    }

    private static final int DATA_VERSION = 1;

    private final BlockReplacer _blockReplacer;
    private final ItemReplacer _itemReplacer;
}
