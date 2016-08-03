package erogenousbeef.bigreactors.net.message.multiblock;

import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.net.message.base.TurbineMessageServer;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TurbineChangeVentMessage extends TurbineMessageServer {

	public TurbineChangeVentMessage() {
		this._newSetting = 0;
	}

	public TurbineChangeVentMessage(MultiblockTurbine turbine, MultiblockTurbine.VentStatus newStatus) {

		super(turbine);
		this._newSetting = newStatus.ordinal();
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
	
	public static class Handler extends TurbineMessageServer.Handler<TurbineChangeVentMessage> {

		@Override
		protected void processTurbineMessage(TurbineChangeVentMessage message, MessageContext ctx, MultiblockTurbine turbine) {
			turbine.setVentStatus(MultiblockTurbine.s_VentStatuses[message._newSetting], true);
		}
	}
}
