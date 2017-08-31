package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.block.BlockBRGenericFluid;
import erogenousbeef.bigreactors.common.block.BlockBRMetal;
import erogenousbeef.bigreactors.common.block.BlockBROre;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import erogenousbeef.bigreactors.common.multiblock.block.*;
import erogenousbeef.bigreactors.common.multiblock.tileentity.*;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityReactorCreativeCoolantPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityTurbineCreativeSteamGenerator;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(BigReactors.MODID)
public final class BrBlocks {

    // Ores
    public static final BlockBROre brOre = null;

    // Metal blocks
    public static final BlockBRMetal blockMetals = null;

    // Reactor parts
    public static final BlockMultiblockGlass reactorGlass = null;
    public static final BlockMultiblockCasing reactorCasing = null;
    public static final BlockMultiblockController reactorController = null;
    public static final BlockMultiblockPowerTap reactorPowerTapRF = null;
    public static final BlockMultiblockPowerTap reactorPowerTapTesla = null;
    public static final BlockMultiblockIOPort reactorAccessPort = null;
    public static final BlockMultiblockIOPort reactorCoolantPort = null;
    public static final BlockReactorControlRod reactorControlRod = null;
    public static final BlockReactorRedNetPort reactorRedNetPort = null;
    public static final BlockMultiblockComputerPort reactorComputerPort = null;
    public static final BlockReactorRedstonePort reactorRedstonePort = null;
    public static final BlockReactorFuelRod reactorFuelRod = null;
    public static final BlockMultiblockIOPort reactorCreativeCoolantPort = null;

    // Turbine parts
    public static final BlockMultiblockGlass turbineGlass = null;
    public static final BlockMultiblockCasing turbineHousing = null;
    public static final BlockMultiblockController turbineController = null;
    public static final BlockMultiblockPowerTap turbinePowerTapRF = null;
    public static final BlockMultiblockPowerTap turbinePowerTapTesla = null;
    public static final BlockMultiblockComputerPort turbineComputerPort = null;
    public static final BlockMultiblockIOPort turbineFluidPort = null;
    public static final BlockTurbineRotorBearing turbineBearing = null;
    public static final BlockTurbineRotorShaft turbineRotorShaft = null;
    public static final BlockTurbineRotorBlade turbineRotorBlade = null;
    public static final BlockMultiblockIOPort turbineCreativeSteamGenerator = null;
    
    // Devices
    //public static final BlockBRDevice deviceCyaniteRep; // Bye Bye ...

    // Fluid blocks
    public static final BlockBRGenericFluid yellorium = null;
    public static final BlockBRGenericFluid cyanite = null;

