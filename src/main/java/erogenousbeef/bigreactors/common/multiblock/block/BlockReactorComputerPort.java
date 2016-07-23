package erogenousbeef.bigreactors.common.multiblock.block;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorComputerPort;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = "ComputerCraft")
})
public class BlockReactorComputerPort extends BlockMultiblockDevice implements IPeripheralProvider {

    public BlockReactorComputerPort(String blockName) {
        super(PartType.ReactorComputerPort, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityReactorComputerPort();
    }

    @Optional.Method(modid ="ComputerCraft")
    @Override
    public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {

        TileEntity tileEntity = world.getTileEntity(pos);

        return tileEntity instanceof IPeripheral ? (IPeripheral)tileEntity : null;
    }
}
