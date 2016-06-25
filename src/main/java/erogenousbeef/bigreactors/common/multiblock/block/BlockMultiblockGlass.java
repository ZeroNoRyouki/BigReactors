package erogenousbeef.bigreactors.common.multiblock.block;

import com.google.common.collect.Maps;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorGlass;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartGlass;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import zero.mods.zerocore.lib.BlockFacings;
import zero.mods.zerocore.lib.PropertyBlockFacings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockMultiblockGlass extends BlockTieredPart {

	public BlockMultiblockGlass(PartType type, String blockName) {

		super(type, blockName, Material.glass);
		this.setStepSound(SoundType.GLASS);
		this._actualFacings = new boolean[EnumFacing.VALUES.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onPostClientRegister() {

		Item item = Item.getItemFromBlock(this);
		ResourceLocation name = this.getRegistryName();
		String domain = name.getResourceDomain();
		String path = name.getResourcePath();

		ModelLoader.setCustomStateMapper(this, (new StateMap.Builder()).withName(Properties.TIER).withSuffix("_" + path).build());

		for (PartTier tier: PartTier.VALUES) {

			ResourceLocation location = new ResourceLocation(domain, tier.getName() + "_" + path);

			ModelLoader.setCustomModelResourceLocation(item, tier.toMeta(), new ModelResourceLocation(location, "inventory"));
		}
	}


	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		switch (this._type) {

			case ReactorGlass:
				return new TileEntityReactorGlass();

			case TurbineGlass:
				return new TileEntityTurbinePartGlass();

			default:
				throw new IllegalArgumentException("Unrecognized part");
		}
	}

	/*
	@Override
	public List<Pair<Integer, ModelResourceLocation>> getMetadataToModelMappings() {

		List<Pair<Integer, ModelResourceLocation>> mappings = new ArrayList();

		/ *
		IBlockState defaultState = this.getDefaultState();
		String mapFormat = "";
		boolean first = true;

		for (IProperty<?> prop : defaultState.getProperties().keySet()) {

			String name = prop.getName();

			if (!first)
				mapFormat += ",";

			if ("tier".equals(name)) {

				mapFormat += "tier=%s";

			} else {

				mapFormat += name + "=" + defaultState.getValue(prop).toString();
			}

			first = false;
		}* /

		ResourceLocation loc = this.getRegistryName();
		String baseBlockStateName = loc.getResourceDomain() + ":" + loc.getResourcePath() + "_";

		for (PartTier tier : PartTier.VALUES)
			mappings.add(new ImmutablePair(Integer.valueOf(tier.toMeta()),
					new ModelResourceLocation(baseBlockStateName + tier.getName(), String.format("facings=none,tier=%s", tier.getName()))));

		return mappings;
	}
	*/
	/*
	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {

		Map<IBlockState, ModelResourceLocation> map = Maps.newHashMap();

		if (blockIn instanceof BlockMultiblockGlass) {

			IBlockState state = blockIn.getDefaultState();

			for (PartTier tier: PartTier.VALUES) {

				IBlockState mapState = state.withProperty(Properties.TIER, tier);
				ModelResourceLocation res = new ModelResourceLocation(
						new ResourceLocation(BigReactors.MODID, "reactorGlass_" + tier.getName()),
						"tier=" + tier.getName()
				);

				map.put(mapState, res);
			}
		}

		return map;
	}*/

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos position) {

		Block thisBlock = state.getBlock();
		int len = EnumFacing.VALUES.length;

		for (int i = 0; i < len; ++i) {

			IBlockState neighbor = world.getBlockState(position.offset(EnumFacing.VALUES[i]));

			this._actualFacings[i] = thisBlock == neighbor.getBlock();
		}

		BlockFacings facings = BlockFacings.from(this._actualFacings);

		return state.withProperty(PropertyBlockFacings.FACINGS, facings.toProperty());
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void buildBlockState(BlockStateContainer.Builder builder) {

		super.buildBlockState(builder);
		builder.add(PropertyBlockFacings.FACINGS);
	}

	@Override
	protected IBlockState buildDefaultState(IBlockState state) {

		return super.buildDefaultState(state).withProperty(PropertyBlockFacings.FACINGS, PropertyBlockFacings.None);
	}

	private boolean[] _actualFacings;
}
