package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.block.BlockBR;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError;
import it.zerono.mods.zerocore.lib.block.ModTileEntity;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Base class for a multiblock part
 */
public class BlockPart extends BlockBR {

    public BlockPart(PartType type, String blockName, Material material) {

        super(blockName, material);
        this._type = type;
        this.setSoundType(SoundType.METAL);
    }

    public PartType getType() {
        return this._type;
    }

    /*
    @Override
    public void onPostRegister() {

        super.onPostRegister();

        String name = this._type.oreDictionaryName;

        if (name.length() > 0)
            OreDictionary.registerOre(name, this.createItemStack());

    }*/

    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vanilla
     * blocks, and also want to have a tile entity on that block, may.
     *
     * Return true from this function to specify this block has a tile entity.
     *
     * @param state State of the current block
     * @return True if block has a tile entity, false otherwise
     */
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos posistion, IBlockState state, EntityPlayer player, EnumHand hand,
                                    ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

        if (this.hasTileEntity(state) && !player.isSneaking()) {

            TileEntity te = world.getTileEntity(posistion);

            if (te instanceof ModTileEntity && (EnumHand.MAIN_HAND == hand)) {

                ModTileEntity modTe = (ModTileEntity)te;

                if (modTe.canOpenGui(world, posistion, state)) {

                    // we have to check all the above on the client side too to avoid placing a ghost-block on that side

                    if (WorldHelper.calledByLogicalServer(world))
                        player.openGui(BigReactors.getInstance(), 0, world, posistion.getX(), posistion.getY(), posistion.getZ());

                    return true;
                }
            }

            // If the player's hands are empty and they rightclick on a multiblock, they get a
            // multiblock-debugging message if the machine is not assembled.

            if ((te instanceof IMultiblockPart) && WorldHelper.calledByLogicalServer(world) && (null == heldItem) && (hand == EnumHand.OFF_HAND)) {

                IMultiblockPart part = (IMultiblockPart) te;
                MultiblockControllerBase controller = part.getMultiblockController();
                ITextComponent message = null;

                if (null != controller) {

                    ValidationError error = controller.getLastError();

                    if (null != error)
                        message = error.getChatMessage();

                } else {

                    message = new TextComponentTranslation("multiblock.validation.block_not_connected");
                }

                if (null != message) {

                    player.addChatMessage(message);
                    return true;
                }
            }
        }

        return super.onBlockActivated(world, posistion, state, player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public void neighborChanged(IBlockState stateAtPosition, World world, BlockPos position, Block neighbor) {

        TileEntity te = world.getTileEntity(position);

        // Signal power taps when their neighbors change, etc.
        if (te instanceof INeighborUpdatableEntity) {
            ((INeighborUpdatableEntity)te).onNeighborBlockChange(world, position, stateAtPosition, neighbor);
        }
    }
    
    @Override
    public void breakBlock(World world, BlockPos position, IBlockState state) {

        TileEntity te = world.getTileEntity(position);

        if (null == te)
            return;

        // empty any inventories found here
        if (te instanceof IInventory) {

            IInventory inventory = (IInventory)te;
            int size = inventory.getSizeInventory();
            double x = position.getX(), y = position.getY(), z = position.getZ();

            for (int slot = 0; slot < size; ++slot) {

                ItemStack stack = inventory.removeStackFromSlot(slot);

                if (null != stack)
                    WorldHelper.spawnItemStack(stack, world, x, y, z, false);
            }
        }

        // remove the TE from the world
        world.removeTileEntity(position);
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos position, EnumFacing side) {

        IBlockState sideState = blockAccess.getBlockState(position.offset(side));
        Block sideBlock = sideState.getBlock();

        return sideBlock != this;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos position) {

        TileEntity te = world.getTileEntity(position);

        if (te instanceof MultiblockTileEntityBase)
            state = this.buildActualState(state, world, position, (MultiblockTileEntityBase)te);

        return state;
    }

    protected IBlockState buildActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos position,
                                           @Nonnull MultiblockTileEntityBase part) {
        return state;
    }

    protected final PartType _type;
}
