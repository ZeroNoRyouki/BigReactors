package erogenousbeef.bigreactors.net.message.multiblock;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorAccessPort;
import erogenousbeef.bigreactors.net.message.base.ReactorMessageServer;

public class ReactorCommandEjectToPortMessage extends ReactorMessageServer {
	protected boolean ejectFuel;
	protected boolean dumpExcess;
	int portX, portY, portZ;
	
	public ReactorCommandEjectToPortMessage() { 
		super();
		ejectFuel = dumpExcess = false;
		portX = portY = portZ = Integer.MAX_VALUE;
	}
	
	public ReactorCommandEjectToPortMessage(TileEntityReactorAccessPort destination,
											boolean ejectFuel,
											boolean dumpExcess) {
		super(destination.getReactorController());

		BlockPos position = destination.getPos();

		this.portX = position.getX();
		this.portY = position.getY();
		this.portZ = position.getZ();
		this.ejectFuel = ejectFuel;
		this.dumpExcess = dumpExcess;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		ejectFuel = buf.readBoolean();
		dumpExcess = buf.readBoolean();
		portX = buf.readInt();
		portY = buf.readInt();
		portZ = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeBoolean(ejectFuel);
		buf.writeBoolean(dumpExcess);
		buf.writeInt(portX);
		buf.writeInt(portY);
		buf.writeInt(portZ);
	}
	
	public static class Handler extends ReactorMessageServer.Handler<ReactorCommandEjectToPortMessage> {
		@Override
		public IMessage handleMessage(ReactorCommandEjectToPortMessage message, MessageContext ctx, MultiblockReactor reactor) {
			BlockPos dest = new BlockPos(message.portX, message.portY, message.portZ);
			if(message.ejectFuel) {
				reactor.ejectFuel(message.dumpExcess, dest);
			}
			else {
				reactor.ejectWaste(message.dumpExcess, dest);
			}
			return null;
		}
	}
}
