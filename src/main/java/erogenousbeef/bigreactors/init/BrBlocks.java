package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BRConfig;
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.CommonProxy;
import erogenousbeef.bigreactors.common.block.*;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import erogenousbeef.bigreactors.common.multiblock.block.*;
import erogenousbeef.bigreactors.common.multiblock.tileentity.*;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityReactorCreativeCoolantPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityTurbineCreativeSteamGenerator;
import erogenousbeef.bigreactors.common.tileentity.TileEntityCyaniteReprocessor;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;

public final class BrBlocks {

    // Ores
    public static final BlockBROre brOre;

    // Metal blocks
    public static final BlockBRMetal blockMetals;

    // Reactor parts
    public static final BlockMultiblockGlass reactorGlass;
    public static final BlockMultiblockCasing reactorCasing;
    public static final BlockReactorController reactorController;
    public static final BlockReactorPowerTap reactorPowerTapRF;
    public static final BlockReactorPowerTap reactorPowerTapTesla;
    public static final BlockReactorIOPort reactorAccessPort;
    public static final BlockReactorIOPort reactorCoolantPort;
    public static final BlockReactorControlRod reactorControlRod;
    public static final BlockReactorRedNetPort reactorRedNetPort;
    public static final BlockReactorComputerPort reactorComputerPort;
    public static final BlockReactorRedstonePort reactorRedstonePort;
    public static final BlockReactorFuelRod reactorFuelRod;
    public static final BlockReactorIOPort reactorCreativeCoolantPort;

    // Turbine parts
    public static final BlockMultiblockGlass turbineGlass;
    public static final BlockMultiblockCasing turbineHousing;
    public static final BlockTurbinePart turbineController;
    public static final BlockTurbinePart turbinePowerTap;
    public static final BlockTurbinePart turbineFluidPort;
    public static final BlockTurbinePart turbineBearing;
    public static final BlockTurbinePart turbineComputerPort;
    public static final BlockMBCreativePart turbineCreativeSteamGenerator;
    
    // Devices
    public static final BlockBRDevice deviceCyaniteRep;

    // Fluid blocks
    public static final BlockBRGenericFluid yellorium;
    public static final BlockBRGenericFluid cyanite;


    public static void initialize() {
    }

