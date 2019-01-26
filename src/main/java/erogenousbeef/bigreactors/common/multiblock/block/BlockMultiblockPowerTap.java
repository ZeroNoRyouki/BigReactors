package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.IPowerProvider;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPowerTapRedstoneFlux;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPowerTapTesla;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePowerTapRedstoneFlux;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePowerTapTesla;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

public class BlockMultiblockPowerTap extends BlockMultiblockDevice {

    public BlockMultiblockPowerTap(PartType type, String blockName, PowerSystem powerSystem) {

        super(type, blockName);
        this._powerSystem = powerSystem;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        final boolean isRF = PowerSystem.RedstoneFlux == this._powerSystem;

        switch (this._type) {

            case ReactorPowerTap:
                return isRF ? new TileEntityReactorPowerTapRedstoneFlux() : new TileEntityReactorPowerTapTesla();

            case TurbinePowerPort:
                return isRF ? new TileEntityTurbinePowerTapRedstoneFlux() : new TileEntityTurbinePowerTapTesla();

            default:
                throw new IllegalArgumentException("Invalid part type");
        }
    }

    /**
     * Called when a tile entity on a side of this block changes is created or is destroyed.
     * @param world The world
     * @param position Block position in world
     * @param neighbor Block position of neighbor
     */
    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos position, BlockPos neighbor) {

        TileEntity te = world.getTileEntity(position);

        // Signal power taps and other ports when their neighbors change, etc.
        if (te instanceof INeighborUpdatableEntity)
            ((INeighborUpdatableEntity)te).onNeighborTileChange(world, position, neighbor);
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    @Override
    public void neighborChanged(IBlockState stateAtPosition, World world, BlockPos position, Block neighbor, BlockPos neighborPos) {

        TileEntity te = world.getTileEntity(position);

        // Signal power taps when their neighbors change, etc.
        if (te instanceof INeighborUpdatableEntity)
            ((INeighborUpdatableEntity)te).onNeighborBlockChange(world, position, stateAtPosition, neighbor);
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(Properties.POWERTAPSTATE);
    }

    @Override
    protected IBlockState buildDefaultState(IBlockState state) {
        return super.buildDefaultState(state).withProperty(Properties.POWERTAPSTATE, PowerTapState.Disconnected);
    }

    @Override
    protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position, MultiblockTileEntityBase part) {

        boolean connected = (part instanceof IPowerProvider) && ((IPowerProvider)part).isProviderConnected();

        return super.buildActualState(state, world, position, part).withProperty(Properties.POWERTAPSTATE,
                connected ? PowerTapState.Connected : PowerTapState.Disconnected);
    }

    private PowerSystem _powerSystem;
}
