package erogenousbeef.bigreactors.common;

import it.zerono.mods.zerocore.lib.MetalSize;
import net.minecraft.util.IStringSerializable;

public enum MetalType implements IStringSerializable {

    Yellorium,
    Cyanite,
    Graphite,
    Blutonium,
    Ludicrite;

    MetalType() {

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

    public String getOreDictionaryName(MetalSize size) {

        return size.oreDictionaryPrefix + this.name();
    }

    public int toMeta() {
        return this.ordinal();
    }

    public static MetalType fromMeta(int meta) {

        MetalType[] values = MetalType.values();

        if (meta < 0 || meta >= values.length)
            throw new IllegalArgumentException("Invalid meta data value");

        return values[meta];
    }

    private final String _name;
}