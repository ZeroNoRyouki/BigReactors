package erogenousbeef.bigreactors.common.block;

import erogenousbeef.bigreactors.common.BigReactors;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import net.minecraft.block.material.Material;

import javax.annotation.Nonnull;

public class BlockBRMetal extends ModBlock {

	public BlockBRMetal(@Nonnull final String blockName, @Nonnull final String oreDictionaryName) {

		super(blockName, Material.IRON, oreDictionaryName);
        this.setCreativeTab(BigReactors.TAB);
        this.setHardness(2.0f);
	}
}
