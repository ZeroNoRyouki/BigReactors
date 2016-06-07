package erogenousbeef.bigreactors.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import erogenousbeef.bigreactors.common.BigReactors;

public class BlockBROre extends Block {
	// TODO blockstate
	//private IIcon iconYellorite;

	public BlockBROre()
	{
		super(Material.rock);
		this.setCreativeTab(BigReactors.TAB);
		this.setRegistryName("brOre");
		this.setUnlocalizedName("brOre");
		// TODO blockstate
		//this.setBlockTextureName(BigReactors.TEXTURE_NAME_PREFIX + "oreYellorite");
		this.setHardness(2f);
	}

	// TODO blockstate
	/*
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		return this.iconYellorite;
	}

	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.iconYellorite = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + "oreYellorite");
	}
	*/

	@Override
	public int damageDropped(IBlockState state) {
		// TODO blockstate
		return super.damageDropped(state);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(item, 1, 0));
	}
}
