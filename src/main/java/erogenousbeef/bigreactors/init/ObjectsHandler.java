package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.block.BlockBRGenericFluid;
import erogenousbeef.bigreactors.common.block.BlockBRMetal;
import erogenousbeef.bigreactors.common.block.BlockBROre;
import erogenousbeef.bigreactors.common.item.ItemBRMetal;
import erogenousbeef.bigreactors.common.item.ItemMineral;
import erogenousbeef.bigreactors.common.item.ItemTieredComponent;
import erogenousbeef.bigreactors.common.item.ItemWrench;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import erogenousbeef.bigreactors.common.multiblock.block.*;
import erogenousbeef.bigreactors.common.multiblock.tileentity.*;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityReactorCreativeCoolantPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityTurbineCreativeSteamGenerator;
import erogenousbeef.bigreactors.init.flattening.BlockReplacer;
import erogenousbeef.bigreactors.init.flattening.ItemReplacer;
import it.zerono.mods.zerocore.lib.config.ConfigHandler;
import it.zerono.mods.zerocore.lib.init.GameObjectsHandler;
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
        //registry.register(new ItemBRMetal("ingotmetals", MetalSize.Ingot));
        registry.register(new ItemBRMetal("ingotyellorium", "ingotYellorium"));
        registry.register(new ItemBRMetal("ingotcyanite", "ingotCyanite"));
        registry.register(new ItemBRMetal("ingotgraphite", "ingotGraphite"));
        registry.register(new ItemBRMetal("ingotblutonium", "ingotBlutonium"));
        registry.register(new ItemBRMetal("ingotludicrite", "ingotLudicrite"));
        registry.register(new ItemBRMetal("ingotsteel", "ingotSteel"));

        // Dusts
        //registry.register(new ItemBRMetal("dustmetals", MetalSize.Dust));
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
        registry.register(new ItemTieredComponent("reactorcasingcores"));

        // Turbine components
        registry.register(new ItemTieredComponent("turbinehousingcores"));

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

    /**
     * Register all the recipes for the blocks and items of this mod
     * Override in your subclass to register your recipes with the provided registry
     *
     * @param registry the recipes registry
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {

        // # Yellorite ore -> Yellorium ingot

        ItemStack result;

        if (BigReactors.CONFIG.registerYelloriteSmeltToUranium) {

            result = OreDictionaryHelper.getOre("ingotUranium");

            if (null == result) {

                BigReactors.getLogger().warn("Config value registerYelloriteSmeltToUranium is set to True, but there are no ores registered as ingotUranium in the ore dictionary! Falling back to using standard yellorium only.");
                result = OreDictionaryHelper.getOre("ingotYellorium");
            }

        } else {

            result = OreDictionaryHelper.getOre("ingotYellorium");
        }

        RecipeHelper2.addSmelting(BrBlocks.oreYellorite.createItemStack(), result, 0.5f);

        // Dusts -> ingots

        RecipeHelper2.addSmelting(BrItems.dustYellorium.createItemStack(), BrItems.ingotYellorium.createItemStack(), 0.0f);
        RecipeHelper2.addSmelting(BrItems.dustCyanite.createItemStack(), BrItems.ingotCyanite.createItemStack(), 0.0f);
        RecipeHelper2.addSmelting(BrItems.dustGraphite.createItemStack(), BrItems.ingotGraphite.createItemStack(), 0.0f);
        RecipeHelper2.addSmelting(BrItems.dustBlutonium.createItemStack(), BrItems.ingotBlutonium.createItemStack(), 0.0f);
        RecipeHelper2.addSmelting(BrItems.dustLudicrite.createItemStack(), BrItems.ingotLudicrite.createItemStack(), 0.0f);
        RecipeHelper2.addSmelting(BrItems.dustSteel.createItemStack(), BrItems.ingotSteel.createItemStack(), 0.0f);

        // Graphite

        result = OreDictionaryHelper.getOre("ingotGraphite");

        // # Coal -> Graphite
        if (BigReactors.CONFIG.registerCoalForSmelting) {
            RecipeHelper2.addSmelting(new ItemStack(Items.COAL, 1, 0), result, 1);
        }

        // # Charcoal -> Graphite
        if (BigReactors.CONFIG.registerCharcoalForSmelting) {
            RecipeHelper2.addSmelting(new ItemStack(Items.COAL, 1, 1), result, 1);
        }

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
