package erogenousbeef.bigreactors.common;

import it.zerono.mods.zerocore.lib.item.TileEntityItemStackHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public abstract class ItemHandlerWrapper implements IItemHandler {

    public ItemHandlerWrapper(TileEntityItemStackHandler handler) {
        this._handler = handler;
    }

    @Override
    public int getSlots() {
        return this._handler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this._handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this._handler.extractItem(slot, amount, simulate);
    }

    protected TileEntityItemStackHandler _handler;
}
