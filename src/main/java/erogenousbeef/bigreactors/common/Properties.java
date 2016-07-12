package erogenousbeef.bigreactors.common;

import erogenousbeef.bigreactors.common.block.OreType;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.block.*;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;

public final class Properties {

    public static final PropertyEnum<MachinePartState> PARTSTATE = PropertyEnum.create("afstate", MachinePartState.class);
    public static final PropertyEnum<PartTier> TIER = PropertyEnum.create("tier", PartTier.class);
    public static final PropertyEnum<BlockMultiblockCasing.CasingType> CASINGTYPE = PropertyEnum.create("casing", BlockMultiblockCasing.CasingType.class);
    public static final PropertyEnum<BlockReactorController.ControllerState> CONTROLLERSTATE = PropertyEnum.create("controller", BlockReactorController.ControllerState.class);
    public static final PropertyEnum<PortDirection> PORTDIRECTION = PropertyEnum.create("portdirection", PortDirection.class);
    public static final PropertyEnum<BlockReactorPowerTap.PowerTapState> POWERTAPSTATE = PropertyEnum.create("powerstate", BlockReactorPowerTap.PowerTapState.class);
    public static final PropertyEnum<FuelRodState> FUELRODSTATE = PropertyEnum.create("fuelrodstate", FuelRodState.class);
    public static final PropertyBool LIT = PropertyBool.create("lit");
    public static final PropertyEnum<MetalType> METAL = PropertyEnum.create("metal", MetalType.class);
    public static final PropertyEnum<OreType> ORE = PropertyEnum.create("ore", OreType.class);

    private Properties() {
    }
}
