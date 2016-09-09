package erogenousbeef.bigreactors.net.message;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.tileentity.base.TileEntityBeefBase;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * A message signifying that a user would like to change
 * the inventory/fluid exposure status of the side of a block.
 * @author Erogenous Beef
 *
 */
public class DeviceChangeExposureMessage extends ModTileEntityMessage {

	public DeviceChangeExposureMessage() {
	}

	public DeviceChangeExposureMessage(BlockPos position, int side, boolean increment) {

		super(position);
		this._side = side;
		this._increment = increment;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {

		super.fromBytes(buffer);
		this._side = buffer.readInt();
		this._increment = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer) {

		super.toBytes(buffer);
		buffer.writeInt(this._side);
		buffer.writeBoolean(this._increment);
	}

	private int _side;
	private boolean _increment;

	public static class Handler extends ModTileEntityMessageHandlerServer<DeviceChangeExposureMessage> {

		@Override
		protected void processTileEntityMessage(DeviceChangeExposureMessage message, MessageContext ctx, TileEntity tileEntity) {

			if (tileEntity instanceof TileEntityBeefBase) {

				TileEntityBeefBase beefTe = (TileEntityBeefBase)tileEntity;
				EnumFacing side = EnumFacing.VALUES[message._side];

				if (message._increment)
					beefTe.incrSide(side);
				else
					beefTe.decrSide(side);

			} else {

				BlockPos position = tileEntity.getPos();

				BRLog.warning("Received SideChangeMessage for TE at %d, %d, %d, but it was not a TE with an iterable side exposure!",
						position.getX(), position.getY(), position.getZ());
			}
		}
	}
}
