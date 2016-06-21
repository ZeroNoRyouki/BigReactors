package erogenousbeef.bigreactors.common.multiblock.block;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

enum PortDirection implements IStringSerializable {

    Inlet,
    Outlet;

    public static final PropertyEnum<PortDirection> PORTDIRECTION = PropertyEnum.<PortDirection>create("portdirection", PortDirection.class);

    PortDirection() {

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

    private final String _name;
}
