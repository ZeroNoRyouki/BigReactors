package erogenousbeef.bigreactors.common.block;

import net.minecraft.util.IStringSerializable;

public enum DeviceType implements IStringSerializable {

    CyaniteReprocessor("brDeviceCyaniteProcessor");

    DeviceType(String oreDictionaryName) {

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

    public int toMeta() {
        return this.ordinal();
    }

    public static DeviceType fromMeta(int meta) {

        DeviceType[] values = DeviceType.values();

        if (meta < 0 || meta >= values.length)
            throw new IllegalArgumentException("Invalid meta data value");

        return values[meta];
    }

    private final String _name;
}

