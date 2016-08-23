//TODO This is just a placeholder while we wait for MineFactory Reloaded to be available again

package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/*
@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = "MineFactoryReloaded")
})
*/
public class BlockReactorRedNetPort extends BlockMultiblockDevice /*implements IRedNetOmniNode*/ {

    public BlockReactorRedNetPort(String blockName) {
        super(PartType.ReactorRednetPort, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityReactorPart();
        //return new TileEntityReactorRedNetPort();
    }

    /* TODO but back recipe when MineFactory Reloaded is back
    if (Loader.isModLoaded("MineFactoryReloaded")) {
        GameRegistry.addRecipe(new ShapedOreRecipe(BrBlocks.reactorRedNetPort.createItemStack(), "CRC", "RGR", "CRC", 'C',
                "reactorCasing", 'R', "cableRedNet", 'G', "ingotGold"));
    }
    */


    /*
    // We do this to skip DISABLED
    @SideOnly(Side.CLIENT)
    public IIcon getRedNetConfigIcon(TileEntityReactorRedNetPort.CircuitType circuitType) {
        if(circuitType == TileEntityReactorRedNetPort.CircuitType.DISABLED) { return null; }
        else {
            return _redNetPortConfigIcons[circuitType.ordinal() - 1];
        }
    }

    // IConnectableRedNet
    @Optional.Method(modid = "MineFactoryReloaded")
    @Override
    public RedNetConnectionType getConnectionType(World world, int x, int y,
                                                  int z, ForgeDirection side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof TileEntityReactorRedNetPort) {
            return RedNetConnectionType.CableAll;
        }

        return RedNetConnectionType.None;
    }

    @Optional.Method(modid = "MineFactoryReloaded")
    @Override
    public int[] getOutputValues(World world, int x, int y, int z,
                                 ForgeDirection side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof TileEntityReactorRedNetPort) {
            return ((TileEntityReactorRedNetPort)te).getOutputValues();
        }
        else {
            int[] values = new int[16];
            for(int i = 0; i < 16; i++) {
                values[i] = 0;
            }
            return values;
        }
    }

    // Never used. we're always in "all" mode.
    @Optional.Method(modid = "MineFactoryReloaded")
    @Override
    public int getOutputValue(World world, int x, int y, int z,
                              ForgeDirection side, int subnet) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof TileEntityReactorRedNetPort) {
            return ((TileEntityReactorRedNetPort)te).getValueForChannel(subnet);
        }
        return 0;
    }

    @Optional.Method(modid = "MineFactoryReloaded")
    @Override
    public void onInputsChanged(World world, int x, int y, int z,
                                ForgeDirection side, int[] inputValues) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof TileEntityReactorRedNetPort) {
            ((TileEntityReactorRedNetPort)te).onInputValuesChanged(inputValues);
        }
    }

    // Never used, we're always in "all" mode.
    @Optional.Method(modid = "MineFactoryReloaded")
    @Override
    public void onInputChanged(World world, int x, int y, int z,
                               ForgeDirection side, int inputValue) {
        return;
    }

    private IIcon[] _redNetPortConfigIcons = new IIcon[TileEntityReactorRedNetPort.CircuitType.values().length - 1];
    */
}