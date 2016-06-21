package erogenousbeef.bigreactors.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import cofh.api.tileentity.IReconfigurableFacing;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.net.message.base.WorldMessageClient;
import zero.mods.zerocore.util.WorldHelper;

public class DeviceUpdateRotationMessage extends WorldMessageClient {
    private EnumFacing newOrientation;

    public DeviceUpdateRotationMessage() { super(); newOrientation = EnumFacing.NORTH; }
    
    public DeviceUpdateRotationMessage(BlockPos position, EnumFacing newOrientation) {
    	super(position);
        this.newOrientation = newOrientation;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	super.fromBytes(buf);
        newOrientation = EnumFacing.VALUES[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	super.toBytes(buf);
        buf.writeInt(newOrientation.getIndex());
    }

    public static class Handler extends WorldMessageClient.Handler<DeviceUpdateRotationMessage> {
        @Override
        public IMessage handleMessage(DeviceUpdateRotationMessage message, MessageContext ctx, TileEntity te) {
            if(te instanceof IReconfigurableFacing) {
                ((IReconfigurableFacing)te).setFacing(message.newOrientation);
                WorldHelper.notifyBlockUpdate(this.getWorld(ctx), new BlockPos(message.x, message.y, message.z), null, null);
            }
            return null;
        }
    }
}
