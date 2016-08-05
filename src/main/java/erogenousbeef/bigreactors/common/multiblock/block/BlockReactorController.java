package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorController;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockReactorController extends BlockMultiblockDevice {

    public BlockReactorController(String blockName) {

        super(PartType.ReactorController, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        return new TileEntityReactorController();
    }

    @Override
    public void registerRecipes() {

        if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
            GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 1), "C C", "GDG", "CRC",
                'D', Items.DIAMOND, 'G', BigReactors.CONFIG.recipeYelloriumIngotName,
                'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1), 'R', Items.REDSTONE));

        if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
            GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 1), "C C", "GDG", "CRC",
                'D', Items.DIAMOND, 'G', BigReactors.CONFIG.recipeYelloriumIngotName,
                'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1), 'R', Items.REDSTONE));
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

        if (part instanceof TileEntityReactorController) {

            MultiblockReactor reactor = ((TileEntityReactorController)part).getReactorController();
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
