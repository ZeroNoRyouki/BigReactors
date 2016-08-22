package erogenousbeef.bigreactors.common.multiblock.block;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.*;
import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@Optional.InterfaceList({
	@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = "ComputerCraft"),
})
@Deprecated
public class BlockTurbinePart extends BlockMultiblockDevice implements IPeripheralProvider {

	public static final int METADATA_HOUSING = 0;
	public static final int METADATA_CONTROLLER = 1;
	public static final int METADATA_POWERTAP = 2;
	public static final int METADATA_FLUIDPORT = 3;
	public static final int METADATA_BEARING = 4;
	public static final int METADATA_COMPUTERPORT = 5;
	
	private static final String[] _subBlocks = new String[] { "housing",
														"controller",
														"powerTap",
														"fluidPort",
														"bearing",
														"computerPort" };

	// Additional non-metadata-based icons
	private static final int SUBICON_NONE = -1;
	private static final int SUBICON_HOUSING_FRAME_TOP = 0;
	private static final int SUBICON_HOUSING_FRAME_BOTTOM = 1;
	private static final int SUBICON_HOUSING_FRAME_LEFT = 2;
	private static final int SUBICON_HOUSING_FRAME_RIGHT = 3;
	private static final int SUBICON_HOUSING_FACE = 4;
	private static final int SUBICON_HOUSING_CORNER = 5;
	private static final int SUBICON_CONTROLLER_IDLE = 6;
	private static final int SUBICON_CONTROLLER_ACTIVE = 7;
	private static final int SUBICON_POWERTAP_ACTIVE = 8;
	private static final int SUBICON_FLUIDPORT_OUTPUT = 9;

	private static final String[] _subIconNames = new String[] {
		"housing.edge.0",
		"housing.edge.1",
		"housing.edge.2",
		"housing.edge.3",
		"housing.face",
		"housing.corner",
		"controller.idle",
		"controller.active",
		"powerTap.connected",
		"fluidPort.outlet"
	};

	/* TODO Commented out IIcon stuff
	private IIcon[] _icons = new IIcon[_subBlocks.length];
	private IIcon[] _subIcons = new IIcon[_subIconNames.length];
	*/

	public BlockTurbinePart(PartType type, String blockName) {

		super(type, blockName);
	}

