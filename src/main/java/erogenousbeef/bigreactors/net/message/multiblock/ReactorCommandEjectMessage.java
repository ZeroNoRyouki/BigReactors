package erogenousbeef.bigreactors.net.message.multiblock;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.net.message.base.ReactorMessageServer;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReactorCommandEjectMessage extends ReactorMessageServer {

	public ReactorCommandEjectMessage() { 
		this._ejectFuel = this._dumpExcess = false;
	}
	
	public ReactorCommandEjectMessage(MultiblockReactor reactor, boolean ejectFuel, boolean dumpExcess) {

		super(reactor);
		this._ejectFuel = ejectFuel;
		this._dumpExcess = dumpExcess;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

		super.fromBytes(buf);
		this._ejectFuel = buf.readBoolean();
		this._dumpExcess = buf.readBoolean();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {

		super.toBytes(buf);
		buf.writeBoolean(this._ejectFuel);
		buf.writeBoolean(this._dumpExcess);
	}

	private boolean _ejectFuel;
	private boolean _dumpExcess;

	public static class Handler extends ReactorMessageServer.Handler<ReactorCommandEjectMessage> {

		@Override
		protected void processReactorMessage(ReactorCommandEjectMessage message, MessageContext ctx, MultiblockReactor reactor) {

			if (message._ejectFuel)
				reactor.ejectFuel(message._dumpExcess, null);
			else
				reactor.ejectWaste(message._dumpExcess, null);
		}
	}
}
