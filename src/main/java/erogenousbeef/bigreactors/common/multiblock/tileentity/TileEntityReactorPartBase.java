package erogenousbeef.bigreactors.common.multiblock.tileentity;

import net.minecraft.entity.player.InventoryPlayer;
import erogenousbeef.bigreactors.api.IHeatEntity;
import erogenousbeef.bigreactors.api.IRadiationModerator;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.data.RadiationData;
import erogenousbeef.bigreactors.common.data.RadiationPacket;
import erogenousbeef.bigreactors.common.interfaces.IBeefDebuggableTile;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IMultiblockGuiHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import zero.mods.zerocore.util.WorldHelper;

public abstract class TileEntityReactorPartBase extends
		RectangularMultiblockTileEntityBase implements IMultiblockGuiHandler, IHeatEntity,
														IRadiationModerator, IActivateable,
														IBeefDebuggableTile {

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
	
	// IMultiblockGuiHandler
	/**
	 * @return The Container object for use by the GUI. Null if there isn't any.
	 */
	@Override
	public Object getContainer(InventoryPlayer inventoryPlayer) {
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Object getGuiElement(InventoryPlayer inventoryPlayer) {
		return null;
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
	
	@Override
	public String getDebugInfo() {
		MultiblockReactor r = getReactorController();
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().toString()).append("\n");
		if(r == null) {
			sb.append("Not attached to controller!");
			return sb.toString();
		}
		sb.append(r.getDebugInfo());
		return sb.toString();
	}
}
