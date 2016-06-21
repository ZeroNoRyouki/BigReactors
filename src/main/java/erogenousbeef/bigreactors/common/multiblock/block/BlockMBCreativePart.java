package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityReactorCreativeCoolantPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityTurbineCreativeSteamGenerator;
import erogenousbeef.bigreactors.utils.StaticUtils;

public class BlockMBCreativePart extends BlockMultiblockDevice {

	public BlockMBCreativePart(PartType type, String blockName) {

		super(type, blockName);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		switch (this._type) {

			case ReactorCreativeCoolantPort:
				return new TileEntityReactorCreativeCoolantPort();

			case TurbineCreativeSteamGenerator:
				return new TileEntityTurbineCreativeSteamGenerator();

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
}
