package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPart;

/*	TODO put back in when ComputerCraft is available for MC 1.9.x
	TODO put back in when MineFactory Reloaded is available for MC 1.9.x
@Optional.InterfaceList({
	@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = "ComputerCraft"),
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = "MineFactoryReloaded")	
})
*/
public class BlockReactorPart extends BlockMultiblockDevice /*implements IRedNetOmniNode, IPeripheralProvider*/ {

	//private IIcon[] _redNetPortConfigIcons = new IIcon[TileEntityReactorRedNetPort.CircuitType.values().length - 1];
	
	public BlockReactorPart(PartType type, String blockName) {

		super(type, blockName);
	}

	// TODO blockstate
	/*
	// We do this to skip DISABLED
	@SideOnly(Side.CLIENT)
	public IIcon getRedNetConfigIcon(TileEntityReactorRedNetPort.CircuitType circuitType) {
		if(circuitType == TileEntityReactorRedNetPort.CircuitType.DISABLED) { return null; }
		else {
			return _redNetPortConfigIcons[circuitType.ordinal() - 1];
		}
	}
	*/

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		switch (this._type) {
			case ReactorRednetPort:
				//return new TileEntityReactorRedNetPort();
				return new TileEntityReactorPart(); // TODO fix when Minefactory Reloaded is back

			case ReactorComputerPort:
				//return new TileEntityReactorComputerPort();
				return new TileEntityReactorPart(); // TODO fix when ComputerCraft is back

			default:
				throw new IllegalArgumentException("Unrecognized part");
		}
	}

	/* TODO put back in when MineFactory Reloaded is available for MC 1.9.x
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
	*/

	/* TODO put back in when ComputerCraft is available for MC 1.9.x
	// IPeripheralProvider
	@Optional.Method(modid ="ComputerCraft")
	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		
		if(te instanceof TileEntityReactorComputerPort)
			return (IPeripheral)te;
		
		return null;
	}
	*/
}
