package erogenousbeef.bigreactors.common.multiblock.block;

import java.util.Random;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
// TODO put back in when Minefactory Reloaded is available for MC 1.9.x
//import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
//import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
//import net.minecraftforge.fml.common.Optional;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorRedstonePort;
import zero.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import zero.mods.zerocore.util.WorldHelper;

// TODO put back in when Minefactory Reloaded is available for MC 1.9.x
/*
@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = "MineFactoryReloaded")	
})
*/
public class BlockReactorRedstonePort extends BlockReactorPart /* implements IRedNetOmniNode */ {

	public BlockReactorRedstonePort(String blockName) {
		super(PartType.ReactorRedstonePort, blockName);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityReactorRedstonePort();
	}

	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		/*
		if (WorldHelper.calledByLogicalServer(world) && this.hasTileEntity(state) && !player.isSneaking()) {

			TileEntity te = world.getTileEntity(pos);

			if (te instanceof TileEntityReactorRedstonePort) {

				TileEntityReactorRedstonePort part = (TileEntityReactorRedstonePort)te;

				if (part.isConnected())
					part.updateRedstoneState();
			}
		}
		*/
		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
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

			if (port.isLit() && (null != out)) {
				/*
				double particleX, particleY, particleZ;
				int x = position.getX(), z = position.getZ();

				particleY = position.getY() + 0.45D + rand.nextFloat() * 0.1D;
				particleX = out.getFrontOffsetX() > 0 ? x + rand.nextFloat() * 0.1D + 1.1D : x + 0.45D + rand.nextFloat() * 0.1D;
				particleZ = out.getFrontOffsetZ() > 0 ? z + rand.nextFloat() * 0.1D + 1.1D : z + 0.45D + rand.nextFloat() * 0.1D;

				world.spawnParticle(EnumParticleTypes.REDSTONE, particleX, particleY, particleZ, 0.0D, rand.nextFloat() * 0.1D, 0.0D);
				*/
				WorldHelper.spawnVanillaParticles(world, EnumParticleTypes.REDSTONE, 1, 1, position.getX(),
						position.getY(), position.getZ(), 0, 1, 0);
        	}
        }
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {

    	TileEntity te = world.getTileEntity(pos);
    	if(te instanceof TileEntityReactorRedstonePort) {
    		((TileEntityReactorRedstonePort)te).onNeighborBlockChange(world, pos, state, neighborBlock);
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