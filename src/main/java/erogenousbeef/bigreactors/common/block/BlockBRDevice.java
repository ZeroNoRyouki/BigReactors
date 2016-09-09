package erogenousbeef.bigreactors.common.block;

import cofh.api.block.IDismantleable;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.interfaces.IWrenchable;
import erogenousbeef.bigreactors.common.tileentity.TileEntityCyaniteReprocessor;
import erogenousbeef.bigreactors.common.tileentity.base.TileEntityBeefBase;
import erogenousbeef.bigreactors.utils.StaticUtils;
import it.zerono.mods.zerocore.lib.block.properties.Orientation;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

public class BlockBRDevice extends BlockBR implements IDismantleable {

	public static final int META_CYANITE_REPROCESSOR = 0;

	public BlockBRDevice(DeviceType type, String blockName) {

		super(blockName, Material.IRON);
		this._type = type;
		this.setSoundType(SoundType.METAL);
		this.setHardness(1.0f);
		this.setDefaultState(
				this.blockState.getBaseState()
						.withProperty(BlockBRDevice.ACTIVE, false)
						.withProperty(Orientation.HFACING, EnumFacing.NORTH)
		);
	}

	@Override
	public void onPostRegister() {

		super.onPostRegister();

		String name = this._type.oreDictionaryName;

		if (name.length() > 0)
			OreDictionary.registerOre(name, this.createItemStack());
	}

	@Override
	public void onPostClientRegister() {

		Item item = Item.getItemFromBlock(this);

		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {

		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (EnumFacing.Axis.Y == enumfacing.getAxis())
			enumfacing = EnumFacing.NORTH;

		return this.getDefaultState().withProperty(Orientation.HFACING, enumfacing);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(Orientation.HFACING)).getIndex();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos position) {

		TileEntity te = world.getTileEntity(position);

		if (te instanceof TileEntityBeefBase)
			state = state.withProperty(ACTIVE, ((TileEntityBeefBase)te).isActive());

		return state;
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
									 int meta, EntityLivingBase placer) {

		facing = (null != placer) ? placer.getHorizontalFacing().getOpposite() : EnumFacing.NORTH;

		return this.getDefaultState().withProperty(Orientation.HFACING, facing);
	}

	@Override
	public void onBlockAdded(World world, BlockPos position, IBlockState state) {

		EnumFacing newFacing = Orientation.suggestDefaultHorizontalFacing(world, position,
				state.getValue(Orientation.HFACING));

		world.setBlockState(position, state.withProperty(Orientation.HFACING, newFacing), 2);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		switch (this._type) {

			case CyaniteReprocessor:
				return new TileEntityCyaniteReprocessor();

			default:
				throw new IllegalArgumentException("Unknown device type for tile entity");
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer,
									EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntity te = world.getTileEntity(pos);
		if(te == null) { return false; }

		if(entityPlayer.isSneaking()) {

			// Wrench + Sneak = Dismantle
			if(StaticUtils.Inventory.isPlayerHoldingWrench(heldItem)) {
				// Pass simulate == true on the client to prevent creation of "ghost" item stacks
				dismantleBlock(entityPlayer, world, pos, false);
				return true;
			}

			return false;
		}
		
		if(te instanceof IWrenchable && StaticUtils.Inventory.isPlayerHoldingWrench(heldItem)) {
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
				entityPlayer.openGui(BigReactors.getInstance(), 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		
		return false;
	}

	// IDismantleable

	@Override
	public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos position, boolean returnDrops) {

		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
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

			int x = position.getX(), y = position.getY(), z = position.getZ();

			for(ItemStack stack: stacks) {
				WorldHelper.spawnItemStack(stack, world, x, y, z, true);
			}
		}

		return stacks;
	}

	@Override
	protected void buildBlockState(BlockStateContainer.Builder builder) {

		super.buildBlockState(builder);
		builder.add(BlockBRDevice.ACTIVE);
		builder.add(Orientation.HFACING);
	}

	private static final PropertyBool ACTIVE = PropertyBool.create("active");

	private DeviceType _type;
}
