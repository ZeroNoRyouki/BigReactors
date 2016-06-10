package erogenousbeef.bigreactors.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.common.BigReactors;

public class ItemBRBucket extends ItemBucket {

	private Block _fluid;
	
	public ItemBRBucket(Block fluid) {
		super(fluid);
		setCreativeTab(BigReactors.TAB);
		_fluid = fluid;
	}

	// TODO Commented out IIcon stuff
	/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName());
	}
	*/

	@Override
	public boolean tryPlaceContainedLiquid(EntityPlayer player, World world, BlockPos position) {
		if(_fluid == null) {
			return false;
		}

		IBlockState state = world.getBlockState(position);
		Block block = state.getBlock();

		if(!world.isAirBlock(position) && block.getMaterial(state).isSolid()) {
			return false;
		}
		else {
			// TODO Commented temporarily to allow this thing to compile...
			/*
			world.setBlock(x, y, z, _fluid, 0, 3);
			*/
			return true;
		}
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes) {
		subTypes.add(new ItemStack(item, 1, 0));
	}
}
