package erogenousbeef.bigreactors.net.message;

import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ControlRodChangeNameMessage extends ModTileEntityMessage {

    public ControlRodChangeNameMessage() {
        this._name = null;
    }
    
    public ControlRodChangeNameMessage(BlockPos position, String name) {

    	super(position);
        this._name = name;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {

    	super.fromBytes(buffer);
        this._name = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer) {

    	super.toBytes(buffer);
        ByteBufUtils.writeUTF8String(buffer, this._name);
    }

    private String _name;

    public static class Handler extends ModTileEntityMessageHandlerServer<ControlRodChangeNameMessage> {

        @Override
        protected void processTileEntityMessage(ControlRodChangeNameMessage message, MessageContext ctx, TileEntity tileEntity) {

            if (tileEntity instanceof TileEntityReactorControlRod)
                ((TileEntityReactorControlRod)tileEntity).setName(message._name);
        }
    }
}
