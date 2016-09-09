package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartTier;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

class ItemBlockPartTier extends ItemBlock {

    public ItemBlockPartTier(Block block) {

        super(block);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + PartTier.fromMeta(stack.getMetadata()).getName();
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }
}
