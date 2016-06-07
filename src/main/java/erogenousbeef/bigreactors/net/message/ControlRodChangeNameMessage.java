package erogenousbeef.bigreactors.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import erogenousbeef.bigreactors.net.message.base.WorldMessageServer;

public class ControlRodChangeNameMessage extends WorldMessageServer {
    private String name;

    public ControlRodChangeNameMessage() { super(); name = null; }
    
    public ControlRodChangeNameMessage(BlockPos position, String name) {
    	super(position);
        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	super.fromBytes(buf);
        name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, name);
    }

    public static class Handler extends WorldMessageServer.Handler<ControlRodChangeNameMessage> {
    	@Override
    	protected IMessage handleMessage(ControlRodChangeNameMessage message, MessageContext ctx, TileEntity te) {
    		if(te instanceof TileEntityReactorControlRod) {
    			((TileEntityReactorControlRod)te).setName(message.name);
    		}
    		return null;
    	}
    }
}
