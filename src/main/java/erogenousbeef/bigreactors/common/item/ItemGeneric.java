package erogenousbeef.bigreactors.common.item;

import erogenousbeef.bigreactors.common.BigReactors;
import it.zerono.mods.zerocore.lib.item.ModItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemGeneric extends ModItem {

    public ItemGeneric(@Nonnull final String itemName) {
        this(itemName, null);
    }

    public ItemGeneric(@Nonnull final String itemName, @Nullable final String oreDictionaryName) {

        super(itemName, oreDictionaryName);
        this.setCreativeTab(BigReactors.TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
    }
}
