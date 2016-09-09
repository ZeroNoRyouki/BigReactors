package erogenousbeef.bigreactors.net.message;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorAccessPort;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReactorAccessPortChangeDirectionMessage extends ModTileEntityMessage {

	public ReactorAccessPortChangeDirectionMessage() {
		this._newSetting = true;
	}

	public ReactorAccessPortChangeDirectionMessage(TileEntityReactorAccessPort port, boolean inlet) {

		super(port);
		this._newSetting = inlet;
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {

		super.toBytes(buffer);
		buffer.writeBoolean(this._newSetting);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {

		super.fromBytes(buffer);
		this._newSetting = buffer.readBoolean();
	}

	private boolean _newSetting;

	public static class Handler extends ModTileEntityMessageHandlerServer<ReactorAccessPortChangeDirectionMessage> {

		@Override
		protected void processTileEntityMessage(ReactorAccessPortChangeDirectionMessage message, MessageContext ctx, TileEntity tileEntity) {

			if (tileEntity instanceof TileEntityReactorAccessPort)
				((TileEntityReactorAccessPort)tileEntity).setDirection(IInputOutputPort.Direction.from(message._newSetting), true);
		}
	}
}
