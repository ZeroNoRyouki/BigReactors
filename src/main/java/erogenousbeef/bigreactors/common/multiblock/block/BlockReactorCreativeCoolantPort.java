package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityReactorCreativeCoolantPort;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockReactorCreativeCoolantPort extends BlockMBCreativePart {

    public BlockReactorCreativeCoolantPort(String blockName) {

        super(PartType.ReactorCreativeCoolantPort, blockName);
        this.setDefaultState(
                this.blockState.getBaseState().withProperty(PortDirection.PORTDIRECTION, PortDirection.Inlet)
        );
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos position) {

        TileEntity te = world.getTileEntity(position);

        state = super.getActualState(state, world, position);

        if (te instanceof TileEntityReactorCreativeCoolantPort) {

            TileEntityReactorCreativeCoolantPort port = (TileEntityReactorCreativeCoolantPort)te;

            state = state.withProperty(PortDirection.PORTDIRECTION, port.isInlet() ? PortDirection.Inlet : PortDirection.Outlet);
        }

        return state;
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(PortDirection.PORTDIRECTION);
    }
}
