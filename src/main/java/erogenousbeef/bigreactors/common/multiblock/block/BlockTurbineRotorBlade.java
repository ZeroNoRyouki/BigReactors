package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.RotorBladeState;
import erogenousbeef.bigreactors.common.multiblock.RotorShaftState;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorBlade;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorPart;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import it.zerono.mods.zerocore.lib.block.properties.Orientation;
import li.cil.oc.api.event.RackMountableRenderEvent;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTurbineRotorBlade extends BlockTieredPart implements ITurbineRotorPart {

    public BlockTurbineRotorBlade(String blockName) {

        super(PartType.TurbineRotorBlade, blockName, Material.IRON);
        this.setLightLevel(0.9f);
        this._neighbors = new IBlockState[EnumFacing.VALUES.length];
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTurbineRotorBlade();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    /**
     * Override and add custom masses when you add non-standard turbine parts
     */
    @Override
    public int getMass(IBlockState blockState) {
        return 10;
    }

    @Override
    public boolean isShaft() {
        return false;
    }

    @Override
    public boolean isBlade() {
        return true;
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(Properties.ROTORBLADESTATE);
    }

    @Override
    protected IBlockState buildDefaultState(IBlockState state) {
        return super.buildDefaultState(state).withProperty(Properties.ROTORBLADESTATE, RotorBladeState.Z_X_POS);
    }

    @Override
    protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position, MultiblockTileEntityBase part) {

        RotorBladeState candidatState = null;
        final int neighborsSlotCount = this._neighbors.length;

        for (int i = 0; i < neighborsSlotCount; ++i)
            this._neighbors[i] = null;

        // looking up for a rotor shaft

        for (EnumFacing facing: EnumFacing.VALUES) {

            final BlockPos neighborPos = position.offset(facing);
            IBlockState neighbor = world.getBlockState(neighborPos);

            this._neighbors[facing.getIndex()] = neighbor;

            if (BrBlocks.turbineRotorShaft == neighbor.getBlock()) {

                // found a rotor shaft: orient the blade toward it

                neighbor = BrBlocks.turbineRotorShaft.getActualState(neighbor, world, neighborPos);

                final RotorShaftState shaftState = neighbor.getValue(Properties.ROTORSHAFTSTATE);
                String name;

                switch (shaftState) {

                    case X_YZ:
                        name ="x_" + facing.getAxis().getName() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? "_neg" : "_pos");
                        break;

                    case Y_XZ:
                        name ="y_" + facing.getAxis().getName() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? "_neg" : "_pos");
                        break;

                    case Z_XY:
                        name ="z_" + facing.getAxis().getName() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? "_neg" : "_pos");
                        break;

                    default:
                        name = shaftState.getName() + (facing.getOpposite().getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? "_pos" : "_neg");
                        break;
                }

                candidatState = RotorBladeState.fromName(name);
                break;
            }
        }
        /*
        if (null == candidatFacing) {

            // no rotor shaft found, let's search for other blades then

            for (EnumFacing facing: EnumFacing.VALUES) {

                final int index = facing.getIndex();
                IBlockState neighbor = this._neighbors[index];

                if (null == neighbor)
                    neighbor = world.getBlockState(position.offset(facing));

                if (this == neighbor.getBlock()) {

                    // found another blade: orient the blade toward it

                    candidateAxis = facing.getAxis();
                    break;
                }
            }
        }
        */

        if (null == candidatState)
            // still no joy? let's go with EAST then
            candidatState = RotorBladeState.Z_X_POS;

        return super.buildActualState(state, world, position, part).withProperty(Properties.ROTORBLADESTATE, candidatState);
    }

    private IBlockState[] _neighbors;
}
