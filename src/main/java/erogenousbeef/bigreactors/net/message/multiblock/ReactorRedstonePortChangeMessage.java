package erogenousbeef.bigreactors.net.message.multiblock;

import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorRedstonePort;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReactorRedstonePortChangeMessage extends ModTileEntityMessage {

    public ReactorRedstonePortChangeMessage() {
    }

    public ReactorRedstonePortChangeMessage(TileEntityReactorRedstonePort port, int newCircut, int newLevel, boolean newGt, boolean pulse) {

        super(port);
        this._newCircut = newCircut;
        this._newLevel = newLevel;
        this._newGreaterThan = newGt;
        this._pulse = pulse;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {

        super.fromBytes(buffer);
        this._newCircut = buffer.readInt();
        this._newLevel = buffer.readInt();
        this._newGreaterThan = buffer.readBoolean();
        this._pulse = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer) {

        super.toBytes(buffer);
        buffer.writeInt(this._newCircut);
        buffer.writeInt(this._newLevel);
        buffer.writeBoolean(this._newGreaterThan);
        buffer.writeBoolean(this._pulse);
    }

    private int _newCircut;
    private int _newLevel;
    private boolean _newGreaterThan;
    private boolean _pulse;

    public static class Handler extends ModTileEntityMessageHandlerServer<ReactorRedstonePortChangeMessage> {

        @Override
        protected void processTileEntityMessage(ReactorRedstonePortChangeMessage message, MessageContext ctx, TileEntity tileEntity) {

            if (tileEntity instanceof TileEntityReactorRedstonePort) {

                TileEntityReactorRedstonePort port = (TileEntityReactorRedstonePort)tileEntity;

                port.onReceiveUpdatePacket(message._newCircut, message._newLevel, message._newGreaterThan, message._pulse);
            }
        }
    }
}
