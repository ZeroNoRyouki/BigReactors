package erogenousbeef.bigreactors.common.block;

import erogenousbeef.bigreactors.common.MineralType;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum OreType implements IStringSerializable {

    Yellorite(0, null),
    Anglesite(1, MineralType.Anglesite),
    Benitoite(2, MineralType.Benitoite);

    /**
     * All the enum values indexed by the meta-data value
     */
    public static final OreType[] VALUES;

    OreType(int meta, MineralType mineralDropped) {

        this._name = this.name().toLowerCase();
        this._meta = meta;
        this._mineralDropped = mineralDropped;
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

    @Nullable
    public MineralType getMineralDropped() {
        return this._mineralDropped;
    }

    @Nonnull
    public static OreType fromMeta(int meta) {

        if (meta < 0 || meta >= VALUES.length)
            meta = 0;

        return VALUES[meta];
    }

    private final String _name;
    private final int _meta;
    private final MineralType _mineralDropped;

    static {

        OreType[] ores = OreType.values();

        VALUES = new OreType[ores.length];
        for (OreType ore: ores)
            VALUES[ore.toMeta()] = ore;
    }
}
