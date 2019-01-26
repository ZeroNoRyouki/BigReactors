package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorController;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineController;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

public class BlockMultiblockController extends BlockMultiblockDevice {

    public BlockMultiblockController(PartType type, String blockName) {
        super(type, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        switch (this._type) {

            case ReactorController:
                return new TileEntityReactorController();

            case TurbineController:
                return new TileEntityTurbineController();

            default:
                throw new IllegalArgumentException("Invalid part type");
        }
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
    protected IBlockState buildActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos position,
                                           @Nonnull MultiblockTileEntityBase part) {

        MultiblockControllerBase controller = part.getMultiblockController();
        ControllerState controllerState = null == controller || !controller.isAssembled() || !(controller instanceof IActivateable) ?
                ControllerState.Off : ((IActivateable)controller).getActive() ? ControllerState.Active : ControllerState.Idle;

        return super.buildActualState(state, world, position, part).withProperty(Properties.CONTROLLERSTATE, controllerState);
    }
}
