package erogenousbeef.bigreactors.common.multiblock.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
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
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorRedstonePort;

// TODO put back in when Minefactory Reloaded is available for MC 1.9.x
/*
@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = "MineFactoryReloaded")	
})
*/
public class BlockReactorRedstonePort extends Block /* implements IRedNetOmniNode */ {

	// TODO blockstate
	//protected IIcon blockIconLit;
	
	public static final int META_REDSTONE_LIT = 1;
	public static final int META_REDSTONE_UNLIT = 0;
	
	protected final static int REDSTONE_VALUE_OFF = 0;  // corresponds to no power
	protected final static int REDSTONE_VALUE_ON  = 15; // corresponds to strong power
	
	public BlockReactorRedstonePort(Material material) {
		super(material);
		
		setStepSound(SoundType.METAL);
		setHardness(2.0f);
        //setRegistryName("blockReactorRedstonePort");
        setUnlocalizedName("blockReactorRedstonePort");
		// TODO textures
        //this.setBlockTextureName(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName());
		setCreativeTab(BigReactors.TAB);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityReactorRedstonePort();
	}

    /* TODO blockstate
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		if(side == 0 || side == 1) { return BigReactors.blockReactorPart.getIcon(side, BlockReactorPart.METADATA_CASING); }

		if(metadata == META_REDSTONE_LIT) { return blockIconLit; }
		else {
			return blockIcon;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + ".unlit");
		this.blockIconLit = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + ".lit");
	}
	*/

    // TODO blockstate
	/* TODO Commented temporarily to allow this thing to compile...
	@Override
	public int damageDropped(int metadata)
	{
		return META_REDSTONE_UNLIT;
	}
	*/

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		if(player.isSneaking()) {
			return false;
		}

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityReactorRedstonePort) {
			if(!((TileEntityReactorRedstonePort)te).isConnected()) { return false; }
			
			if(!world.isRemote)
				((TileEntityReactorRedstonePort)te).sendRedstoneUpdate();

			if(!world.isRemote) {
				player.openGui(BRLoader.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}

		return false;
	}

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
	/* TODO Commented temporarily to allow this thing to compile...
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos position, Random rand) {

    	TileEntity te = world.getTileEntity(position);
        if (te instanceof TileEntityReactorRedstonePort)
        {
        	TileEntityReactorRedstonePort port = (TileEntityReactorRedstonePort)te;
        	if(port.isRedstoneActive()) {
                ForgeDirection out = port.getOutwardsDir();
                
                if(out != ForgeDirection.UNKNOWN) {
                    double particleX, particleY, particleZ;
                    particleY = y + 0.45D + rand.nextFloat() * 0.1D;

                    if(out.offsetX > 0)
                    	particleX = x + rand.nextFloat() * 0.1D + 1.1D;
                    else
                    	particleX = x + 0.45D + rand.nextFloat() * 0.1D;
                    
                    if(out.offsetZ > 0)
                    	particleZ = z + rand.nextFloat() * 0.1D + 1.1D;
                    else
                    	particleZ = z + 0.45D + rand.nextFloat() * 0.1D;

                    world.spawnParticle(EnumParticleTypes.REDSTONE, particleX, particleY, particleZ, 0.0D, rand.nextFloat() * 0.1D, 0.0D);
                }
        	}
        }
    }
	*/

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
    	super.onNeighborBlockChange(world, pos, state, neighborBlock);

    	TileEntity te = world.getTileEntity(pos);
    	if(te instanceof TileEntityReactorRedstonePort) {
    		((TileEntityReactorRedstonePort)te).onNeighborBlockChange(world, pos, state, neighborBlock);
    	}
    }
    
	// Redstone API
	/* TODO Commented temporarily to allow this thing to compile...
    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		if(side == 0 || side == 1) { return REDSTONE_VALUE_OFF; }

		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityReactorRedstonePort) {
			TileEntityReactorRedstonePort port = (TileEntityReactorRedstonePort)te;
			if(port.isOutput())
				return port.isRedstoneActive() ? REDSTONE_VALUE_ON : REDSTONE_VALUE_OFF;
			else
				return REDSTONE_VALUE_OFF;
		}
		
		return REDSTONE_VALUE_OFF;
	}
	*/

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
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }
}