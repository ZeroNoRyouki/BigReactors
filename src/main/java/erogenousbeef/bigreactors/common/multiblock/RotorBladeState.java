package erogenousbeef.bigreactors.common.multiblock;

import com.google.common.collect.Maps;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;
import java.util.Map;

public enum RotorBladeState implements IStringSerializable {

    Y_X_POS,
    Y_X_NEG,
    Y_Z_POS,
    Y_Z_NEG,
    X_Y_POS,
    X_Y_NEG,
    X_Z_POS,
    X_Z_NEG,
    Z_Y_POS,
    Z_Y_NEG,
    Z_X_POS,
    Z_X_NEG;

    RotorBladeState() {
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

    @Nullable
    public static RotorBladeState fromName(String name) {
        return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
    }

    private final String _name;
    private static final Map<String, RotorBladeState> NAME_LOOKUP = Maps.newHashMap();

    static {

        for (RotorBladeState state : values())
            NAME_LOOKUP.put(state.getName(), state);
    }
}