package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorRedstonePort;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

// TODO put back in when Minefactory Reloaded is available for MC 1.9.x
/*
@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = "MineFactoryReloaded")	
})
*/
public class BlockReactorRedstonePort extends BlockMultiblockDevice /* implements IRedNetOmniNode */ {

	public BlockReactorRedstonePort(String blockName) {
		super(PartType.ReactorRedstonePort, blockName);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityReactorRedstonePort();
	}

	@Override
	public void registerRecipes() {

		if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
			GameRegistry.addRecipe(this.createItemStack(PartTier.Legacy, 1), "CRC", "RGR", "CRC",
				'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1), 'R', Items.REDSTONE, 'G', Items.GOLD_INGOT);

		if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
			GameRegistry.addRecipe(this.createItemStack(PartTier.Basic, 1), "CRC", "RGR", "CRC",
				'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1), 'R', Items.REDSTONE, 'G', Items.GOLD_INGOT);
	}

	/**
     * A randomly called display update to be able to add particles or other items for display
     */
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos position, Random rand) {

    	TileEntity te = world.getTileEntity(position);

		if (te instanceof TileEntityReactorRedstonePort) {

        	TileEntityReactorRedstonePort port = (TileEntityReactorRedstonePort)te;
			EnumFacing out = port.getOutwardFacing();

			if (port.isLit() && (null != out))
				WorldHelper.spawnVanillaParticles(world, EnumParticleTypes.REDSTONE, 1, 1, position.getX(),
						position.getY(), position.getZ(), 0, 1, 0);
        }
    }

    @Override
	public void neighborChanged(IBlockState stateAtPosition, World world, BlockPos position, Block neighbor) {

    	TileEntity te = world.getTileEntity(position);
    	if(te instanceof TileEntityReactorRedstonePort) {
    		((TileEntityReactorRedstonePort)te).onNeighborBlockChange(world, position, stateAtPosition, neighbor);
    	}
    }
    
	// Redstone API

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return this.getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {

		TileEntity te = blockAccess.getTileEntity(pos);

		return te instanceof TileEntityReactorRedstonePort ? ((TileEntityReactorRedstonePort)te).getWeakPower() : 0;
	}

    // TODO put back in when Minefactory Reloaded is available for MC 1.9.x
    /*
	// IRedNetOmniNode - for pretty cable connections
	@Optional.Method(modid = "MineFactoryReloaded")
	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y,
			int z, ForgeDirection side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityReactorRedstonePort) {
			TileEntityReactorRedstonePort port = (TileEntityReactorRedstonePort)te;
			if(port.isConnected()) {
				return RedNetConnectionType.CableSingle;
			}
		}
		return RedNetConnectionType.None;
	}

	@Optional.Method(modid = "MineFactoryReloaded")
	@Override
	public int[] getOutputValues(World world, int x, int y, int z,
			ForgeDirection side) {
		return null;
	}

	@Optional.Method(modid = "MineFactoryReloaded")
	@Override
	public int getOutputValue(World world, int x, int y, int z,
			ForgeDirection side, int subnet) {
		return isProvidingWeakPower(world, x, y, z, side.ordinal());
	}

	@Optional.Method(modid = "MineFactoryReloaded")
	@Override
	public void onInputsChanged(World world, int x, int y, int z,
			ForgeDirection side, int[] inputValues) {
		// Not used
	}

	@Optional.Method(modid = "MineFactoryReloaded")
	@Override
	public void onInputChanged(World world, int x, int y, int z,
			ForgeDirection side, int inputValue) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityReactorRedstonePort) {
			TileEntityReactorRedstonePort port = (TileEntityReactorRedstonePort)te;
			port.onRedNetUpdate(inputValue);
		}
	}
	*/

	@Override
	protected void buildBlockState(BlockStateContainer.Builder builder) {

		super.buildBlockState(builder);
		builder.add(Properties.LIT);
	}

	@Override
	protected IBlockState buildDefaultState(IBlockState state) {
		return super.buildDefaultState(state).withProperty(Properties.LIT, false);
	}

	@Override
	protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position, MultiblockTileEntityBase part) {
		return super.buildActualState(state, world, position, part).withProperty(Properties.LIT,
				(part instanceof TileEntityReactorRedstonePort) && ((TileEntityReactorRedstonePort)part).isLit());
	}
}