package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.compat.CompatManager;
import erogenousbeef.bigreactors.common.compat.IdReference;
import erogenousbeef.bigreactors.common.multiblock.computer.TurbineComputer;
import erogenousbeef.bigreactors.common.multiblock.computer.TurbineComputerCC;
import erogenousbeef.bigreactors.common.multiblock.computer.TurbineComputerOC;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;

@Optional.InterfaceList({
		@Optional.Interface(iface = "erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart", modid = IdReference.MODID_OPENCOMPUTERS),
})
public class TileEntityTurbineComputerPort extends TileEntityTurbinePart {

	public TileEntityTurbineComputerPort() {

		this._ccComputer = CompatManager.isModLoaded(IdReference.MODID_COMPUTERCRAFT) ? TurbineComputerCC.create(this) : null;
		this._ocComputer = CompatManager.isModLoaded(IdReference.MODID_OPENCOMPUTERS) ? TurbineComputerOC.create(this) : null;
	}

	@Override
	@Optional.Method(modid = IdReference.MODID_OPENCOMPUTERS)
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		// Note: this is only needed by OpenComputers, hence the @Optional annotation
		return (null != this._ocComputer && TurbineComputerOC.isComputerCapability(capability)) ||
				super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	@Optional.Method(modid = IdReference.MODID_OPENCOMPUTERS)
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {

		// Note: this is only needed by OpenComputers, hence the @Optional annotation

		if (null != this._ocComputer && TurbineComputerOC.isComputerCapability(capability))
			return (T)this._ocComputer;

		return super.getCapability(capability, facing);
	}

	@Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
	public TurbineComputer getComputerCraftPeripheral() {
		// Note: this is only needed by ComputerCraft, hence the @Optional annotation
		return this._ccComputer;
	}

	@Override
	public void onAttached(MultiblockControllerBase newController) {

		super.onAttached(newController);

		if (null != this._ccComputer)
			this._ccComputer.onAttachedToController();

		if (null != this._ocComputer)
			this._ocComputer.onAttachedToController();
	}

	@Override
	public void onDetached(MultiblockControllerBase oldController) {

		super.onDetached(oldController);

		if (null != this._ccComputer)
			this._ccComputer.onDetachedFromController();

		if (null != this._ocComputer)
			this._ocComputer.onDetachedFromController();
	}

	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataFrom(data, syncReason);

		if (null != this._ccComputer)
			this._ccComputer.syncDataFrom(data, syncReason);

		if (null != this._ocComputer)
			this._ocComputer.syncDataFrom(data, syncReason);
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataTo(data, syncReason);

		if (null != this._ccComputer)
			this._ccComputer.syncDataTo(data, syncReason);

		if (null != this._ocComputer)
			this._ocComputer.syncDataTo(data, syncReason);
	}

	private final TurbineComputer _ccComputer;
	private final TurbineComputer _ocComputer;
}