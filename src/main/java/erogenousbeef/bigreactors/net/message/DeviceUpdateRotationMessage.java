package erogenousbeef.bigreactors.net.message;

import cofh.api.tileentity.IReconfigurableFacing;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerClient;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DeviceUpdateRotationMessage extends ModTileEntityMessage {

    public DeviceUpdateRotationMessage() {
        this._newOrientation = EnumFacing.NORTH;
    }
    
    public DeviceUpdateRotationMessage(BlockPos position, EnumFacing newOrientation) {

        super(position);
        this._newOrientation = newOrientation;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {

    	super.fromBytes(buffer);
        this._newOrientation = EnumFacing.VALUES[buffer.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buffer) {

    	super.toBytes(buffer);
        buffer.writeInt(this._newOrientation.getIndex());
    }

    private EnumFacing _newOrientation;

    public static class Handler extends ModTileEntityMessageHandlerClient<DeviceUpdateRotationMessage> {

        @Override
        protected void processTileEntityMessage(DeviceUpdateRotationMessage message, MessageContext ctx, TileEntity tileEntity) {

            if (tileEntity instanceof IReconfigurableFacing) {

                ((IReconfigurableFacing)tileEntity).setFacing(message._newOrientation);
                WorldHelper.notifyBlockUpdate(this.getWorld(ctx), message.getPos(), null, null);
            }
        }
    }
}
