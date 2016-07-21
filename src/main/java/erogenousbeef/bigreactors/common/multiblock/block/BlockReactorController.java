package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityController;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;

public class BlockReactorController extends BlockReactorPart {

    public BlockReactorController(String blockName) {

        super(PartType.ReactorController, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        return new TileEntityController();
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(Properties.CONTROLLERSTATE);
    }

    @Override
    protected IBlockState buildDefaultState(IBlockState state) {

        return super.buildDefaultState(state).withProperty(Properties.CONTROLLERSTATE, ControllerState.Off);
    }

    @Override
    protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position, MultiblockTileEntityBase part) {

        state = super.buildActualState(state, world, position, part);

        if (part instanceof TileEntityController) {

            MultiblockReactor reactor = ((TileEntityController)part).getReactorController();
            ControllerState controllerState = null == reactor || !reactor.isAssembled() ? ControllerState.Off :
                    reactor.getActive() ? ControllerState.Active : ControllerState.Idle;

            state = state.withProperty(Properties.CONTROLLERSTATE, controllerState);
        }

        return state;
    }

    public enum ControllerState implements IStringSerializable {

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
}
