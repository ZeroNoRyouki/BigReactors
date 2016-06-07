package erogenousbeef.bigreactors.common.multiblock.block;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityReactorCreativeCoolantPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityTurbineCreativeSteamGenerator;
import erogenousbeef.bigreactors.utils.StaticUtils;
import zero.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;

public class BlockMBCreativePart extends Block {

	public static final int REACTOR_CREATIVE_COOLANT_PORT = 0;
	public static final int TURBINE_CREATIVE_FLUID_PORT = 1;
	
	private static String[] subBlocks = new String[] { "reactor.coolantPort", "turbine.fluidPort" };
	private static String[] subIconNames = new String[] { "reactor.coolantPort.outlet" };
	
	private static final int SUBICON_CREATIVE_COOLANT_OUTLET = 0;

	// TODO blockstate
	/*
	private IIcon[] icons = new IIcon[subBlocks.length];
	private IIcon[] subIcons = new IIcon[subIconNames.length];
	*/

	public BlockMBCreativePart(Material material) {
		super(material);

		setStepSound(SoundType.METAL);
		setHardness(1.0f);
		setRegistryName("blockMBCreativePart");
		setUnlocalizedName("blockMBCreativePart");
		// TODO blockstate
		//this.setBlockTextureName(BigReactors.TEXTURE_NAME_PREFIX + "blockMBCreativePart");
		setCreativeTab(BigReactors.TAB);
	}

	// TODO blockstate
	/*
	@Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		
		if(te instanceof RectangularMultiblockTileEntityBase) {
			RectangularMultiblockTileEntityBase rte = (RectangularMultiblockTileEntityBase)te;
			MultiblockControllerBase controller = rte.getMultiblockController();
			if(controller != null && controller.isAssembled()) {
				if(rte.getOutwardsDir().ordinal() == side) {
					return getIconFromTileEntity(rte, metadata);
				}
			}
		}
		
		return getIcon(side, metadata);
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		if(side == 0 || side == 1) { return blockIcon; }
		metadata = metadata % icons.length;
		return icons[metadata];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName());
		
		for(int i = 0; i < subBlocks.length; ++i) {
			icons[i] = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + "." + subBlocks[i]);
		}
		
		for(int i = 0; i < subIconNames.length; ++i) {
			subIcons[i] = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + "." + subIconNames[i]);
		}
	}
	*/

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
		// TODO blockstate
		return true; // fix!
	}

	// TODO blockstate + use createTileEntity(World world, IBlockState state)
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		switch(metadata) {
			case REACTOR_CREATIVE_COOLANT_PORT:
				return new TileEntityReactorCreativeCoolantPort();
			case TURBINE_CREATIVE_FLUID_PORT:
				return new TileEntityTurbineCreativeSteamGenerator();
			default:
				return null;
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		if(player.isSneaking()) {
			return false;
		}

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityReactorCreativeCoolantPort) {
			TileEntityReactorCreativeCoolantPort cp = (TileEntityReactorCreativeCoolantPort)te;
			if(heldItem == null || StaticUtils.Inventory.isPlayerHoldingWrench(player)) {
				// Use wrench to change inlet/outlet state
				cp.setInlet(!cp.isInlet(), true);
			}
			else {
				cp.forceAddWater();
			}
			return true;
		}
		
		return false;
	}

	@Override
	public int damageDropped(IBlockState state) {
		// TODO fix metadata
		// return metadata;
		return super.damageDropped(state);
	}

	public ItemStack getReactorCoolantPort() {
		return new ItemStack(this, 1, REACTOR_CREATIVE_COOLANT_PORT);
	}
	
	public ItemStack getTurbineFluidPort() {
		return new ItemStack(this, 1, TURBINE_CREATIVE_FLUID_PORT);
	}

	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(getReactorCoolantPort());
		par3List.add(getTurbineFluidPort());
	}

	// TODO textures
	/*
	private IIcon getIconFromTileEntity(RectangularMultiblockTileEntityBase rte, int metadata) {
		if(rte instanceof TileEntityReactorCreativeCoolantPort) {
			if(!((TileEntityReactorCreativeCoolantPort)rte).isInlet()) {
				return subIcons[SUBICON_CREATIVE_COOLANT_OUTLET];
			}
		}

		metadata = metadata % icons.length;
		return icons[metadata];
	}
	*/

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
		return false;
	}
}
