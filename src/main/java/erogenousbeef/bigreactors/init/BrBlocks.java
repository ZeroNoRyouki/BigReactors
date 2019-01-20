package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.block.BlockBRGenericFluid;
import erogenousbeef.bigreactors.common.block.BlockBRMetal;
import erogenousbeef.bigreactors.common.block.BlockBROre;
import erogenousbeef.bigreactors.common.multiblock.block.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(BigReactors.MODID)
public final class BrBlocks {

    // Ores
    /*
    @GameRegistry.ObjectHolder("brore")
    public static final BlockBROre brOre = null;
    */
    @GameRegistry.ObjectHolder("oreyellorite")
    public static final BlockBROre oreYellorite = null;

    @GameRegistry.ObjectHolder("oreanglesite")
    public static final BlockBROre oreAnglesite = null;

    @GameRegistry.ObjectHolder("orebenitoite")
    public static final BlockBROre oreBenitoite = null;

    // Metal blocks
    /*
    @GameRegistry.ObjectHolder("blockmetals")
    public static final BlockBRMetal blockMetals = null;
    */
    @GameRegistry.ObjectHolder("blockyellorium")
    public static final BlockBRMetal blockYellorium = null;

    @GameRegistry.ObjectHolder("blockcyanite")
    public static final BlockBRMetal blockCyanite = null;

    @GameRegistry.ObjectHolder("blockgraphite")
    public static final BlockBRMetal blockGraphite = null;

    @GameRegistry.ObjectHolder("blockblutonium")
    public static final BlockBRMetal blockBlutonium = null;

    @GameRegistry.ObjectHolder("blockludicrite")
    public static final BlockBRMetal blockLudicrite = null;

    @GameRegistry.ObjectHolder("blocksteel")
    public static final BlockBRMetal blockSteel = null;

    // Reactor parts

    @GameRegistry.ObjectHolder("reactorglass")
    public static final BlockMultiblockGlass reactorGlass = null;

    @GameRegistry.ObjectHolder("reactorcasing")
    public static final BlockMultiblockCasing reactorCasing = null;

    @GameRegistry.ObjectHolder("reactorcontroller")
    public static final BlockMultiblockController reactorController = null;

    @GameRegistry.ObjectHolder("reactorpowertaprf")
    public static final BlockMultiblockPowerTap reactorPowerTapRF = null;

    @GameRegistry.ObjectHolder("reactorpowertaptesla")
    public static final BlockMultiblockPowerTap reactorPowerTapTesla = null;

    @GameRegistry.ObjectHolder("reactoraccessport")
    public static final BlockMultiblockIOPort reactorAccessPort = null;

    @GameRegistry.ObjectHolder("reactorcoolantport")
    public static final BlockMultiblockIOPort reactorCoolantPort = null;

    @GameRegistry.ObjectHolder("reactorcontrolrod")
    public static final BlockReactorControlRod reactorControlRod = null;

    @GameRegistry.ObjectHolder("reactorrednetport")
    public static final BlockReactorRedNetPort reactorRedNetPort = null;

    @GameRegistry.ObjectHolder("reactorcomputerport")
    public static final BlockMultiblockComputerPort reactorComputerPort = null;

    @GameRegistry.ObjectHolder("reactorredstoneport")
    public static final BlockReactorRedstonePort reactorRedstonePort = null;

    @GameRegistry.ObjectHolder("reactorfuelrod")
    public static final BlockReactorFuelRod reactorFuelRod = null;

    @GameRegistry.ObjectHolder("reactorcreativecoolantport")
    public static final BlockMultiblockIOPort reactorCreativeCoolantPort = null;

    // Turbine parts

    @GameRegistry.ObjectHolder("turbineglass")
    public static final BlockMultiblockGlass turbineGlass = null;

    @GameRegistry.ObjectHolder("turbinehousing")
    public static final BlockMultiblockCasing turbineHousing = null;

    @GameRegistry.ObjectHolder("turbinecontroller")
    public static final BlockMultiblockController turbineController = null;

    @GameRegistry.ObjectHolder("turbinepowertaprf")
    public static final BlockMultiblockPowerTap turbinePowerTapRF = null;

    @GameRegistry.ObjectHolder("turbinepowertaptesla")
    public static final BlockMultiblockPowerTap turbinePowerTapTesla = null;

    @GameRegistry.ObjectHolder("turbinecomputerport")
    public static final BlockMultiblockComputerPort turbineComputerPort = null;

    @GameRegistry.ObjectHolder("turbinefluidport")
    public static final BlockMultiblockIOPort turbineFluidPort = null;

    @GameRegistry.ObjectHolder("turbinebearing")
    public static final BlockTurbineRotorBearing turbineBearing = null;

    @GameRegistry.ObjectHolder("turbinerotorshaft")
    public static final BlockTurbineRotorShaft turbineRotorShaft = null;

    @GameRegistry.ObjectHolder("turbinerotorblade")
    public static final BlockTurbineRotorBlade turbineRotorBlade = null;

    @GameRegistry.ObjectHolder("turbinecreativesteamgenerator")
    public static final BlockMultiblockIOPort turbineCreativeSteamGenerator = null;
    
    // Devices
    //public static final BlockBRDevice deviceCyaniteRep; // Bye Bye ...

    // Fluid blocks

    @GameRegistry.ObjectHolder("yellorium")
    public static final BlockBRGenericFluid yellorium = null;

    @GameRegistry.ObjectHolder("cyanite")
    public static final BlockBRGenericFluid cyanite = null;
}