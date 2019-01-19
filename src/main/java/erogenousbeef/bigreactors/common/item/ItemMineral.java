package erogenousbeef.bigreactors.common.item;

import erogenousbeef.bigreactors.common.BigReactors;
import it.zerono.mods.zerocore.lib.item.ModItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMineral extends ModItem {

    public ItemMineral(String itemName) {

        super(itemName);
        this.setCreativeTab(BigReactors.TAB);
        this.setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRegisterModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
