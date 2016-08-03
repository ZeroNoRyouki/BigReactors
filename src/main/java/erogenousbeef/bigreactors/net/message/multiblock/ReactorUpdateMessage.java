package erogenousbeef.bigreactors.net.message.multiblock;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.net.message.base.ReactorMessageClient;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReactorUpdateMessage extends ReactorMessageClient {

	public ReactorUpdateMessage() {
	}

	public ReactorUpdateMessage(MultiblockReactor reactor) {
		super(reactor);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

		super.fromBytes(buf);
		this._data = buf.readBytes(buf.readableBytes());
	}
	
	@Override
	public void toBytes(ByteBuf buf) {

		super.toBytes(buf);
		this.REACTOR.serialize(buf);
	}

	private ByteBuf _data;
	
	public static class Handler extends ReactorMessageClient.Handler<ReactorUpdateMessage> {

		@Override
		protected void processReactorMessage(ReactorUpdateMessage message, MessageContext ctx, MultiblockReactor reactor) {
			reactor.deserialize(message._data);
		}
	}
}