    /*
    static void initialize() {
    }

    static {

        final InitHandler init = InitHandler.INSTANCE;
        final boolean regCreativeParts = BigReactors.CONFIG.registerCreativeMultiblockParts;

        // register blocks

        // - ores
        brOre = (BlockBROre)init.register(new BlockBROre("brore"));

        // - metal blocks
        blockMetals = (BlockBRMetal)init.register(new BlockBRMetal("blockmetals"));
        
        // - reactor parts
        reactorCasing = (BlockMultiblockCasing)init.register(new BlockMultiblockCasing(PartType.ReactorCasing, "reactorcasing"));
        reactorGlass = (BlockMultiblockGlass)init.register(new BlockMultiblockGlass(PartType.ReactorGlass, "reactorglass"));
        reactorController = (BlockMultiblockController)init.register(new BlockMultiblockController(PartType.ReactorController, "reactorcontroller"));
        reactorPowerTapRF = (BlockMultiblockPowerTap)init.register(new BlockMultiblockPowerTap(PartType.ReactorPowerTap, "reactorpowertaprf", PowerSystem.RedstoneFlux));
        reactorPowerTapTesla = (BlockMultiblockPowerTap)init.register(new BlockMultiblockPowerTap(PartType.ReactorPowerTap, "reactorpowertaptesla", PowerSystem.Tesla));
        reactorAccessPort = (BlockMultiblockIOPort)init.register(new BlockMultiblockIOPort(PartType.ReactorAccessPort, "reactoraccessport"));
        reactorCoolantPort = (BlockMultiblockIOPort)init.register(new BlockMultiblockIOPort(PartType.ReactorCoolantPort, "reactorcoolantport"));
        reactorControlRod = (BlockReactorControlRod)init.register(new BlockReactorControlRod("reactorcontrolrod"));
        reactorRedNetPort = (BlockReactorRedNetPort)init.register(new BlockReactorRedNetPort("reactorrednetport"));
        reactorComputerPort = (BlockMultiblockComputerPort)init.register(new BlockMultiblockComputerPort(PartType.ReactorComputerPort, "reactorcomputerport"));
        reactorRedstonePort = (BlockReactorRedstonePort)init.register(new BlockReactorRedstonePort("reactorredstoneport"));
        reactorFuelRod = (BlockReactorFuelRod)init.register(new BlockReactorFuelRod("reactorfuelrod"));
        reactorCreativeCoolantPort = !regCreativeParts ? null : (BlockMultiblockIOPort)init.register(new BlockMultiblockIOPort(PartType.ReactorCreativeCoolantPort, "reactorcreativecoolantport"));

        // - turbine parts
        turbineGlass = (BlockMultiblockGlass)init.register(new BlockMultiblockGlass(PartType.TurbineGlass, "turbineglass"));
        turbineHousing = (BlockMultiblockCasing)init.register(new BlockMultiblockCasing(PartType.TurbineHousing, "turbinehousing"));
        turbineController = (BlockMultiblockController)init.register(new BlockMultiblockController(PartType.TurbineController, "turbinecontroller"));
        turbinePowerTapRF = (BlockMultiblockPowerTap)init.register(new BlockMultiblockPowerTap(PartType.TurbinePowerPort, "turbinepowertaprf", PowerSystem.RedstoneFlux));
        turbinePowerTapTesla = (BlockMultiblockPowerTap)init.register(new BlockMultiblockPowerTap(PartType.TurbinePowerPort, "turbinepowertaptesla", PowerSystem.Tesla));
        turbineComputerPort = (BlockMultiblockComputerPort)init.register(new BlockMultiblockComputerPort(PartType.TurbineComputerPort, "turbinecomputerport"));
        turbineFluidPort = (BlockMultiblockIOPort)init.register(new BlockMultiblockIOPort(PartType.TurbineFluidPort, "turbinefluidport"));
        turbineBearing = (BlockTurbineRotorBearing)init.register(new BlockTurbineRotorBearing("turbinebearing"));
        turbineRotorShaft = (BlockTurbineRotorShaft)init.register(new BlockTurbineRotorShaft("turbinerotorshaft"));
        turbineRotorBlade = (BlockTurbineRotorBlade)init.register(new BlockTurbineRotorBlade("turbinerotorblade"));
        turbineCreativeSteamGenerator = !regCreativeParts ? null : (BlockMultiblockIOPort)init.register(new BlockMultiblockIOPort(PartType.TurbineCreativeSteamGenerator, "turbinecreativesteamgenerator"));

        // - devices
        //deviceCyaniteRep = (BlockBRDevice)init.register(new BlockBRDevice(DeviceType.CyaniteReprocessor, "deviceCyaniteRep"));

        // - fluid blocks
        yellorium = init.register(new BlockBRGenericFluid(BrFluids.fluidYellorium, "yellorium", new MaterialLiquid(MapColor.YELLOW)));
        cyanite = init.register(new BlockBRGenericFluid(BrFluids.fluidCyanite, "cyanite", Material.LAVA));

        // - register block tile entities

        //init.register(TileEntityCyaniteReprocessor.class);

        init.register(TileEntityReactorPart.class);
        init.register(TileEntityReactorGlass.class);
        init.register(TileEntityReactorController.class);
        init.register(TileEntityReactorPowerTapRedstoneFlux.class);
        init.register(TileEntityReactorPowerTapTesla.class);
        init.register(TileEntityReactorAccessPort.class);
        init.register(TileEntityReactorFuelRod.class);
        init.register(TileEntityReactorControlRod.class);
        init.register(TileEntityReactorRedstonePort.class);
        init.register(TileEntityReactorComputerPort.class);
        init.register(TileEntityReactorCoolantPort.class);
        init.register(TileEntityReactorCreativeCoolantPort.class);

        init.register(TileEntityTurbinePart.class);
        init.register(TileEntityTurbinePowerTapRedstoneFlux.class);
        init.register(TileEntityTurbinePowerTapTesla.class);
        init.register(TileEntityTurbineFluidPort.class);
        init.register(TileEntityTurbinePartGlass.class);
        init.register(TileEntityTurbineRotorBearing.class);
        init.register(TileEntityTurbineRotorShaft.class);
        init.register(TileEntityTurbineRotorBlade.class);
        init.register(TileEntityTurbineCreativeSteamGenerator.class);
        init.register(TileEntityTurbineComputerPort.class);
        init.register(TileEntityTurbineController.class);

        //init.register(TileEntityReactorRedNetPort.class);
    }
    */
}