	/* TODO Commented out IIcon stuff
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		// Base icons
		for(int i = 0; i < _subBlocks.length; ++i) {
			_icons[i] = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + "." + _subBlocks[i]);
		}
		
		for(int i = 0; i < _subIcons.length; i++) {
			_subIcons[i] = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + "." + _subIconNames[i]);
		}
		
		this.blockIcon = _icons[0];
	}

	@Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		int metadata = blockAccess.getBlockMetadata(x,y,z);

		if(te instanceof TileEntityTurbinePartBase) {
			TileEntityTurbinePartBase part = (TileEntityTurbinePartBase)te;
			MultiblockTurbine turbine = part.getTurbine();
			
			if(metadata == METADATA_FLUIDPORT) {
				if(te instanceof TileEntityTurbineFluidPort) {
					if(turbine == null || !turbine.isAssembled() || part.getOutwardsDir().ordinal() == side)
					{
						if(((TileEntityTurbineFluidPort)te).getFlowDirection() == FluidFlow.Out)
							return _subIcons[SUBICON_FLUIDPORT_OUTPUT];
						else
							return _icons[METADATA_FLUIDPORT];
					}
					else if(turbine.isAssembled() && part.getOutwardsDir().ordinal() != side)
						return _subIcons[SUBICON_HOUSING_FACE];
				}
				return getIcon(side, metadata);
			}
			else if(!part.isConnected() || turbine == null || !turbine.isAssembled()) {
				return getIcon(side, metadata);
			}
			else {
				int subIcon = SUBICON_NONE;
				if(metadata == METADATA_HOUSING) {
					subIcon = getSubIconForHousing(blockAccess, x, y, z, turbine, side);
				}
				else if(part.getOutwardsDir().ordinal() == side) {
					// Only put the fancy icon on one side of the machine. Other parts will use the base.
					if(metadata == METADATA_CONTROLLER) {
						if(turbine.getActive()) {
							subIcon = SUBICON_CONTROLLER_ACTIVE;
						}
						else {
							subIcon = SUBICON_CONTROLLER_IDLE;
						}
					}
					else if(metadata == METADATA_POWERTAP) {
						if(te instanceof TileEntityTurbinePowerTap && ((TileEntityTurbinePowerTap)te).isAttachedToPowerNetwork()) {
							subIcon = SUBICON_POWERTAP_ACTIVE;
						}
					}
				}
				else {
					// Assembled non-housing parts use the face texture so it's all smooth on the inside
					subIcon = SUBICON_HOUSING_FACE;
				}
				
				if(subIcon == SUBICON_NONE) {
					return getIcon(side, metadata);
				}
				else {
					return _subIcons[subIcon];
				}
			}
		}

		// Not a "proper" TE, so just pass through
		return getIcon(side, metadata);
	}
	
	private int getSubIconForHousing(IBlockAccess blockAccess, int x, int y, int z, MultiblockTurbine turbine, int side) {
		BlockPos minCoord, maxCoord;
		minCoord = turbine.getMinimumCoord();
		maxCoord = turbine.getMaximumCoord();
		
		if(minCoord == null || maxCoord == null) {
			return SUBICON_NONE;
		}
		
		int extremes = 0;

		if(x == minCoord.getX()) { extremes++; }
		if(y == minCoord.getY()) { extremes++; }
		if(z == minCoord.getZ()) { extremes++; }
		
		if(x == maxCoord.getX()) { extremes++; }
		if(y == maxCoord.getY()) { extremes++; }
		if(z == maxCoord.getZ()) { extremes++; }

		if(extremes >= 3) {
			return SUBICON_HOUSING_CORNER;
		}
		else if(extremes <= 0) {
			return SUBICON_NONE;
		}
		else if(extremes == 1) {
			return SUBICON_HOUSING_FACE;
		}
		else {
			ForgeDirection[] dirsToCheck = StaticUtils.neighborsBySide[side];
			ForgeDirection dir;

			Block myBlock = blockAccess.getBlock(x, y, z);
			int iconIdx = -1;

			for(int i = 0; i < dirsToCheck.length; i++) {
				dir = dirsToCheck[i];
				
				Block neighborBlock = blockAccess.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
				// See if we're a turbine part
				if(neighborBlock != myBlock && neighborBlock != BigReactors.blockMultiblockGlass
						&& (BigReactors.blockMultiblockCreativePart != null && neighborBlock != BigReactors.blockMultiblockCreativePart)) {
					// One of these things is not like the others...
					iconIdx = i;
					break;
				}
			}
			
			return iconIdx + SUBICON_HOUSING_FRAME_TOP;
		}
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		metadata = Math.max(0, Math.min(metadata, _subBlocks.length-1));
		return _icons[metadata];
	}
	*/
	/*
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		if(metadata == METADATA_POWERTAP) {
			return new TileEntityTurbinePowerTap();
		}
		else if(metadata == METADATA_FLUIDPORT) {
			return new TileEntityTurbineFluidPort();
		}
		else if(metadata == METADATA_BEARING) {
			// Does jack-all different except for store display lists on the client
			return new TileEntityTurbineRotorBearing();
		}
		else if(metadata == METADATA_COMPUTERPORT) {
			return new TileEntityTurbineComputerPort();
		}
		else {
			return new TileEntityTurbinePart();
		}
	}*/
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		switch (this._type) {
			/*
			case TurbinePowerPort:
				return new TileEntityTurbinePowerTap();

			case TurbineFluidPort:
				return new TileEntityTurbineFluidPort();
		*/
			case TurbineRotorBearing:
				return new TileEntityTurbineRotorBearing();
		/*
			case TurbineComputerPort:
				return new TileEntityTurbineComputerPort();

			case TurbineController:
				return new TileEntityTurbinePart();
		*/
			default:
				throw new IllegalArgumentException("Unrecognized part");
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		if(player.isSneaking()) {
			return false;
		}

		// TODO Commented temporarily to allow this thing to compile...
		int metadata = -1;/*world.getBlockMetadata(x, y, z);*/
		/*
		if(metadata == METADATA_FLUIDPORT && StaticUtils.Inventory.isPlayerHoldingWrench(player)) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileEntityTurbineFluidPort) {
				TileEntityTurbineFluidPort fluidPort = (TileEntityTurbineFluidPort)te; 
				FluidFlow flow = fluidPort.getFlowDirection();
				fluidPort.setFluidFlowDirection(flow == FluidFlow.In ? FluidFlow.Out : FluidFlow.In, true);
				return true;
			}
		}*/
		
		if(world.isRemote) {
			return true;
		}
		
		// If the player's hands are empty and they rightclick on a multiblock, they get a 
		// multiblock-debugging message if the machine is not assembled.
		/*
		if(heldItem == null) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof IMultiblockPart) {
				MultiblockControllerBase controller = ((IMultiblockPart)te).getMultiblockController();

				if(controller == null) {
					player.addChatMessage(new TextComponentString(String.format("SERIOUS ERROR - server part @ %d, %d, %d has no controller!", pos.getX(), pos.getY(), pos.getZ()))); //TODO Localize
				}
				else {
					ValidationError e = controller.getLastError();
					if(e != null) {
						// TODO Commented temporarily to allow this thing to compile...
						//player.addChatMessage(new ChatComponentText(e.getMessage() + " - controller " + Integer.toString(controller.hashCode()))); //Would it be worth it to localize one word?
						return true;
					}
				}
			}
		}*/
		/*
		// Does this machine even have a GUI?
		if(metadata != METADATA_CONTROLLER) { return false; }

		// Check to see if machine is assembled
		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof IMultiblockPart)) {
			return false;
		}
		
		IMultiblockPart part = (IMultiblockPart)te;
		if(!part.isConnected() || !part.getMultiblockController().isAssembled()) {
			return false;
		}
		
		player.openGui(BigReactors.getInstance(), 0, world, pos.getX(), pos.getY(), pos.getZ());
		*/
		return true;
	}

	// TODO Commented temporarily to allow this thing to compile...
	/*
	@Override
	public boolean renderAsNormalBlock()
	{
		return true;
	}
	*/

	@Override
	public int damageDropped(IBlockState state) {
		return super.damageDropped(state);
	}
	

	/*
	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for(int i = 0; i < _subBlocks.length; i++) {
			par3List.add(new ItemStack(this, 1, i));
		}
	}*/




	/**/
    // IPeripheralProvider
	@Optional.Method(modid = "ComputerCraft")
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		/*
		TileEntity te = world.getTileEntity(pos);

		return (te instanceof TileEntityTurbineComputerPort) ? (IPeripheral)te : null;
		*/
		return null;
	}
	/**/
}