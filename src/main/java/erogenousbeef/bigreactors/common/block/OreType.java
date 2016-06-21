package erogenousbeef.bigreactors.common.block;

import net.minecraft.util.IStringSerializable;

public enum OreType implements IStringSerializable {

    Yellorite;

    OreType() {

        this._name = this.name().toLowerCase();
    }

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

    public static OreType fromMeta(int meta) {

        OreType[] values = OreType.values();

        if (meta < 0 || meta >= values.length)
            throw new IllegalArgumentException("Invalid meta data value");

        return values[meta];
    }

    private final String _name;
}
