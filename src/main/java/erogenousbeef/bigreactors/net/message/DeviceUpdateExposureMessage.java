package erogenousbeef.bigreactors.net.message;

import erogenousbeef.bigreactors.common.tileentity.base.TileEntityBeefBase;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DeviceUpdateExposureMessage extends ModTileEntityMessage {

	public DeviceUpdateExposureMessage() {
		this._exposures = null;
	}
	
	public DeviceUpdateExposureMessage(BlockPos position, int[] exposures) {

		super(position);
		this._exposures = new int[exposures.length];
		System.arraycopy(exposures, 0, this._exposures, 0, this._exposures.length);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {

		super.toBytes(buf);
		buf.writeInt(this._exposures.length);

		for (int i = 0; i < this._exposures.length; i++)
			buf.writeInt(this._exposures[i]);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

		super.fromBytes(buf);

		int numExposures = buf.readInt();

		assert(numExposures > 0);
		this._exposures = new int[numExposures];

		for(int i = 0; i < numExposures; i++)
			this._exposures[i] = buf.readInt();
	}

	int[] _exposures;

	public static class Handler extends ModTileEntityMessageHandlerClient<DeviceUpdateExposureMessage> {

		@Override
		protected void processTileEntityMessage(DeviceUpdateExposureMessage message, MessageContext ctx, TileEntity tileEntity) {

			if (tileEntity instanceof TileEntityBeefBase)
				((TileEntityBeefBase)tileEntity).setSides(message._exposures);
		}
	}
}
