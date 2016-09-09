package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.RotorBladeState;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorBlade;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockTurbineRotorBlade extends BlockTieredPart implements ITurbineRotorPart {

    public BlockTurbineRotorBlade(String blockName) {

        super(PartType.TurbineRotorBlade, blockName, Material.IRON);
        this.setLightLevel(0.9f);
        this._neighbors = new IBlockState[EnumFacing.VALUES.length];
    }

    @Override
    public void registerRecipes() {

        if (PartTier.TURBINE_TIERS.contains(PartTier.Legacy))
            GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 1), "CII",
                    'C', "ingotCyanite", 'I', "ingotIron"));

        if (PartTier.TURBINE_TIERS.contains(PartTier.Basic))
            GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 1), "CII",
                    'C', "ingotCyanite", 'I', "ingotIron"));
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTurbineRotorBlade();
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        // allow correct brightness of the rotor TESR
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullyOpaque(IBlockState state) {
        return false;
    }

    @Override
    public boolean isVisuallyOpaque() {
        return true;
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
        return this.buildActualStateInternal(state, world, position, part, false);
    }

    public IBlockState buildActualStateInternal(IBlockState state, IBlockAccess world, BlockPos position,
                                                   MultiblockTileEntityBase part, boolean buildingClientRotor) {

        final MultiblockTurbine turbine = part.isConnected() ? (MultiblockTurbine)part.getMultiblockController() : null;

        if (!buildingClientRotor && null != turbine && turbine.getActive())
            return super.buildActualState(state, world, position, part).withProperty(Properties.ROTORBLADESTATE, RotorBladeState.HIDDEN);

        RotorBladeState candidateState = null;
        final int neighborsSlotCount = this._neighbors.length;
        final BlockTurbineRotorShaft turbineRotorShaft = BrBlocks.turbineRotorShaft;

        for (int i = 0; i < neighborsSlotCount; ++i)
            this._neighbors[i] = null;

        // looking up for a rotor shaft

        for (EnumFacing facing: EnumFacing.VALUES) {

            final BlockPos neighborPos = position.offset(facing);
            IBlockState neighborState;

            this._neighbors[facing.getIndex()] = neighborState = world.getBlockState(neighborPos);

            if (turbineRotorShaft == neighborState.getBlock()) {

                // found a rotor shaft: orient the blade toward it

                neighborState = turbineRotorShaft.buildActualStateInternal(neighborState, world, neighborPos, part, buildingClientRotor);
                candidateState = RotorBladeState.from(neighborState.getValue(Properties.ROTORSHAFTSTATE), facing);
                break;
            }
        }

        if (null == candidateState) {

            // no rotor shaft found, let's search for other blades then

            for (EnumFacing facing: EnumFacing.VALUES) {

                final int index = facing.getIndex();
                IBlockState neighborState = this._neighbors[index];

                if (null == neighborState)
                    neighborState = world.getBlockState(position.offset(facing));

                if (this == neighborState.getBlock()) {

                    // found another blade, looking for a rotor shaft along this direction

                    BlockPos checkPos = position;
                    IBlockState checkState;
                    Block checkBlock;

                    while (true) {

                        checkPos = checkPos.offset(facing);
                        checkState = world.getBlockState(checkPos);
                        checkBlock = checkState.getBlock();

                        if (turbineRotorShaft == checkBlock) {

                            // found a rotor shaft
                            checkState = turbineRotorShaft.buildActualStateInternal(checkState, world, checkPos, part, buildingClientRotor);
                            candidateState = RotorBladeState.from(checkState.getValue(Properties.ROTORSHAFTSTATE), facing);
                            break;

                        } else if (this != checkBlock) {

                            // not a rotor shaft nor a blade... give up
                            break;
                        }
                    }

                    if (null != candidateState)
                        break;
                }
            }
        }

        if (null == candidateState)
            // still no joy? let's go with EAST then
            candidateState = RotorBladeState.Z_X_POS;

        return super.buildActualState(state, world, position, part).withProperty(Properties.ROTORBLADESTATE, candidateState);
    }

    private IBlockState[] _neighbors;
}
