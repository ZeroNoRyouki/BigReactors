package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.RotorBladeState;
import erogenousbeef.bigreactors.common.multiblock.RotorShaftState;
import erogenousbeef.bigreactors.common.multiblock.block.BlockTurbineRotorBlade;
import erogenousbeef.bigreactors.common.multiblock.block.BlockTurbineRotorShaft;
import erogenousbeef.bigreactors.common.multiblock.helpers.RotorInfo;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTurbineRotorBearing extends
        TileEntityTurbinePart {

	RotorInfo rotorInfo = null;
	Integer displayList = null;
	float angle = 0f;
	
	@SideOnly(Side.CLIENT)
	public Integer getDisplayList() { return displayList; }
	
	@SideOnly(Side.CLIENT)
	public void setDisplayList(int newList) { displayList = newList; }
	
	@SideOnly(Side.CLIENT)
	public void clearDisplayList() { displayList = null; }
	
	@SideOnly(Side.CLIENT)
	public float getAngle() { return angle; }
	
	@SideOnly(Side.CLIENT)
	public void setAngle(float newAngle) { angle = newAngle; }

	protected AxisAlignedBB _renderBoundingBox;

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {

		final long distance = 16 * FMLClientHandler.instance().getClient().gameSettings.renderDistanceChunks;

		return distance * distance;
	}

	@Override
	public void onPostMachineAssembled(MultiblockControllerBase controller) {

		super.onPostMachineAssembled(controller);
		this.displayList = null;
		this.calculateRotorInfo();
	}
	
	@SideOnly(Side.CLIENT)
	public RotorInfo getRotorInfo() {

		if (null == this.rotorInfo && this.isConnected() && this.getMultiblockController().isAssembled())
			this.calculateRotorInfo();

		return this.rotorInfo;
	}

	@SideOnly(Side.CLIENT)
	public void resetRotorInfo() {

		this.clearDisplayList();
		this.rotorInfo = null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return null != this._renderBoundingBox ? this._renderBoundingBox : super.getRenderBoundingBox();
	}

	private void calculateRotorInfo() {

		final BlockTurbineRotorShaft turbineRotorShaft = BrBlocks.turbineRotorShaft;
		final BlockTurbineRotorBlade turbineRotorBlade = BrBlocks.turbineRotorBlade;

		// Calculate bounding box
		final MultiblockTurbine turbine = getTurbine();
		final BlockPos minCoord = turbine.getMinimumCoord();
		final BlockPos maxCoord = turbine.getMaximumCoord();
		final World world = this.getWorld();

		this._renderBoundingBox = new AxisAlignedBB(minCoord.getX(), minCoord.getY(), minCoord.getZ(),
										maxCoord.getX() + 1, maxCoord.getY() + 1, maxCoord.getZ() + 1);
		
		if (WorldHelper.calledByLogicalClient(world)) {

			EnumFacing direction = this.getOutwardFacing();
			EnumFacing.Axis shaftAxis = direction.getAxis();

			// Calculate rotor info
			RotorInfo info = this.rotorInfo = new RotorInfo();

			info.x = this.getPos().getX();
			info.y = this.getPos().getY();
			info.z = this.getPos().getZ();

			info.rotorDirection = direction.getOpposite();

			switch (shaftAxis) {

				case Y:
					info.rotorLength = maxCoord.getY() - minCoord.getY() - 1;
					break;

				case X:
					info.rotorLength = maxCoord.getX() - minCoord.getX() - 1;
					break;

				case Z:
					info.rotorLength = maxCoord.getZ() - minCoord.getZ() - 1;
					break;
			}

			BlockPos currentCoord = this.getWorldPosition().offset(info.rotorDirection);
			BlockPos bladeCoord;
			IBlockState state;
			final EnumFacing[] dirsToCheck = RotorShaftState.getBladesDirections(shaftAxis);
			int rotorPosition = 0;

			info.bladeLengths = new int[info.rotorLength][4];
			info.shaftStates = new RotorShaftState[info.rotorLength];
			info.bladeStates = new RotorBladeState[info.rotorLength][4];

			while (rotorPosition < info.rotorLength) {

				state = world.getBlockState(currentCoord);
				info.shaftStates[rotorPosition] = turbineRotorShaft.buildActualStateInternal(state, world,
						currentCoord, this, true).getValue(Properties.ROTORSHAFTSTATE);

				// Current block is a rotor
				// Get list of normals
				int bladeLength;
				RotorBladeState bladeState;
				EnumFacing bladeDir;

				for (int bladeIdx = 0; bladeIdx < dirsToCheck.length; bladeIdx++) {

					bladeDir = dirsToCheck[bladeIdx];
					bladeCoord = currentCoord.offset(bladeDir);
					bladeLength = 0;
					bladeState = null;

					state = world.getBlockState(bladeCoord);

					if (turbineRotorBlade == state.getBlock()) {

						bladeState = turbineRotorBlade.buildActualStateInternal(state, world, bladeCoord, this,
								true).getValue(Properties.ROTORBLADESTATE);

						while (bladeLength < 32 && turbineRotorBlade == world.getBlockState(bladeCoord).getBlock()) {

							++bladeLength;
							bladeCoord = bladeCoord.offset(bladeDir);
						}
					}

					info.bladeLengths[rotorPosition][bladeIdx] = bladeLength;
					info.bladeStates[rotorPosition][bladeIdx] = bladeState;
				}
				
				++rotorPosition;
				currentCoord = currentCoord.offset(info.rotorDirection);
			}
		}
	}
}
