package erogenousbeef.bigreactors.common;

import it.zerono.mods.zerocore.lib.MetalSize;
import net.minecraft.util.IStringSerializable;

public enum MetalType implements IStringSerializable {

    Yellorium(0),
    Cyanite(1),
    Graphite(2),
    Blutonium(3),
    Ludicrite(4),
    Steel(5);

    /**
     * All the enum values indexed by the meta-data value
     */
    public static final MetalType[] VALUES;

    MetalType(int meta) {

        this._name = this.name().toLowerCase();
        this._meta = meta;
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
        return this._meta;
    }

    public static MetalType fromMeta(int meta) {

        if (meta < 0 || meta >= VALUES.length)
            meta = 0;

        return VALUES[meta];
    }

    private final String _name;
    private final int _meta;

    static {

        MetalType[] metals = MetalType.values();

        VALUES = new MetalType[metals.length];
        for (MetalType metal: metals)
            VALUES[metal.toMeta()] = metal;
    }
}