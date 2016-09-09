package erogenousbeef.bigreactors.common;

import net.minecraft.util.IStringSerializable;

public enum MineralType implements IStringSerializable {

    Anglesite(0),
    Benitoite(1);

    /**
     * All the enum values indexed by the meta-data value
     */
    public static final MineralType[] VALUES;

    MineralType(int meta) {

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

    public int toMeta() {
        return this._meta;
    }

    public static MineralType fromMeta(int meta) {

        if (meta < 0 || meta >= VALUES.length)
            meta = 0;

        return VALUES[meta];
    }

    private final String _name;
    private final int _meta;

    static {

        MineralType[] minerals = MineralType.values();

        VALUES = new MineralType[minerals.length];
        for (MineralType mineral: minerals)
            VALUES[mineral.toMeta()] = mineral;
    }
}
