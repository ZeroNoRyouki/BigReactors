package erogenousbeef.bigreactors.common.item;

import erogenousbeef.bigreactors.common.BigReactors;
import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.item.ModItem;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import it.zerono.mods.zerocore.util.CodeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemWrench extends ModItem/* implements IToolHammer*/ {

    public ItemWrench(String itemName) {

        super(itemName);
        this.setCreativeTab(BigReactors.TAB);
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {

        if (EnumHand.OFF_HAND == hand && WorldHelper.calledByLogicalServer(world)) {

            final TileEntity te = world.getTileEntity(pos);

            if (te instanceof IMultiblockPart) {

                final IMultiblockPart part = (IMultiblockPart) te;
                final BlockPos minCoord = part.getMultiblockController().getMinimumCoord();
                final BlockPos maxCoord = part.getMultiblockController().getMaximumCoord();

                if (player.isSneaking()) {

                    CodeHelper.sendChatMessage(player, new TextComponentTranslation("item.bigreactors:wrench.machine.update"));
                    part.getMultiblockController().forceStructureUpdate(world);

                } else {

                    CodeHelper.sendChatMessage(player, new TextComponentTranslation("item.bigreactors:wrench.machine.coords",
                            minCoord.getX(), minCoord.getY(), minCoord.getZ(),
                            maxCoord.getX(), maxCoord.getY(), maxCoord.getZ()));
                }

                return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.PASS;
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
     *//*
    @Override
    public boolean isUsable(ItemStack item, EntityLivingBase user, BlockPos pos) {
        return user instanceof EntityPlayer;
    }*/

    /**
     * Called to ensure that the tool can be used on an entity.
     *
     * @param item
     *            The ItemStack for the tool. Not required to match equipped item (e.g., multi-tools that contain other tools).
     * @param user
     *            The entity using the tool.
     * @param entity
     *            The entity the tool is being used on.
     * @return True if this tool can be used.
     *//*
    @Override
    public boolean isUsable(ItemStack item, EntityLivingBase user, Entity entity) {
        return false;
    }*/

    /**
     * Callback for when the tool has been used reactively.
     *
     * @param item
     *            The ItemStack for the tool. Not required to match equipped item (e.g., multi-tools that contain other tools).
     * @param user
     *            The entity using the tool.
     * @param pos
     *            Coordinates of the block.
     *//*
    @Override
    public void toolUsed(ItemStack item, EntityLivingBase user, BlockPos pos) {
    }*/

    /**
     * Callback for when the tool has been used reactively.
     *
     * @param item
     *            The ItemStack for the tool. Not required to match equipped item (e.g., multi-tools that contain other tools).
     * @param user
     *            The entity using the tool.
     * @param entity
     *            The entity the tool is being used on.
     *//*
    @Override
    public void toolUsed(ItemStack item, EntityLivingBase user, Entity entity) {
    }*/


}
