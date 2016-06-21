package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityController;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPart;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockReactorController extends BlockReactorPart {

    public BlockReactorController(String blockName) {

        super(PartType.ReactorController, blockName);
        this.setDefaultState(
                this.blockState.getBaseState().withProperty(BlockReactorController.CONTROLLERSTATE, ControllerState.Off)
        );
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        return new TileEntityController();
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(BlockReactorController.CONTROLLERSTATE);
    }

    private enum ControllerState implements IStringSerializable {

        Off,
        Idle,
        Active;

        ControllerState() {

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

    private static final PropertyEnum<ControllerState> CONTROLLERSTATE = PropertyEnum.<ControllerState>create("controller", ControllerState.class);
}
