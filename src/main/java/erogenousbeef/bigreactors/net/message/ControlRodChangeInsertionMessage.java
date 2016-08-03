package erogenousbeef.bigreactors.net.message;

import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ControlRodChangeInsertionMessage extends ModTileEntityMessage {

	public ControlRodChangeInsertionMessage() {

		this._amount = 0;
		this._changeAll = false;
	}

	public ControlRodChangeInsertionMessage(BlockPos position, int amount, boolean all) {

		super(position);
		this._amount = amount;
		this._changeAll = all;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {

		super.fromBytes(buffer);
		this._amount = buffer.readInt();
		this._changeAll = buffer.readBoolean();
	}
	
	@Override
	public void toBytes(ByteBuf buffer) {

		super.toBytes(buffer);
		buffer.writeInt(this._amount);
		buffer.writeBoolean(this._changeAll);
	}

	protected int _amount;
	protected boolean _changeAll;

	public static class Handler extends ModTileEntityMessageHandlerServer<ControlRodChangeInsertionMessage> {

		@Override
		protected void processTileEntityMessage(ControlRodChangeInsertionMessage message, MessageContext ctx, TileEntity tileEntity) {

			if (tileEntity instanceof TileEntityReactorControlRod) {

				TileEntityReactorControlRod rod = (TileEntityReactorControlRod)tileEntity;
				int newInsertion = rod.getControlRodInsertion() + (short)message._amount;

				if (message._changeAll && rod.getReactorController() != null)
					rod.getReactorController().setAllControlRodInsertionValues(newInsertion);
				else
					rod.setControlRodInsertion((short)newInsertion);
			}
		}
	}
}
