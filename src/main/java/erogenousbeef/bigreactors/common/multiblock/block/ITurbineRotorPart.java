package erogenousbeef.bigreactors.common.multiblock.block;

import net.minecraft.block.state.IBlockState;

public interface ITurbineRotorPart {

    int getMass(IBlockState blockState);

    boolean isShaft();

    boolean isBlade();
}
