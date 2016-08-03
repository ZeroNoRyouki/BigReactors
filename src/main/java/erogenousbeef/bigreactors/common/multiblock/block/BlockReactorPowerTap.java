package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPowerTap;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPowerTapRedstoneFlux;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPowerTapTesla;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReactorPowerTap extends BlockMultiblockDevice {

    public BlockReactorPowerTap(String blockName, PowerSystem powerSystem) {

        super(PartType.ReactorPowerTap, blockName);
        this._powerSystem = powerSystem;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        switch (this._powerSystem) {

            case RedstoneFlux:
                return new TileEntityReactorPowerTapRedstoneFlux();

            case Tesla:
                return new TileEntityReactorPowerTapTesla();

            default:
                throw new IllegalArgumentException("Unrecognized power system");
        }
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

        state = super.buildActualState(state, world, position, part);

        if (part instanceof TileEntityReactorPowerTap)
            state = state.withProperty(Properties.POWERTAPSTATE,((TileEntityReactorPowerTap)part).hasEnergyConnection() ?
                    PowerTapState.Connected : PowerTapState.Disconnected);

        return state;
    }

    protected PowerSystem _powerSystem;

    public enum PowerTapState implements IStringSerializable {

        Connected,
        Disconnected;

        PowerTapState() {

            this._name = this.name().toLowerCase();
        }

        @Override
        public String toString() {

            return this._name;
        }

        @Override
        public String getName() {

            return this._name;
        }

        private final String _name;
    }
}
