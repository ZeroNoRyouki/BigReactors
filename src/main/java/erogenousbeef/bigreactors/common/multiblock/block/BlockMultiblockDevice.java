package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

public class BlockMultiblockDevice extends BlockTieredPart {

    public BlockMultiblockDevice(PartType type, String blockName) {

        super(type, blockName, Material.IRON);
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(Properties.PARTSTATE);
    }

    @Override
    protected IBlockState buildDefaultState(IBlockState state) {

        return super.buildDefaultState(state).withProperty(Properties.PARTSTATE, MachinePartState.Disassembled);
    }

    @Override
    protected IBlockState buildActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos position,
                                           @Nonnull MultiblockTileEntityBase part) {

        state = super.buildActualState(state, world, position, part);

        if (part instanceof RectangularMultiblockTileEntityBase) {

            boolean assembled = part.isConnected() && part.getMultiblockController().isAssembled();
            MachinePartState partState = assembled ?
                    MachinePartState.from(((RectangularMultiblockTileEntityBase)part).getPartPosition()) : MachinePartState.Disassembled;

            state = state.withProperty(Properties.PARTSTATE, partState);
        }

        return state;
    }
}
