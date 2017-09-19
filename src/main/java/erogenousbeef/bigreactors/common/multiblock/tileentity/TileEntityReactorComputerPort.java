package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.compat.IdReference;
import erogenousbeef.bigreactors.common.multiblock.computer.ReactorComputer;
import erogenousbeef.bigreactors.common.multiblock.computer.ReactorComputerCC;
import erogenousbeef.bigreactors.common.multiblock.computer.ReactorComputerOC;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import li.cil.oc.api.network.Environment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;

@Optional.InterfaceList({
		@Optional.Interface(iface = "erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart", modid = IdReference.MODID_OPENCOMPUTERS),
})
public class TileEntityReactorComputerPort extends TileEntityReactorPart implements ITickableMultiblockPart {

	@Override
	@Optional.Method(modid = IdReference.MODID_OPENCOMPUTERS)
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {

		if (s_openComputersEnabled) {

			if (ReactorComputerOC.isComputerCapability(capability))
				return true;
		}

		return super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	@Optional.Method(modid = IdReference.MODID_OPENCOMPUTERS)
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {

		if (s_openComputersEnabled) {

			if (ReactorComputerOC.isComputerCapability(capability)) {

				if (null == this._ocComputer)
					this._ocComputer = ReactorComputerOC.createCapability(this);

				return (T)this._ocComputer;
			}
		}

		return super.getCapability(capability, facing);
	}

	@Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
	public ReactorComputer getComputerCraftPeripheral() {

		if (null == this._ccComputer)
			this._ccComputer = new ReactorComputerCC(this);

		return this._ccComputer;
	}

	@CapabilityInject(Environment.class)
	public static void enableOpenComputers(Capability<?> capability) {
		s_openComputersEnabled = null != capability;
	}

	/**
	 * Called once every tick from the reactor's main server tick loop.
	 */
	@Override
	@Optional.Method(modid = IdReference.MODID_OPENCOMPUTERS)
	public void onMultiblockServerTick() {

		// Note: this is only needed by OpenComputers

		if (s_openComputersEnabled && null != this._ocComputer)
			this._ocComputer.onServerTick();
	}

	/**
	 * Called from Minecraft's tile entity loop, after all tile entities have been ticked,
	 * as the chunk in which this tile entity is contained is unloading.
	 * Happens before the Forge TickEnd event.
	 *
	 * @see TileEntity#onChunkUnload()
	 */
	@Override
	@Optional.Method(modid = IdReference.MODID_OPENCOMPUTERS)
	public void onChunkUnload() {

		// Note: this is only needed by OpenComputers

		if (s_openComputersEnabled && null != this._ocComputer)
			this._ocComputer.onChunkUnload();

		super.onChunkUnload();
	}

	/**
	 * Called when a block is removed by game actions, such as a player breaking the block
	 * or the block being changed into another block.
	 *
	 * @see TileEntity#invalidate()
	 */
	@Override
	@Optional.Method(modid = IdReference.MODID_OPENCOMPUTERS)
	public void invalidate() {

		// Note: this is only needed by OpenComputers

		if (s_openComputersEnabled && null != this._ocComputer)
			this._ocComputer.onPortRemoved();

		super.invalidate();
	}

	private ReactorComputer _ccComputer = null;
	private ReactorComputer _ocComputer = null;

	private static boolean s_openComputersEnabled = false;
}