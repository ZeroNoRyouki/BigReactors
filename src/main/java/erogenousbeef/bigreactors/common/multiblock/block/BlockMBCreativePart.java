/*
package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityReactorCreativeCoolantPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.creative.TileEntityTurbineCreativeSteamGenerator;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

@Deprecated
public class BlockMBCreativePart extends BlockTieredPart {

	public BlockMBCreativePart(PartType type, String blockName) {
		super(type, blockName, Material.IRON);
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
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		// NO sub-blocks for the creative parts
	}

	@Override
	protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position, MultiblockTileEntityBase part) {

		state = super.buildActualState(state, world, position, part);

		if (part instanceof TileEntityReactorCreativeCoolantPort)
			state = state.withProperty(Properties.TIER, ((TileEntityReactorCreativeCoolantPort)part).getMachineTier());

		return state;
	}
}
*/