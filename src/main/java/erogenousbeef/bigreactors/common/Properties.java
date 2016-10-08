package erogenousbeef.bigreactors.common;

import erogenousbeef.bigreactors.common.block.OreType;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.RotorBladeState;
import erogenousbeef.bigreactors.common.multiblock.RotorShaftState;
import erogenousbeef.bigreactors.common.multiblock.block.*;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;

public final class Properties {

    public static final PropertyEnum<MachinePartState> PARTSTATE = PropertyEnum.create("afstate", MachinePartState.class);
    public static final PropertyEnum<PartTier> TIER = PropertyEnum.create("tier", PartTier.class);
    public static final PropertyEnum<BlockMultiblockCasing.CasingType> CASINGTYPE = PropertyEnum.create("casing", BlockMultiblockCasing.CasingType.class);
    public static final PropertyEnum<ControllerState> CONTROLLERSTATE = PropertyEnum.create("controller", ControllerState.class);
    public static final PropertyEnum<PortDirection> PORTDIRECTION = PropertyEnum.create("portdirection", PortDirection.class);
    public static final PropertyEnum<PowerTapState> POWERTAPSTATE = PropertyEnum.create("powerstate", PowerTapState.class);
    public static final PropertyEnum<FuelRodState> FUELRODSTATE = PropertyEnum.create("fuelrodstate", FuelRodState.class);
    public static final PropertyBool LIT = PropertyBool.create("lit");
    public static final PropertyEnum<MetalType> METAL = PropertyEnum.create("metal", MetalType.class);
    public static final PropertyEnum<OreType> ORE = PropertyEnum.create("ore", OreType.class);
    public static final PropertyEnum<RotorShaftState> ROTORSHAFTSTATE = PropertyEnum.create("state", RotorShaftState.class);
    public static final PropertyEnum<RotorBladeState> ROTORBLADESTATE = PropertyEnum.create("state", RotorBladeState.class);

    private Properties() {
    }
}
