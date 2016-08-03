package erogenousbeef.bigreactors.net.message.multiblock;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor.WasteEjectionSetting;
import erogenousbeef.bigreactors.net.message.base.ReactorMessageServer;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReactorChangeWasteEjectionMessage extends ReactorMessageServer {

	public ReactorChangeWasteEjectionMessage() {
		this._newSetting = 0;
	}

	public ReactorChangeWasteEjectionMessage(MultiblockReactor reactor, WasteEjectionSetting setting) {

		super(reactor);
		this._newSetting = setting.ordinal();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {

		super.toBytes(buf);
		buf.writeInt(this._newSetting);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

		super.fromBytes(buf);
		this._newSetting = buf.readInt();
	}

	private int _newSetting;
	
	public static class Handler extends ReactorMessageServer.Handler<ReactorChangeWasteEjectionMessage> {

		@Override
		protected void processReactorMessage(ReactorChangeWasteEjectionMessage message, MessageContext ctx, MultiblockReactor reactor) {
			reactor.setWasteEjection(MultiblockReactor.s_EjectionSettings[message._newSetting]);
		}
	}
}
