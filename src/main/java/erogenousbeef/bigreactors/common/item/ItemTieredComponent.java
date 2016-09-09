package erogenousbeef.bigreactors.common.item;

import erogenousbeef.bigreactors.common.multiblock.PartTier;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ItemTieredComponent extends ItemBase {

    public ItemTieredComponent(String itemName) {

        super(itemName);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this._subItems = null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPostClientRegister() {

        ResourceLocation location = this.getRegistryName();

        for (PartTier tier : PartTier.values())
            ModelLoader.setCustomModelResourceLocation(this, tier.toMeta(),
                    new ModelResourceLocation(location, String.format("tier=%s", tier.getName())));
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        return super.getUnlocalizedName() + "." + PartTier.fromMeta(stack.getMetadata()).getName();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {

        if (null == this._subItems) {

            PartTier[] types = PartTier.RELEASED_TIERS;
            int length = types.length;

            this._subItems = new ArrayList<>(length);

            for (int i = 0; i < length; ++i)
                this._subItems.add(new ItemStack(item, 1, types[i].toMeta()));
        }

        list.addAll(this._subItems);
    }

    public ItemStack createItemStack(PartTier tier, int amount) {

        return new ItemStack(this, amount, tier.toMeta());
    }

    private List<ItemStack> _subItems;
}
