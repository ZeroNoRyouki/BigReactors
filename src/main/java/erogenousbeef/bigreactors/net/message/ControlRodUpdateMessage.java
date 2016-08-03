package erogenousbeef.bigreactors.net.message;

import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ControlRodUpdateMessage extends ModTileEntityMessage {

    public ControlRodUpdateMessage() {
        this._insertion = 0;
    }
    
    public ControlRodUpdateMessage(BlockPos position, short controlRodInsertion) {

    	super(position);
        this._insertion = controlRodInsertion;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {

    	super.fromBytes(buffer);
        this._insertion = buffer.readShort();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
    	super.toBytes(buffer);
        buffer.writeShort(this._insertion);
    }

    private short _insertion;

    public static class Handler extends ModTileEntityMessageHandlerClient<ControlRodUpdateMessage> {

        @Override
        protected void processTileEntityMessage(ControlRodUpdateMessage message, MessageContext ctx, TileEntity tileEntity) {

            if (tileEntity instanceof TileEntityReactorControlRod)
                ((TileEntityReactorControlRod)tileEntity).onControlRodUpdate(message._insertion);
        }
    }
}
