package erogenousbeef.bigreactors.common.item;

import erogenousbeef.bigreactors.common.BigReactors;
import it.zerono.mods.zerocore.lib.item.ModItem;

import javax.annotation.Nonnull;

public class ItemBRMetal extends ModItem {

	public ItemBRMetal(@Nonnull final String itemName, @Nonnull final String oreDictionaryName) {

		super(itemName, oreDictionaryName);
        this.setCreativeTab(BigReactors.TAB);
		this.setMaxDamage(0);
	}
}
