package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorGlass;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartGlass;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.lib.BlockFacings;
import it.zerono.mods.zerocore.lib.PropertyBlockFacings;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BlockMultiblockGlass extends BlockTieredPart {

	public BlockMultiblockGlass(PartType type, String blockName) {

		super(type, blockName, Material.GLASS);
		this.setSoundType(SoundType.GLASS);
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

		for (PartTier tier: PartTier.RELEASED_TIERS) {

			ResourceLocation location = new ResourceLocation(domain, tier.getName() + "_" + path);

			ModelLoader.setCustomModelResourceLocation(item, tier.toMeta(), new ModelResourceLocation(location, "inventory"));
		}
	}

	@Override
	public void registerRecipes() {

		final EnumSet<PartTier> tiers;
		final BlockMultiblockCasing casingBlock;
		final boolean useGlassReinforced = BigReactors.CONFIG.requireObsidianGlass && OreDictionaryHelper.doesOreNameExist("glassReinforced");
		final boolean useGlassHardened = BigReactors.CONFIG.requireObsidianGlass && OreDictionaryHelper.doesOreNameExist("blockGlassHardened");
		final List<String> glassTypes = new ArrayList<>();

		if (PartType.ReactorGlass == this._type) {

			tiers = PartTier.REACTOR_TIERS;
			casingBlock = BrBlocks.reactorCasing;

		} else {

			tiers = PartTier.TURBINE_TIERS;
			casingBlock = BrBlocks.turbineHousing;
		}

		if (useGlassReinforced)
			glassTypes.add("glassReinforced");

		if (useGlassHardened)
			glassTypes.add("blockGlassHardened");

		if (!useGlassReinforced && !useGlassHardened)
			glassTypes.add("blockGlassColorless");

		for (PartTier tier : tiers) {

			final ItemStack output = this.createItemStack(tier, 1);
			final ItemStack casing = casingBlock.createItemStack(tier, 1);

			for (String glass : glassTypes)
				GameRegistry.addRecipe(new ShapedOreRecipe(output, "GCG", 'G', glass, 'C', casing));
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