    static {

        CommonProxy proxy = BRLoader.proxy;
        boolean regCreativeParts;

        BRConfig.CONFIGURATION.load();
        regCreativeParts = BRConfig.CONFIGURATION.get("General", "registerCreativeMultiblockParts", true, "If true, creative parts for reactors, turbines and other multiblocks will be registered.").getBoolean(true);
        BRConfig.CONFIGURATION.save();

        // register blocks

        // - ores
        brOre = (BlockBROre)proxy.register(new BlockBROre("brOre"));

        // - metal blocks
        blockMetals = (BlockBRMetal)proxy.register(new BlockBRMetal("blockMetals"));
        
        // - reactor parts
        reactorGlass = (BlockMultiblockGlass)proxy.register(new BlockMultiblockGlass(PartType.ReactorGlass, "reactorGlass"));
        reactorCasing = (BlockMultiblockCasing)proxy.register(new BlockMultiblockCasing(PartType.ReactorCasing, "reactorCasing"));
        reactorController = (BlockReactorController)proxy.register(new BlockReactorController("reactorController"));
        reactorPowerTapRF = (BlockReactorPowerTap)proxy.register(new BlockReactorPowerTap("reactorPowerTapRF", PowerSystem.RedstoneFlux));
        reactorPowerTapTesla = (BlockReactorPowerTap)proxy.register(new BlockReactorPowerTap("reactorPowerTapTesla", PowerSystem.Tesla));
        reactorAccessPort = (BlockReactorIOPort)proxy.register(new BlockReactorIOPort(PartType.ReactorAccessPort, "reactorAccessPort"));
        reactorCoolantPort = (BlockReactorIOPort)proxy.register(new BlockReactorIOPort(PartType.ReactorCoolantPort, "reactorCoolantPort"));
        reactorControlRod = (BlockReactorControlRod)proxy.register(new BlockReactorControlRod("reactorControlRod"));
        reactorRedNetPort = (BlockReactorRedNetPort)proxy.register(new BlockReactorRedNetPort("reactorRedNetPort"));
        reactorComputerPort = (BlockReactorComputerPort)proxy.register(new BlockReactorComputerPort("reactorComputerPort"));
        reactorRedstonePort = (BlockReactorRedstonePort)proxy.register(new BlockReactorRedstonePort("reactorRedstonePort"));
        reactorFuelRod = (BlockReactorFuelRod)proxy.register(new BlockReactorFuelRod("reactorFuelRod"));
        reactorCreativeCoolantPort = !regCreativeParts ? null : (BlockReactorIOPort)proxy.register(new BlockReactorIOPort(PartType.ReactorCreativeCoolantPort, "reactorCreativeCoolantPort"));

        // - turbine parts
        turbineGlass = (BlockMultiblockGlass)proxy.register(new BlockMultiblockGlass(PartType.TurbineGlass, "turbineGlass"));
        turbineHousing = (BlockMultiblockCasing)proxy.register(new BlockMultiblockCasing(PartType.TurbineHousing, "turbineHousing"));

        turbineController = (BlockTurbinePart)proxy.register(new BlockTurbinePart(PartType.TurbineController, "turbineController"));
        turbinePowerTap = (BlockTurbinePart)proxy.register(new BlockTurbinePart(PartType.TurbinePowerPort, "turbinePowerTap"));
        turbineFluidPort = (BlockTurbinePart)proxy.register(new BlockTurbinePart(PartType.TurbineFluidPort, "turbineFluidPort"));
        turbineBearing = (BlockTurbinePart)proxy.register(new BlockTurbinePart(PartType.TurbineRotorBearing, "turbineBearing"));
        turbineComputerPort = (BlockTurbinePart)proxy.register(new BlockTurbinePart(PartType.TurbineComputerPort, "turbineComputerPort"));
        turbineCreativeSteamGenerator = !regCreativeParts ? null : (BlockMBCreativePart)proxy.register(new BlockMBCreativePart(PartType.TurbineCreativeSteamGenerator, "turbineCreativeSteamGenerator"));

        // - devices
        deviceCyaniteRep = (BlockBRDevice)proxy.register(new BlockBRDevice(DeviceType.CyaniteReprocessor, "deviceCyaniteRep"));

        // - fluid blocks

        yellorium = proxy.register(new BlockBRGenericFluid(BrFluids.fluidYellorium, "yellorium", new MaterialLiquid(MapColor.YELLOW)));
        cyanite = proxy.register(new BlockBRGenericFluid(BrFluids.fluidCyanite, "cyanite", Material.LAVA));

        // - register block tile entities
        proxy.register(TileEntityCyaniteReprocessor.class);
        proxy.register(TileEntityReactorPart.class);
        proxy.register(TileEntityReactorGlass.class);
        proxy.register(TileEntityController.class);
        proxy.register(TileEntityReactorPowerTapRedstoneFlux.class);
        proxy.register(TileEntityReactorPowerTapTesla.class);
        proxy.register(TileEntityReactorAccessPort.class);
        proxy.register(TileEntityReactorFuelRod.class);
        proxy.register(TileEntityReactorControlRod.class);
        proxy.register(TileEntityReactorRedstonePort.class);
        //proxy.register(TileEntityReactorRedNetPort.class);
        proxy.register(TileEntityReactorComputerPort.class);
        proxy.register(TileEntityReactorCoolantPort.class);
        proxy.register(TileEntityReactorCreativeCoolantPort.class);
        proxy.register(TileEntityTurbinePartStandard.class);
        proxy.register(TileEntityTurbinePowerTap.class);
        proxy.register(TileEntityTurbineFluidPort.class);
        proxy.register(TileEntityTurbinePartGlass.class);
        proxy.register(TileEntityTurbineRotorBearing.class);
        proxy.register(TileEntityTurbineRotorPart.class);
        proxy.register(TileEntityTurbineCreativeSteamGenerator.class);
        //proxy.register(TileEntityTurbineComputerPort.class);
    }
}
