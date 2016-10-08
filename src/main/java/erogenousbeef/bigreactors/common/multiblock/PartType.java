package erogenousbeef.bigreactors.common.multiblock;

import net.minecraft.util.IStringSerializable;

public enum PartType implements IStringSerializable {

    ReactorGlass("glassReactor"),
    ReactorCasing("reactorCasing"),
    ReactorController("reactorController"),
    ReactorControlRod("reactorControlRod"),
    ReactorFuelRod("reactorFuelRod"),
    ReactorPowerTap("reactorPowerTap"),
    ReactorAccessPort("reactorAccessPort"),
    ReactorCoolantPort("reactorCoolantPort"),
    ReactorRedstonePort("reactorRedstonePort"),
    ReactorRednetPort("reactorRedNetPort"),
    ReactorComputerPort("reactorComputerPort"),
    ReactorCreativeCoolantPort(""),
    TurbineGlass("glassTurbine"),
    TurbineHousing("turbineHousing"),
    TurbineController("turbineController"),
    TurbinePowerPort("turbinePowerTapRF"),
    TurbineFluidPort("turbineFluidPort"),
    TurbineRotorBearing("turbineBearing"),
    TurbineComputerPort("turbineComputerPort"),
    TurbineRotorShaft("turbineRotorShaft"),
    TurbineRotorBlade("turbineRotorBlade"),
    TurbineCreativeSteamGenerator("");

    PartType(String oreDictionaryName) {

        this._name = this.name().toLowerCase();
        this.oreDictionaryName = oreDictionaryName;
    }

    public final String oreDictionaryName;

    @Override
    public String toString() {

        return this._name;
    }

    @Override
    public String getName() {

        return this._name;
    }

    private final String _name;
}
