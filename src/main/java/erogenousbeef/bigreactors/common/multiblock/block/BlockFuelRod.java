package erogenousbeef.bigreactors.common.multiblock.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;

public class BlockFuelRod extends Block {

	public static int renderId;

	// TODO blockstate
	/*
	@SideOnly(Side.CLIENT)
	private IIcon iconFuelRodSide;
	@SideOnly(Side.CLIENT)
	private IIcon iconFuelRodTopBottom;
	*/

	public BlockFuelRod(Material material) {
		super(material);
		
		setHardness(2f);
		setLightLevel(0.9f);
		setLightOpacity(1);
		setCreativeTab(BigReactors.TAB);
		setUnlocalizedName("yelloriumFuelRod");
		// TODO blockstate
		//setBlockTextureName(BigReactors.TEXTURE_NAME_PREFIX + "yelloriumFuelRod");
	}

	// TODO blockstate
	/*
	@Override
	public int getRenderType() {
		return renderId;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		if(side == 0 || side == 1) { return this.iconFuelRodTopBottom; }
		
		return this.iconFuelRodSide;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess iblockaccess, int x, int y, int z, int side) {
		if(side == 0 || side == 1) { return this.iconFuelRodTopBottom; }
		else { return this.iconFuelRodSide; }
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.iconFuelRodSide = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + "fuelRod.side");
		this.iconFuelRodTopBottom = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + "fuelRod.end");
	}
	*/

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	/**
	 * Called throughout the code as a replacement for block instanceof BlockContainer
	 * Moving this to the Block base class allows for mods that wish to extend vanilla
	 * blocks, and also want to have a tile entity on that block, may.
	 *
	 * Return true from this function to specify this block has a tile entity.
	 *
	 * @param state State of the current block
	 * @return True if block has a tile entity, false otherwise
	 */
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityReactorFuelRod();
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
		return false;
	}

	/*
	 * TODO Have to make my own particle for this. :/
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random par5Random)
    {
    	TileEntity te = world.getBlockTileEntity(x, y, z);
    	if(te instanceof TileEntityReactorFuelRod) {
    		TileEntityReactorFuelRod fuelRod = (TileEntityReactorFuelRod)te;
    		MultiblockReactor reactor = fuelRod.getReactorController();
    		if(reactor != null && reactor.isActive() && reactor.getFuelConsumedLastTick() > 0) {
    			int numParticles = par5Random.nextInt(4) + 1;
    			while(numParticles > 0) {
                    world.spawnParticle(BigReactors.isValentinesDay ? "heart" : "crit",
                    		fuelRod.xCoord + 0.5D,
                    		fuelRod.yCoord + 0.5D,
                    		fuelRod.zCoord + 0.5D,
                    		par5Random.nextFloat() * 3f - 1.5f,
                    		par5Random.nextFloat() * 3f - 1.5f,
                    		par5Random.nextFloat() * 3f - 1.5f);
    				numParticles--;
    			}
    		}
    	}
    }
     */
}
