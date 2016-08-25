package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.api.IHeatEntity;
import erogenousbeef.bigreactors.api.IRadiationModerator;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.data.RadiationData;
import erogenousbeef.bigreactors.common.data.RadiationPacket;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.block.BlockTieredPart;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntityReactorPartBase extends RectangularMultiblockTileEntityBase implements IHeatEntity,
														IRadiationModerator, IActivateable, IDebuggable {

	public TileEntityReactorPartBase() {
	}

	public MultiblockReactor getReactorController() { return (MultiblockReactor)this.getMultiblockController(); }

	@Override
	public MultiblockControllerBase createNewMultiblock() {
		return new MultiblockReactor(this.worldObj);
	}
	
	@Override
	public Class<? extends MultiblockControllerBase> getMultiblockControllerType() { return MultiblockReactor.class; }
	
	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {
		super.onMachineAssembled(controller);
		
		// Re-render this block on the client
		if(worldObj.isRemote) {
			WorldHelper.notifyBlockUpdate(worldObj, this.getPos(), null, null);
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();
		
		// Re-render this block on the client
		if(worldObj.isRemote) {
			WorldHelper.notifyBlockUpdate(worldObj, this.getPos(), null, null);
		}
	}
	
	// IHeatEntity
	@Override
	public float getHeat() {
		if(!this.isConnected()) { return 0f; }
		return getReactorController().getFuelHeat();
	}

	@Override
	public float getThermalConductivity() {
		return IHeatEntity.conductivityIron;
	}

	// IRadiationModerator
	@Override
	public void moderateRadiation(RadiationData data, RadiationPacket radiation) {
		// Discard all remaining radiation, sorry bucko
		radiation.intensity = 0f;
	}
	
	// IActivateable
	@Override
	public BlockPos getReferenceCoord() {
		if(isConnected()) {
			return getMultiblockController().getReferenceCoord();
		}
		else {
			return this.getPos();
		}
	}
	
	@Override
	public boolean getActive() {
		if(isConnected()) {
			return getReactorController().getActive();
		}
		else {
			return false;
		}
	}
	
	@Override
	public void setActive(boolean active) {
		if(isConnected()) {
			getReactorController().setActive(active);
		}
		else {
			BlockPos position = this.getPos();
			BRLog.error("Received a setActive command at %d, %d, %d, but not connected to a multiblock controller!",
					position.getX(), position.getY(), position.getZ());
		}
	}

	public PartTier getPartTier() {

		IBlockState state = this.worldObj.getBlockState(this.getWorldPosition());
		Block block = state.getBlock();

		return block instanceof BlockTieredPart ? ((BlockTieredPart)block).getTierFromState(state) : null;
	}

	@Override
	public boolean isGoodForFrame(IMultiblockValidator validatorCallback) {
		return false;
	}

	@Override
	public boolean isGoodForSides(IMultiblockValidator validatorCallback) {
		return false;
	}

	@Override
	public boolean isGoodForTop(IMultiblockValidator validatorCallback) {
		return false;
	}

	@Override
	public boolean isGoodForBottom(IMultiblockValidator validatorCallback) {
		return false;
	}

	@Override
	public boolean isGoodForInterior(IMultiblockValidator validatorCallback) {
		return false;
	}

	@Override
	public void onMachineActivated() {
	}

	@Override
	public void onMachineDeactivated() {
	}

	// IDebuggable

	@Override
	public void getDebugMessages(IDebugMessages messages) {

		MultiblockReactor reactor = this.getReactorController();

		messages.add("debug.bigreactors.teclass", this.getClass().toString());

		if (null != reactor)
			reactor.getDebugMessages(messages);
		else
			messages.add("debug.bigreactors.notattached");
	}
}