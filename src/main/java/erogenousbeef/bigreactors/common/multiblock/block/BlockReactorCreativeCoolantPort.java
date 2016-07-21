package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityReactorCreativeCoolantPort;
import erogenousbeef.bigreactors.utils.StaticUtils;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;

import java.util.List;

@Deprecated
public class BlockReactorCreativeCoolantPort extends BlockReactorPart {

    public BlockReactorCreativeCoolantPort(String blockName) {
        super(PartType.ReactorCreativeCoolantPort, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityReactorCreativeCoolantPort();
    }


    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

        if(player.isSneaking()) {
            return false;
        }
        // TODO fix
        TileEntity te = world.getTileEntity(pos);

        if(te instanceof TileEntityReactorCreativeCoolantPort) {

            TileEntityReactorCreativeCoolantPort cp = (TileEntityReactorCreativeCoolantPort)te;

            if(heldItem == null || StaticUtils.Inventory.isPlayerHoldingWrench(player)) {
                // Use wrench to change inlet/outlet state
                cp.setInlet(!cp.isInlet(), true);
            }
            else {
                cp.forceAddWater();
            }
            return true;
        }

        return false;
    }

    @Override // COMMON
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(Properties.PORTDIRECTION);
    }

    @Override // COMMON
    protected IBlockState buildDefaultState(IBlockState state) {
        return super.buildDefaultState(state).withProperty(Properties.PORTDIRECTION, PortDirection.Inlet);
    }

    @Override // COMMON
    protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position, MultiblockTileEntityBase part) {

        state = super.buildActualState(state, world, position, part);

        if (part instanceof TileEntityReactorCreativeCoolantPort) {

            TileEntityReactorCreativeCoolantPort port = (TileEntityReactorCreativeCoolantPort)part;

            state = state.withProperty(Properties.PORTDIRECTION, port.isInlet() ? PortDirection.Inlet : PortDirection.Outlet);
        }

        return state;
    }

    @Override // ADD 4 creative
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        // NO sub-blocks for the creative parts
    }
}
