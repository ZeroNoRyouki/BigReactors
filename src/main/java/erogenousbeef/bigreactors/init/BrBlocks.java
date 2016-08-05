package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
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

        final InitHandler init = InitHandler.INSTANCE;
        final boolean regCreativeParts = BigReactors.CONFIG.registerCreativeMultiblockParts;

        // register blocks

        // - ores
        brOre = (BlockBROre)init.register(new BlockBROre("brOre"));

        // - metal blocks
        blockMetals = (BlockBRMetal)init.register(new BlockBRMetal("blockMetals"));
        
        // - reactor parts
        reactorCasing = (BlockMultiblockCasing)init.register(new BlockMultiblockCasing(PartType.ReactorCasing, "reactorCasing"));
        reactorGlass = (BlockMultiblockGlass)init.register(new BlockMultiblockGlass(PartType.ReactorGlass, "reactorGlass"));
        reactorController = (BlockReactorController)init.register(new BlockReactorController("reactorController"));
        reactorPowerTapRF = (BlockReactorPowerTap)init.register(new BlockReactorPowerTap("reactorPowerTapRF", PowerSystem.RedstoneFlux));
        reactorPowerTapTesla = (BlockReactorPowerTap)init.register(new BlockReactorPowerTap("reactorPowerTapTesla", PowerSystem.Tesla));
        reactorAccessPort = (BlockReactorIOPort)init.register(new BlockReactorIOPort(PartType.ReactorAccessPort, "reactorAccessPort"));
        reactorCoolantPort = (BlockReactorIOPort)init.register(new BlockReactorIOPort(PartType.ReactorCoolantPort, "reactorCoolantPort"));
        reactorControlRod = (BlockReactorControlRod)init.register(new BlockReactorControlRod("reactorControlRod"));
        reactorRedNetPort = (BlockReactorRedNetPort)init.register(new BlockReactorRedNetPort("reactorRedNetPort"));
        reactorComputerPort = (BlockReactorComputerPort)init.register(new BlockReactorComputerPort("reactorComputerPort"));
        reactorRedstonePort = (BlockReactorRedstonePort)init.register(new BlockReactorRedstonePort("reactorRedstonePort"));
        reactorFuelRod = (BlockReactorFuelRod)init.register(new BlockReactorFuelRod("reactorFuelRod"));
        reactorCreativeCoolantPort = !regCreativeParts ? null : (BlockReactorIOPort)init.register(new BlockReactorIOPort(PartType.ReactorCreativeCoolantPort, "reactorCreativeCoolantPort"));

        // - turbine parts
        turbineGlass = (BlockMultiblockGlass)init.register(new BlockMultiblockGlass(PartType.TurbineGlass, "turbineGlass"));
        turbineHousing = (BlockMultiblockCasing)init.register(new BlockMultiblockCasing(PartType.TurbineHousing, "turbineHousing"));

        turbineController = (BlockTurbinePart)init.register(new BlockTurbinePart(PartType.TurbineController, "turbineController"));
        turbinePowerTap = (BlockTurbinePart)init.register(new BlockTurbinePart(PartType.TurbinePowerPort, "turbinePowerTap"));
        turbineFluidPort = (BlockTurbinePart)init.register(new BlockTurbinePart(PartType.TurbineFluidPort, "turbineFluidPort"));
        turbineBearing = (BlockTurbinePart)init.register(new BlockTurbinePart(PartType.TurbineRotorBearing, "turbineBearing"));
        turbineComputerPort = (BlockTurbinePart)init.register(new BlockTurbinePart(PartType.TurbineComputerPort, "turbineComputerPort"));
        turbineCreativeSteamGenerator = !regCreativeParts ? null : (BlockMBCreativePart)init.register(new BlockMBCreativePart(PartType.TurbineCreativeSteamGenerator, "turbineCreativeSteamGenerator"));

        // - devices
        deviceCyaniteRep = (BlockBRDevice)init.register(new BlockBRDevice(DeviceType.CyaniteReprocessor, "deviceCyaniteRep"));

        // - fluid blocks

        yellorium = init.register(new BlockBRGenericFluid(BrFluids.fluidYellorium, "yellorium", new MaterialLiquid(MapColor.YELLOW)));
        cyanite = init.register(new BlockBRGenericFluid(BrFluids.fluidCyanite, "cyanite", Material.LAVA));

        // - register block tile entities
        init.register(TileEntityCyaniteReprocessor.class);
        init.register(TileEntityReactorPart.class);
        init.register(TileEntityReactorGlass.class);
        init.register(TileEntityReactorController.class);
        init.register(TileEntityReactorPowerTapRedstoneFlux.class);
        init.register(TileEntityReactorPowerTapTesla.class);
        init.register(TileEntityReactorAccessPort.class);
        init.register(TileEntityReactorFuelRod.class);
        init.register(TileEntityReactorControlRod.class);
        init.register(TileEntityReactorRedstonePort.class);
        //init.register(TileEntityReactorRedNetPort.class);
        init.register(TileEntityReactorComputerPort.class);
        init.register(TileEntityReactorCoolantPort.class);
        init.register(TileEntityReactorCreativeCoolantPort.class);
        init.register(TileEntityTurbinePartStandard.class);
        init.register(TileEntityTurbinePowerTap.class);
        init.register(TileEntityTurbineFluidPort.class);
        init.register(TileEntityTurbinePartGlass.class);
        init.register(TileEntityTurbineRotorBearing.class);
        init.register(TileEntityTurbineRotorPart.class);
        init.register(TileEntityTurbineCreativeSteamGenerator.class);
        //init.register(TileEntityTurbineComputerPort.class);
    }
}
