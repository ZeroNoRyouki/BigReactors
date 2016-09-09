package erogenousbeef.bigreactors.net.message;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Send a "setActive" command to any IActivateable machine.
 * Currently used for multiblock reactors and turbines.
 * @see erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable
 * @author Erogenous Beef
 *
 */
public class MachineCommandActivateMessage extends ModTileEntityMessage {

	public MachineCommandActivateMessage() {
		this._setActive = true;
	}

	protected MachineCommandActivateMessage(BlockPos position, boolean setActive) {

		super(position);
		this._setActive = setActive;
	}

	public MachineCommandActivateMessage(IActivateable machine, boolean setActive) {
		this(machine.getReferenceCoord(), setActive);
	}

	@Override
	public void toBytes(ByteBuf buffer) {

		super.toBytes(buffer);
		buffer.writeBoolean(this._setActive);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {

		super.fromBytes(buffer);
		this._setActive = buffer.readBoolean();
	}

	protected boolean _setActive;

	public static class Handler extends ModTileEntityMessageHandlerServer<MachineCommandActivateMessage> {

		@Override
		protected void processTileEntityMessage(MachineCommandActivateMessage message, MessageContext ctx, TileEntity tileEntity) {

			if (tileEntity instanceof IActivateable) {

				((IActivateable) tileEntity).setActive(message._setActive);

			} else {

				BlockPos position = message.getPos();

				BRLog.error("Received a MachineCommandActivateMessage for %d, %d, %d but found no activateable machine", position.getX(), position.getY(), position.getZ());
			}
		}
	}
}
