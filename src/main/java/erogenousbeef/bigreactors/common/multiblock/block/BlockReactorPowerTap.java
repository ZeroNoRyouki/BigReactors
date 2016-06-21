package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPowerTap;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockReactorPowerTap extends BlockReactorPart {

    public BlockReactorPowerTap(String blockName) {

        super(PartType.ReactorPowerTap, blockName);
        this.setDefaultState(
                this.blockState.getBaseState().withProperty(BlockReactorPowerTap.POWERTAPSTATE, PowerTapState.Disconnected)
        );
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        return new TileEntityReactorPowerTap();
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(BlockReactorPowerTap.POWERTAPSTATE);
    }

    private enum PowerTapState implements IStringSerializable {

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

    private static final PropertyEnum<PowerTapState> POWERTAPSTATE = PropertyEnum.<PowerTapState>create("powerstate", PowerTapState.class);
}
