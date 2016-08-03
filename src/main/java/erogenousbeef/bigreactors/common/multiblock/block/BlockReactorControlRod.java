package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockReactorControlRod extends BlockMultiblockDevice {

    public BlockReactorControlRod(String blockName) {

        super(PartType.ReactorControlRod, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityReactorControlRod();
    }
}
