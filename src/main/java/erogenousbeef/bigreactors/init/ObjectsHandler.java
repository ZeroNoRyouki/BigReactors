package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.block.BlockBRGenericFluid;
import erogenousbeef.bigreactors.common.block.BlockBRMetal;
import erogenousbeef.bigreactors.common.block.BlockBROre;
import erogenousbeef.bigreactors.common.config.Config;
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
import it.zerono.mods.zerocore.lib.MetalSize;
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
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import zero.temp.RecipeHelper2;

import javax.annotation.Nonnull;

public class ObjectsHandler extends GameObjectsHandler {

    public ObjectsHandler(ConfigHandler... configs) {
        super(configs);
    }

    @Override
    protected void onRegisterBlocks(@Nonnull IForgeRegistry<Block> registry) {

        final boolean regCreativeParts = BigReactors.CONFIG.registerCreativeMultiblockParts;

        // make sure ore fluids are registered as well..
        BrFluids.initialize();

        // Ores
        registry.register(new BlockBROre("brore"));

        // Metal blocks
        registry.register(new BlockBRMetal("blockmetals"));

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
                RecipeHelper2.addShaped(registry, BrItems.ingotMetals.createItemStack(MetalType.Blutonium, 1),
                        "CCC", "C C", "CCC",
                        'C', ingotCyanite);
            }
        });

        // Dusts
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

        // Minerals
        registry.register(new ItemMineral("minerals"));

        // Reactor components
        registry.register(new ItemTieredComponent("reactorcasingcores") {

            @Override
            public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {

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
            }
        });

        // Turbine components
        registry.register(new ItemTieredComponent("turbinehousingcores") {

            @Override
            public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {

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
            }
        });

        // Miscellanea
        registry.register(new ItemWrench("wrench"));
    }

    @Override
    protected void onRegisterOreDictionaryEntries() {

        // Patch up vanilla being stupid - most mods already do this, so it's usually a no-op

        if (!OreDictionaryHelper.doesOreNameExist("blockSnow"))
            OreDictionary.registerOre("blockSnow", new ItemStack(Blocks.SNOW, 1));

        if (!OreDictionaryHelper.doesOreNameExist("blockIce"))
            OreDictionary.registerOre("blockIce", new ItemStack(Blocks.ICE, 1));
    }
}
