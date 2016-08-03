package erogenousbeef.bigreactors.net.message;

import erogenousbeef.bigreactors.common.tileentity.base.TileEntityBeefBase;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DeviceUpdateMessage extends ModTileEntityMessage {

    public DeviceUpdateMessage() {
        this._compound = null;
    }
    
    public DeviceUpdateMessage(BlockPos position, NBTTagCompound compound) {

    	super(position);
        this._compound = compound;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {

    	super.fromBytes(buffer);
        this._compound = ByteBufUtils.readTag(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer) {

    	super.toBytes(buffer);
        ByteBufUtils.writeTag(buffer, this._compound);
    }

    private NBTTagCompound _compound;

    public static class Handler extends ModTileEntityMessageHandlerClient<DeviceUpdateMessage> {

        @Override
        protected void processTileEntityMessage(DeviceUpdateMessage message, MessageContext ctx, TileEntity tileEntity) {

            if (tileEntity instanceof TileEntityBeefBase)
                ((TileEntityBeefBase)tileEntity).onReceiveUpdate(message._compound);
        }
    }
}
