package erogenousbeef.bigreactors.common.multiblock;

import net.minecraft.util.IStringSerializable;

import java.util.EnumSet;

public enum PartTier implements IStringSerializable {

    Legacy(0),
    Basic(1);

    /**
     * All the enum values indexed by the meta-data value
     */
    public static final PartTier[] VALUES;
    public static final PartTier[] RELEASED_TIERS;

    public static final EnumSet<PartTier> REACTOR_TIERS;
    public static final EnumSet<PartTier> TURBINE_TIERS;

    PartTier(int meta) {

        this._name = this.name().toLowerCase();
        this._meta = meta;
    }

    public int toMeta() {
        return this._meta;
    }

    public static PartTier fromMeta(int meta) {

        if (meta < 0 || meta >= VALUES.length)
            meta = 0;

        return VALUES[meta];
    }

    @Override
    public String toString() {
        return this._name;
    }

    @Override
    public String getName() {
        return this._name;
    }

    private final String _name;
    private final int _meta;

    static {

        final PartTier[] tiers = PartTier.values();

        VALUES = new PartTier[tiers.length];
        for (PartTier tier: tiers)
            VALUES[tier.toMeta()] = tier;

        REACTOR_TIERS = EnumSet.of(Legacy);
        TURBINE_TIERS = EnumSet.of(Legacy);

        RELEASED_TIERS = new PartTier[] { Legacy };
    }
}
