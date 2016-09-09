package erogenousbeef.bigreactors.net.message.base;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class ReactorMessageClient extends ModTileEntityMessage {

	protected final MultiblockReactor REACTOR;

	protected ReactorMessageClient() {
		this.REACTOR = null;
	}

	protected ReactorMessageClient(MultiblockReactor reactor) {

		super(reactor.getReferenceCoord());
		this.REACTOR = reactor;
	}

	public static abstract class Handler<MessageT extends ReactorMessageClient> extends ModTileEntityMessageHandlerClient<MessageT> {

		@Override
		protected void processTileEntityMessage(MessageT message, MessageContext ctx, TileEntity tileEntity) {

			BlockPos position = null != tileEntity ? tileEntity.getPos() : null;

			if (tileEntity instanceof TileEntityReactorPartBase) {

				MultiblockReactor reactor = ((TileEntityReactorPartBase)tileEntity).getReactorController();

				if (null != reactor) {

					this.processReactorMessage(message, ctx, reactor);

				} else {

					BRLog.error("Received ReactorMessageClient for a reactor part @ %d, %d, %d which has no attached reactor",
							position.getX(), position.getY(), position.getZ());
				}
			} else if (null != position) {

				BRLog.error("Received ReactorMessageClient for a non-reactor-part block @ %d, %d, %d",
						position.getX(), position.getY(), position.getZ());
			}
		}

		protected abstract void processReactorMessage(final MessageT message, final MessageContext ctx, final MultiblockReactor reactor);
	}
}
