package erogenousbeef.bigreactors.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import erogenousbeef.bigreactors.net.message.base.WorldMessageServer;

/**
 * Send a "setActive" command to any IActivateable machine.
 * Currently used for multiblock reactors and turbines.
 * @see erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable
 * @author Erogenous Beef
 *
 */
public class MachineCommandActivateMessage extends WorldMessageServer {
	protected boolean setActive;
	public MachineCommandActivateMessage() { super(); setActive = true; }

	protected MachineCommandActivateMessage(BlockPos position, boolean setActive) {
		super(position);
		this.setActive = setActive;
	}

	public MachineCommandActivateMessage(IActivateable machine, boolean setActive) {
		this(machine.getReferenceCoord(), setActive);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeBoolean(setActive);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		setActive = buf.readBoolean();
	}
	
	public static class Handler extends WorldMessageServer.Handler<MachineCommandActivateMessage> {
		@Override
		protected IMessage handleMessage(MachineCommandActivateMessage message,
				MessageContext ctx, TileEntity te) {
			if(te instanceof IActivateable) {
				IActivateable machine = (IActivateable)te;
				machine.setActive(message.setActive);
			}
			else {
				BRLog.error("Received a MachineCommandActivateMessage for %d, %d, %d but found no activateable machine", message.x, message.y, message.z);
			}
			return null;
		}
	}
	
}
