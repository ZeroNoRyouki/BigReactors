package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.RotorShaftState;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorShaft;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.EnumSet;

public class BlockTurbineRotorShaft extends BlockTieredPart implements ITurbineRotorPart {

	public BlockTurbineRotorShaft(String blockName) {

		super(PartType.TurbineRotorShaft, blockName, Material.IRON);
		this.setLightLevel(0.9f);
		this._neighbors = new IBlockState[EnumFacing.VALUES.length];
	}

	@Override
	public void registerRecipes() {

		if (PartTier.TURBINE_TIERS.contains(PartTier.Legacy))
			GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 1), "ICI",
				'C', "ingotCyanite", 'I', "ingotIron"));

		if (PartTier.TURBINE_TIERS.contains(PartTier.Basic))
			GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 1), "ICI",
				'C', "ingotCyanite", 'I', "ingotIron"));
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityTurbineRotorShaft();
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		// allow correct brightness of the rotor TESR
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}

	@Override
	public boolean isVisuallyOpaque() {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	/**
	 * Override and add custom masses when you add non-standard turbine parts
	 */
	@Override
	public int getMass(IBlockState blockState) {
		return 10;
	}

	@Override
	public boolean isShaft() {
		return true;
	}

	@Override
	public boolean isBlade() {
		return false;
	}

	@Override
	protected void buildBlockState(BlockStateContainer.Builder builder) {

		super.buildBlockState(builder);
		builder.add(Properties.ROTORSHAFTSTATE);
	}

	@Override
	protected IBlockState buildDefaultState(IBlockState state) {
		return super.buildDefaultState(state).withProperty(Properties.ROTORSHAFTSTATE, RotorShaftState.Y_NOBLADES);
	}

	@Override
	protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position, MultiblockTileEntityBase part) {
		return this.buildActualStateInternal(state, world, position, part, false);
	}

	public IBlockState buildActualStateInternal(IBlockState state, IBlockAccess world, BlockPos position,
												   MultiblockTileEntityBase part, boolean buildingClientRotor) {

		final MultiblockTurbine turbine = part.isConnected() ? (MultiblockTurbine)part.getMultiblockController() : null;

		if (!buildingClientRotor && null != turbine && turbine.getActive())
			return super.buildActualState(state, world, position, part).withProperty(Properties.ROTORSHAFTSTATE, RotorShaftState.HIDDEN);

		EnumFacing.Axis rotorAxis = EnumFacing.Axis.Y;
		final int neighborsSlotCount = this._neighbors.length;

		for (int i = 0; i < neighborsSlotCount; ++i)
			this._neighbors[i] = null;

		for (EnumFacing facing: EnumFacing.VALUES) {

			// select an axis based on the first rotor shaft found nearby

			final IBlockState neighbor = world.getBlockState(position.offset(facing));

			this._neighbors[facing.getIndex()] = neighbor;

			if (this == neighbor.getBlock()) {

				rotorAxis = facing.getAxis();
				break;
			}
		}

		// any blade around?

		final EnumFacing.AxisDirection[] directions = EnumFacing.AxisDirection.values();
		final EnumSet<EnumFacing.Axis> candidateBladeAxis = EnumSet.allOf(EnumFacing.Axis.class);
		final EnumSet<EnumFacing.Axis> bladeAxis;

		candidateBladeAxis.remove(rotorAxis);
		bladeAxis = EnumSet.copyOf(candidateBladeAxis);

		for (EnumFacing.Axis axis : candidateBladeAxis) {
			for (EnumFacing.AxisDirection direction : directions) {

				final EnumFacing facing = EnumFacing.getFacingFromAxis(direction, axis);
				final int index = facing.getIndex();
				IBlockState neighbor = this._neighbors[index];

				if (null == neighbor)
					neighbor = world.getBlockState(position.offset(facing));

				if (BrBlocks.turbineRotorBlade != neighbor.getBlock()) {

					bladeAxis.remove(axis);
					break;
				}
			}
		}

		// final rotor state

		RotorShaftState rotorState = this.getStateFromBladePosition(rotorAxis, bladeAxis);

		return super.buildActualState(state, world, position, part).withProperty(Properties.ROTORSHAFTSTATE, rotorState);
	}

	private RotorShaftState getStateFromBladePosition(EnumFacing.Axis rotorAxis, EnumSet<EnumFacing.Axis> bladeAxis) {

		final int count = bladeAxis.size();
		final RotorShaftState[] states = ROTOR_STATE_MAP[rotorAxis.ordinal()];
		RotorShaftState result = RotorShaftState.Y_NOBLADES;

		switch (count) {

			case 0:
				result = states[3];
				break;

			case 1:
				for (EnumFacing.Axis axis : bladeAxis)
					if (bladeAxis.contains(axis)) {

						result = states[axis.ordinal()];
						break;
					}
				break;

			case 2:
				result = states[4];
				break;
		}

		return result;
	}

	private IBlockState[] _neighbors;
	private static final RotorShaftState[][] ROTOR_STATE_MAP;

	static {

		ROTOR_STATE_MAP = new RotorShaftState[3][5];

		int xIndex = EnumFacing.Axis.X.ordinal();
		int yIndex = EnumFacing.Axis.Y.ordinal();
		int zIndex = EnumFacing.Axis.Z.ordinal();
		RotorShaftState[] entry;

		entry = ROTOR_STATE_MAP[xIndex];
		entry[xIndex] = null;
		entry[yIndex] = RotorShaftState.X_Y;
		entry[zIndex] = RotorShaftState.X_Z;
		entry[3] = RotorShaftState.X_NOBLADES;
		entry[4] = RotorShaftState.X_YZ;

		entry = ROTOR_STATE_MAP[yIndex];
		entry[xIndex] = RotorShaftState.Y_X;
		entry[yIndex] = null;
		entry[zIndex] = RotorShaftState.Y_Z;
		entry[3] = RotorShaftState.Y_NOBLADES;
		entry[4] = RotorShaftState.Y_XZ;

		entry = ROTOR_STATE_MAP[zIndex];
		entry[xIndex] = RotorShaftState.Z_X;
		entry[yIndex] = RotorShaftState.Z_Y;
		entry[zIndex] = null;
		entry[3] = RotorShaftState.Z_NOBLADES;
		entry[4] = RotorShaftState.Z_XY;
	}
}