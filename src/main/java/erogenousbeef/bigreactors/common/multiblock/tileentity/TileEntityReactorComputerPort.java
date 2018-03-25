package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.compat.CompatManager;
import erogenousbeef.bigreactors.common.compat.IdReference;
import erogenousbeef.bigreactors.common.multiblock.computer.ReactorComputerPeripheral;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.lib.compat.computer.Connector;
import it.zerono.mods.zerocore.lib.compat.computer.ConnectorComputerCraft;
import it.zerono.mods.zerocore.lib.compat.computer.ConnectorOpenComputers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;

@Optional.InterfaceList({
		@Optional.Interface(iface = "erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart", modid = IdReference.MODID_OPENCOMPUTERS),
})
public class TileEntityReactorComputerPort extends TileEntityReactorPart {

	public TileEntityReactorComputerPort() {

		final ReactorComputerPeripheral reactorPeripheral = new ReactorComputerPeripheral(this);

		this._ocConnector = CompatManager.isModLoaded(IdReference.MODID_OPENCOMPUTERS) ?
				ConnectorOpenComputers.create("br_reactor", reactorPeripheral) : null;

		this._ccConnector = CompatManager.isModLoaded(IdReference.MODID_COMPUTERCRAFT) ?
				ConnectorComputerCraft.create("BigReactors-Reactor", reactorPeripheral) : null;
	}

	@Override
	@Optional.Method(modid = IdReference.MODID_OPENCOMPUTERS)
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		// Note: this is only needed by OpenComputers, hence the @Optional annotation
		return (null != this._ocConnector && ConnectorOpenComputers.isComputerCapability(capability)) ||
				super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	@Optional.Method(modid = IdReference.MODID_OPENCOMPUTERS)
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {

		// Note: this is only needed by OpenComputers, hence the @Optional annotation
		if (null != this._ocConnector && ConnectorOpenComputers.isComputerCapability(capability)) {
			return (T) this._ocConnector;
		}

		return super.getCapability(capability, facing);
	}

	@Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
	public Connector getComputerCraftPeripheral() {
		// Note: this is only needed by ComputerCraft, hence the @Optional annotation
		return this._ccConnector;
	}

	@Override
	public void onAttached(MultiblockControllerBase newController) {

		super.onAttached(newController);

		if (null != this._ccConnector) {
			this._ccConnector.onAttachedToController();
		}

		if (null != this._ocConnector) {
			this._ocConnector.onAttachedToController();
		}
	}

	@Override
	public void onDetached(MultiblockControllerBase oldController) {

		super.onDetached(oldController);

		if (null != this._ccConnector) {
			this._ccConnector.onDetachedFromController();
		}

		if (null != this._ocConnector) {
			this._ocConnector.onDetachedFromController();
		}
	}

	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataFrom(data, syncReason);

		if (null != this._ccConnector) {
			this._ccConnector.syncDataFrom(data, syncReason);
		}

		if (null != this._ocConnector) {
			this._ocConnector.syncDataFrom(data, syncReason);
		}
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataTo(data, syncReason);

		if (null != this._ccConnector) {
			this._ccConnector.syncDataTo(data, syncReason);
		}

		if (null != this._ocConnector) {
			this._ocConnector.syncDataTo(data, syncReason);
		}
	}

	private final Connector _ocConnector;
	private final Connector _ccConnector;
}