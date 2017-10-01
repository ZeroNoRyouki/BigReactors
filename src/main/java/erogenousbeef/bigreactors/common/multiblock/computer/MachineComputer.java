package erogenousbeef.bigreactors.common.multiblock.computer;

import it.zerono.mods.zerocore.lib.block.ModTileEntity;
import net.minecraft.nbt.NBTTagCompound;

public abstract class MachineComputer {

    public void onAttachedToController() {
    }

    public void onDetachedFromController() {
    }

    public void syncDataFrom(NBTTagCompound data, ModTileEntity.SyncReason syncReason) {
    }

    public void syncDataTo(NBTTagCompound data, ModTileEntity.SyncReason syncReason) {
    }
}
