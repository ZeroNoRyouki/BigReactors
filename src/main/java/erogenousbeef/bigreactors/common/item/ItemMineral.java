package erogenousbeef.bigreactors.common.item;

import erogenousbeef.bigreactors.common.MineralType;
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

public class ItemMineral extends ItemBase  {

    public ItemMineral(String itemName) {

        super(itemName);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this._subItems = null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPostClientRegister() {

        ResourceLocation location = this.getRegistryName();

        for (MineralType mineral : MineralType.values())
            ModelLoader.setCustomModelResourceLocation(this, mineral.toMeta(),
                    new ModelResourceLocation(location, String.format("mineral=%s", mineral.getName())));
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        return super.getUnlocalizedName() + "." + MineralType.fromMeta(stack.getMetadata()).getName();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {

        if (null == this._subItems) {

            MineralType[] types = MineralType.VALUES;
            int length = types.length;

            this._subItems = new ArrayList<>(length);

            for (int i = 0; i < length; ++i)
                this._subItems.add(new ItemStack(item, 1, types[i].toMeta()));
        }

        list.addAll(this._subItems);
    }

    public ItemStack createItemStack(MineralType mineral, int amount) {

        return new ItemStack(this, amount, mineral.toMeta());
    }

    private List<ItemStack> _subItems;
}
