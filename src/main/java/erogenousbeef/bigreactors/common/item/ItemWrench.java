package erogenousbeef.bigreactors.common.item;

import cofh.api.item.IToolHammer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWrench extends ItemBase implements IToolHammer {

    public ItemWrench(String itemName) {

        super(itemName);
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPostClientRegister() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(new ItemStack(this, 1, 0), " I ", "WII", "IW ",
                'I', Items.IRON_INGOT, 'W', new ItemStack(Blocks.WOOL, 1, EnumDyeColor.YELLOW.getMetadata()));
    }

    /**
     * Called to ensure that the tool can be used.
     *
     * @param item
     *            The ItemStack for the tool. Not required to match equipped item (e.g., multi-tools that contain other tools).
     * @param user
     *            The entity using the tool.
     * @param pos
     *            Coordinates of the block.
     * @return True if this tool can be used
     */
    @Override
    public boolean isUsable(ItemStack item, EntityLivingBase user, BlockPos pos) {
        return user instanceof EntityPlayer;
    }

    /**
     * Callback for when the tool has been used reactively.
     *
     * @param item
     *            The ItemStack for the tool. Not required to match equipped item (e.g., multi-tools that contain other tools).
     * @param user
     *            The entity using the tool.
     * @param pos
     *            Coordinates of the block.
     */
    @Override
    public void toolUsed(ItemStack item, EntityLivingBase user, BlockPos pos) {
    }
}
