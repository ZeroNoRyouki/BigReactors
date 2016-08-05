package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorAccessPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorCoolantPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityReactorCreativeCoolantPort;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.utils.StaticUtils;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

public class BlockReactorIOPort extends BlockMultiblockDevice {

    public BlockReactorIOPort(PartType type, String blockName) {
        super(type, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        switch (this._type) {

            case ReactorAccessPort:
                return new TileEntityReactorAccessPort();

            case ReactorCoolantPort:
                return new TileEntityReactorCoolantPort();

            case ReactorCreativeCoolantPort:
                return new TileEntityReactorCreativeCoolantPort();

            default:
                throw new IllegalArgumentException("Unrecognized part");
        }
    }

    @Override
    public void registerRecipes() {

        if (PartType.ReactorAccessPort == this._type) {

            if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
                GameRegistry.addRecipe(this.createItemStack(PartTier.Legacy, 1), "C C", " V ", "CPC",
                    'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1), 'V', Blocks.CHEST, 'P', Blocks.PISTON);

            if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
                GameRegistry.addRecipe(this.createItemStack(PartTier.Basic, 1), "C C", " V ", "CPC",
                    'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1), 'V', Blocks.CHEST, 'P', Blocks.PISTON);

        } else if (PartType.ReactorCoolantPort == this._type) {

            if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 1), "C C", "IVI", "CPC",
                    'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1), 'V', Items.BUCKET, 'P', Blocks.PISTON,
                    'I', "ingotIron"));

            if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 1), "C C", "IVI", "CPC",
                    'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1), 'V', Items.BUCKET, 'P', Blocks.PISTON,
                    'I', "ingotSteel"));
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

        if (!WorldHelper.calledByLogicalServer(world) || player.isSneaking())
            return false;

        TileEntity te = world.getTileEntity(pos);
        boolean hasWrench = StaticUtils.Inventory.isPlayerHoldingWrench(player);

        if (te instanceof TileEntityReactorCreativeCoolantPort) {

            TileEntityReactorCreativeCoolantPort cp = (TileEntityReactorCreativeCoolantPort)te;

            if (heldItem == null || hasWrench)
                // Use wrench to change inlet/outlet state
                cp.toggleInlet(true);
            else
                cp.forceAddWater();

            return true;
        }

        if (hasWrench) {

            if (te instanceof TileEntityReactorCoolantPort) {

                ((TileEntityReactorCoolantPort)te).toggleInlet(true);
                return true;

            } else if (te instanceof TileEntityReactorAccessPort) {

                ((TileEntityReactorAccessPort)te).toggleInlet();
                return true;
            }
        }

        return !(te instanceof TileEntityReactorCoolantPort) && super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {

        // NO sub-blocks for the creative parts
        if (PartType.ReactorCreativeCoolantPort != this._type)
            super.getSubBlocks(itemIn, tab, list);
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(Properties.PORTDIRECTION);
    }

    @Override
    protected IBlockState buildDefaultState(IBlockState state) {
        return super.buildDefaultState(state).withProperty(Properties.PORTDIRECTION, PortDirection.Inlet);
    }

    @Override
    protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position, MultiblockTileEntityBase part) {

        state = super.buildActualState(state, world, position, part);

        if (part instanceof TileEntityReactorAccessPort)
            state = state.withProperty(Properties.PORTDIRECTION,
                    ((TileEntityReactorAccessPort)part).isInlet() ? PortDirection.Inlet : PortDirection.Outlet);

        else if (part instanceof TileEntityReactorCoolantPort)
            state = state.withProperty(Properties.PORTDIRECTION,
                    ((TileEntityReactorCoolantPort)part).isInlet() ? PortDirection.Inlet : PortDirection.Outlet);

        return state;
    }
}
