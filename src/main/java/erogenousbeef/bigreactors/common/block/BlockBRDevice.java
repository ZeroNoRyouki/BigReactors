package erogenousbeef.bigreactors.common.block;

import java.util.ArrayList;
import java.util.List;

import cofh.api.block.IDismantleable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.exception.ExceptionUtils;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import cofh.api.tileentity.IReconfigurableFacing;
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.interfaces.IBeefReconfigurableSides;
import erogenousbeef.bigreactors.common.interfaces.IWrenchable;
import erogenousbeef.bigreactors.common.tileentity.TileEntityCyaniteReprocessor;
import erogenousbeef.bigreactors.common.tileentity.base.TileEntityBeefBase;
import erogenousbeef.bigreactors.utils.StaticUtils;
import zero.mods.zerocore.util.WorldHelper;

public class BlockBRDevice extends Block implements IDismantleable {

	public static final int META_CYANITE_REPROCESSOR = 0;
	
	public static final String[] _subBlocks = {
		"cyaniteReprocessor"
	};

	/* TODO blockstate
	private IIcon[] _icons = new IIcon[_subBlocks.length];
	private IIcon[] _activeIcons = new IIcon[_subBlocks.length];
	*/
	
	public BlockBRDevice(Material material) {
		super(material);
		setStepSound(SoundType.METAL);
		setHardness(1.0f);
		setRegistryName("blockBRDevice");
		setUnlocalizedName("blockBRDevice");
		//TODO blockstate
		//setBlockTextureName(BigReactors.TEXTURE_NAME_PREFIX + "blockBRDevice");
		setCreativeTab(BigReactors.TAB);
	}

	// TODO blockstate?
	//public static final int SIDE_FRONT = ForgeDirection.NORTH.ordinal();

	/* TODO blockstate
	private IIcon safeGetIcon(IIcon[] list, int idx, int x, int y, int z) {
		if(idx < 0 || idx >= list.length) {
			BRLog.warning("Invalid metadata (%d) for block at %d, %d, %d!", idx, x, y, z);
			return blockIcon;
		}
		else {
			return list[idx];
		}
	}
	public IIcon getIconFromTileEntity(TileEntity te, int metadata, int side) {
		if(metadata < 0) { return blockIcon; }

		// Tracks the actual index of the current side, after rotation
		int front = -1;

		if(te instanceof IReconfigurableFacing) {
			IReconfigurableFacing teFacing = (IReconfigurableFacing)te;
			front = teFacing.getFacing();
		}
		
		if(side == front) {
			if(te instanceof TileEntityBeefBase) {
				TileEntityBeefBase beefTe = (TileEntityBeefBase)te;
				if(beefTe.isActive()) {
					return safeGetIcon(_activeIcons, metadata, te.xCoord, te.yCoord, te.zCoord);
				}
			}
			return safeGetIcon(_icons, metadata, te.xCoord, te.yCoord, te.zCoord);
		}
		
		if(te instanceof IBeefReconfigurableSides) {
			IBeefReconfigurableSides teSides = (IBeefReconfigurableSides)te;
			return teSides.getIconForSide(side);
		}

		return blockIcon;
	}
	
	@Override
	public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
	{
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		return this.getIconFromTileEntity(te, metadata, side);
	}
	
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		// This is used when rendering in-inventory. 4 == front here.
		if(side == 4) {
			return _icons[metadata];
		}
		return this.blockIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName());
		
		for(int i = 0; i < _subBlocks.length; ++i) {
			_icons[i] = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + "." + _subBlocks[i]);
			_activeIcons[i] = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + "." + _subBlocks[i] + ".active");
		}
	}
	*/

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		// TODO blockstate
		return null;
		/*
		switch(metadata) {
		case META_CYANITE_REPROCESSOR:
			return new TileEntityCyaniteReprocessor();
		default:
			throw new IllegalArgumentException("Unknown metadata for tile entity");
		}
		*/
	}

	public ItemStack getCyaniteReprocessorItemStack() {
		return new ItemStack(this, 1, META_CYANITE_REPROCESSOR);
	}
	
	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(this.getCyaniteReprocessorItemStack());
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer,
									EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntity te = world.getTileEntity(pos);
		if(te == null) { return false; }

		if(entityPlayer.isSneaking()) {

			// Wrench + Sneak = Dismantle
			if(StaticUtils.Inventory.isPlayerHoldingWrench(entityPlayer)) {
				// Pass simulate == true on the client to prevent creation of "ghost" item stacks
				dismantleBlock(entityPlayer, world, pos.getX(), pos.getY(), pos.getZ(), false);
				return true;
			}

			return false;
		}
		
		if(te instanceof IWrenchable && StaticUtils.Inventory.isPlayerHoldingWrench(entityPlayer)) {
			return ((IWrenchable)te).onWrench(entityPlayer, side);
		}

		// Handle buckets
		if(te instanceof IFluidHandler)
		{
			if(FluidContainerRegistry.isEmptyContainer(entityPlayer.inventory.getCurrentItem())) {
				IFluidHandler fluidHandler = (IFluidHandler)te;
				FluidTankInfo[] infoz = fluidHandler.getTankInfo(side);
				for(FluidTankInfo info : infoz) {
					if(StaticUtils.Fluids.fillContainerFromTank(world, fluidHandler, entityPlayer, info.fluid)) {
						return true;
					}
				}
			}
			else if(FluidContainerRegistry.isFilledContainer(entityPlayer.inventory.getCurrentItem()))
			{
				if(StaticUtils.Fluids.fillTankWithContainer(world, (IFluidHandler)te, entityPlayer)) {
					return true;
				}
			}
		}

		// Show GUI
		if(te instanceof TileEntityBeefBase) {
			if(!world.isRemote) {
				entityPlayer.openGui(BRLoader.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		
		return false;
	}

	// IDismantleable

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {

		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		BlockPos position = new BlockPos(x, y, z);
		IBlockState blockState = world.getBlockState(position);

		stacks.add(new ItemStack(getItemDropped(blockState, world.rand, 0), 1, damageDropped(blockState)));
		
		if(returnDrops) {
			TileEntity te = world.getTileEntity(position);
			
			if(te instanceof IInventory) {
				IInventory invTe = (IInventory)te;
				for(int i = 0; i < invTe.getSizeInventory(); i++) {
					ItemStack stack = invTe.getStackInSlot(i);
					if(stack != null) {
						stacks.add(stack);
						invTe.setInventorySlotContents(i, null);
					}
				}
			}
		}

		world.setBlockToAir(position);

		if(!returnDrops) {
			for(ItemStack stack: stacks) {
				WorldHelper.spawnItemStack(stack, world, x, y, z, true);
			}
		}

		return stacks;
	}
}
