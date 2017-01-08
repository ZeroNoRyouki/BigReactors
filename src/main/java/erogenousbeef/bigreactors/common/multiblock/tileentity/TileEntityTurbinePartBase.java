package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntityTurbinePartBase extends TileEntityMachinePart implements IDebuggable {

	@Override
	public MultiblockControllerBase createNewMultiblock() {
		return new MultiblockTurbine(worldObj);
	}
	
	@Override
	public Class<? extends MultiblockControllerBase> getMultiblockControllerType() {
		return MultiblockTurbine.class;
	}
/*
	@Override
	public void onPostMachineAssembled(MultiblockControllerBase controller) {

		super.onPostMachineAssembled(controller);
		
		// Re-render this block on the client
		if(worldObj.isRemote) {
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
		}
	}

	@Override
	public void onPostMachineBroken() {

		super.onPostMachineBroken();
		
		// Re-render this block on the client
		if(worldObj.isRemote) {
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
		}
	}

	@Override
	public void onMachineActivated() {
	}

	@Override
	public void onMachineDeactivated() {
	}*/

	public MultiblockTurbine getTurbine() {
		return (MultiblockTurbine)getMultiblockController();
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
			return getTurbine().getActive();
		}
		else {
			return false;
		}
	}
	
	@Override
	public void setActive(boolean active) {
		if(isConnected()) {
			getTurbine().setActive(active);
		}
		else {
			BlockPos position = this.getPos();
			BRLog.error("Received a setActive command at %d, %d, %d, but not connected to a multiblock controller!", position.getX(), position.getY(), position.getZ());
		}
	}

	/*
	public PartTier getPartTier() {

		IBlockState state = this.worldObj.getBlockState(this.getWorldPosition());
		Block block = state.getBlock();

		return block instanceof BlockTieredPart ? ((BlockTieredPart)block).getTierFromState(state) : null;
	}*/

	// IDebuggable

	@Override
	public void getDebugMessages(IDebugMessages messages) {

		MultiblockTurbine turbine = this.getTurbine();

		messages.add("debug.bigreactors.teclass", this.getClass().toString());

		if (null != turbine)
			turbine.getDebugMessages(messages);
		else
			messages.add("debug.bigreactors.notattached");

	}
}
