package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zero.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import zero.mods.zerocore.lib.block.properties.Orientation;

public class BlockMultiblockDevice extends BlockPart {

    public BlockMultiblockDevice(PartType type, String blockName) {

        super(type, blockName, Material.iron);
        this.setDefaultState(
                this.blockState.getBaseState()
                        .withProperty(BlockMultiblockDevice.ASSEMBLED, false)
                        .withProperty(Orientation.HFACING, EnumFacing.NORTH)
        );
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {

        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (EnumFacing.Axis.Y == enumfacing.getAxis())
            enumfacing = EnumFacing.NORTH;

        return this.getDefaultState().withProperty(Orientation.HFACING, enumfacing);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {

        return (state.getValue(Orientation.HFACING)).getIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos position) {

        TileEntity te = world.getTileEntity(position);

        if (te instanceof RectangularMultiblockTileEntityBase) {

            RectangularMultiblockTileEntityBase part = (RectangularMultiblockTileEntityBase)te;
            boolean assembled = part.isConnected() && part.getMultiblockController().isAssembled();

            state = state.withProperty(ASSEMBLED, assembled);

            if (assembled) {

                switch (part.getPartPosition()) {

                    case NorthFace:
                        state = state.withProperty(Orientation.HFACING, EnumFacing.NORTH);
                        break;

                    case SouthFace:
                        state = state.withProperty(Orientation.HFACING, EnumFacing.SOUTH);
                        break;

                    case WestFace:
                        state = state.withProperty(Orientation.HFACING, EnumFacing.WEST);
                        break;

                    case EastFace:
                        state = state.withProperty(Orientation.HFACING, EnumFacing.EAST);
                        break;
                }
            }
        }

        return state;
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                     int meta, EntityLivingBase placer) {

        facing = (null != placer) ? placer.getHorizontalFacing().getOpposite() : EnumFacing.NORTH;

        return this.getDefaultState().withProperty(Orientation.HFACING, facing);
    }

    @Override
    public void onBlockAdded(World world, BlockPos position, IBlockState state) {

        EnumFacing newFacing = Orientation.suggestDefaultHorizontalFacing(world, position,
                state.getValue(Orientation.HFACING));

        world.setBlockState(position, state.withProperty(Orientation.HFACING, newFacing), 2);
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(BlockMultiblockDevice.ASSEMBLED);
        builder.add(Orientation.HFACING);
    }

    private static final PropertyBool ASSEMBLED = PropertyBool.create("assembled");
}
