package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorPart;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockTurbineRotorPart extends Block {

	public static final int METADATA_SHAFT = 0;
	public static final int METADATA_BLADE = 1;
	public static int renderId;

	private static final String[] _subBlocks = new String[] { "rotor",
															  "blade",
															};

	// TODO blockstate
	/*
	private IIcon[] _icons = new IIcon[_subBlocks.length];
	private IIcon[] _subIcons = new IIcon[1];
	*/

	public BlockTurbineRotorPart(Material material) {
		super(material);

		this.setSoundType(SoundType.METAL);
		setLightLevel(0.9f);
		setHardness(2.0f);
		//setRegistryName("blockTurbineRotorPart");
		setUnlocalizedName("blockTurbineRotorPart");
		// TODO blockstate
		//this.setBlockTextureName(BigReactors.TEXTURE_NAME_PREFIX + "blockTurbineRotorPart");
		setCreativeTab(BigReactors.TAB);
	}

	// TODO blockstate
	/*
	@Override
	public int getRenderType() {
		return renderId;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		// Base icons
		for(int i = 0; i < _subBlocks.length; ++i) {
			_icons[i] = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + "." + _subBlocks[i]);
		}
		
		_subIcons[0] = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + ".rotor.connector");
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		return _icons[metadata];
	}

	public IIcon getRotorConnectorIcon() {
		return _subIcons[0];
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
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityTurbineRotorPart();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	// TODO Commented temporarily to allow this thing to compile...
	/*
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	*/

	@Override
	public int damageDropped(IBlockState state) {
		// TODO fix metadata
		// return metadata;
		return super.damageDropped(state);
	}
	
	public ItemStack getItemStack(String name) {
		int metadata = -1;
		for(int i = 0; i < _subBlocks.length; i++) {
			if(_subBlocks[i].equals(name)) {
				metadata = i;
				break;
			}
		}
		
		if(metadata < 0) {
			throw new IllegalArgumentException("Unable to find a block with the name " + name);
		}
		
		return new ItemStack(this, 1, metadata);
	}
	
	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for(int i = 0; i < _subBlocks.length; i++) {
			par3List.add(new ItemStack(this, 1, i));
		}
	}
	
	public int getRotorMass(Block block, int metadata) {
		if(this == block) {
			switch(metadata) {
			// TODO: add masses when you add non-standard turbine parts
			default:
				return 10;
			}
		}
		
		return 0;
	}
	
	public static boolean isRotorBlade(int metadata) {
		return metadata == METADATA_BLADE;
	}
	
	public static boolean isRotorShaft(int metadata) {
		return metadata == METADATA_SHAFT;
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
		return false;
	}
}
