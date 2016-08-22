package erogenousbeef.bigreactors.common.interfaces;

import net.minecraftforge.fluids.capability.IFluidTankProperties;

public interface IFluidHandlerInfo {
    /**
     * Returns an array of objects which represent the internal tanks.
     * These objects cannot be used to manipulate the internal tanks.
     *
     * @return Properties for the relevant internal tanks.
     */
    IFluidTankProperties[] getTankProperties();
}
