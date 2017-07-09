package erogenousbeef.bigreactors.net.message.multiblock;

import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorRedNetPort;
import erogenousbeef.bigreactors.net.helpers.RedNetChange;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReactorRedNetPortChangeMessage extends ModTileEntityMessage {

    private RedNetChange[] changes;

    public ReactorRedNetPortChangeMessage() { super(); changes = null; }

    public ReactorRedNetPortChangeMessage(TileEntityReactorRedNetPort port, RedNetChange[] changes) {

        super(port);
        this.changes = changes;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        int numChanges = buf.readInt();
        if(numChanges < 1) { return; }

        changes = new RedNetChange[numChanges];
        for(int i = 0; i < numChanges; i++) {
            changes[i] = RedNetChange.fromBytes(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        if(changes == null || changes.length < 1) {
            buf.writeInt(0);
            return;
        }

        buf.writeInt(changes.length);
        for(int i = 0; i < changes.length; i++) {
            changes[i].toBytes(buf);
        }
    }

    public static class Handler extends ModTileEntityMessageHandlerServer<ReactorRedNetPortChangeMessage> {

        @Override
        protected void processTileEntityMessage(ReactorRedNetPortChangeMessage message, MessageContext ctx, TileEntity tileEntity) {

            if (tileEntity instanceof TileEntityReactorRedNetPort) {

                final TileEntityReactorRedNetPort port = (TileEntityReactorRedNetPort)tileEntity;

                port.onCircuitUpdate(message.changes);
            }
        }
    }
}
