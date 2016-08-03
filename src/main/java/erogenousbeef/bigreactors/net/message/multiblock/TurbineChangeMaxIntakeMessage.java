package erogenousbeef.bigreactors.net.message.multiblock;

import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.net.message.base.TurbineMessageServer;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TurbineChangeMaxIntakeMessage extends TurbineMessageServer {

	public TurbineChangeMaxIntakeMessage() {
		this._newSetting = MultiblockTurbine.MAX_PERMITTED_FLOW;
	}

	public TurbineChangeMaxIntakeMessage(MultiblockTurbine turbine, int newSetting) {

		super(turbine);
		this._newSetting = newSetting;
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
	
	public static class Handler extends TurbineMessageServer.Handler<TurbineChangeMaxIntakeMessage> {

		@Override
		protected void processTurbineMessage(TurbineChangeMaxIntakeMessage message, MessageContext ctx, MultiblockTurbine turbine) {
			turbine.setMaxIntakeRate(message._newSetting);
		}
	}
}
