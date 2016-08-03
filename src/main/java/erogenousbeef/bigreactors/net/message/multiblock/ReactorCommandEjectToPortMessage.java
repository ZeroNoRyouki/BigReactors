package erogenousbeef.bigreactors.net.message.multiblock;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorAccessPort;
import erogenousbeef.bigreactors.net.message.base.ReactorMessageServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReactorCommandEjectToPortMessage extends ReactorMessageServer {

	public ReactorCommandEjectToPortMessage() {

		this._ejectFuel = this._dumpExcess = false;
		this._portX = this._portY = this._portZ = Integer.MAX_VALUE;
	}
	
	public ReactorCommandEjectToPortMessage(TileEntityReactorAccessPort destination, boolean ejectFuel, boolean dumpExcess) {

		super(destination.getReactorController());

		BlockPos position = destination.getPos();

		this._portX = position.getX();
		this._portY = position.getY();
		this._portZ = position.getZ();
		this._ejectFuel = ejectFuel;
		this._dumpExcess = dumpExcess;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

		super.fromBytes(buf);
		this._ejectFuel = buf.readBoolean();
		this._dumpExcess = buf.readBoolean();
		this._portX = buf.readInt();
		this._portY = buf.readInt();
		this._portZ = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {

		super.toBytes(buf);
		buf.writeBoolean(this._ejectFuel);
		buf.writeBoolean(this._dumpExcess);
		buf.writeInt(this._portX);
		buf.writeInt(this._portY);
		buf.writeInt(this._portZ);
	}

	private boolean _ejectFuel;
	private boolean _dumpExcess;
	private int _portX, _portY, _portZ;

	public static class Handler extends ReactorMessageServer.Handler<ReactorCommandEjectToPortMessage> {

		@Override
		protected void processReactorMessage(ReactorCommandEjectToPortMessage message, MessageContext ctx, MultiblockReactor reactor) {

			BlockPos dest = new BlockPos(message._portX, message._portY, message._portZ);

			if (message._ejectFuel)
				reactor.ejectFuel(message._dumpExcess, dest);
			else
				reactor.ejectWaste(message._dumpExcess, dest);
		}
	}
}
