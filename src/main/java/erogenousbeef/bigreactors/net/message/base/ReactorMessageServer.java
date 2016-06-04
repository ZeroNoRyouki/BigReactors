package erogenousbeef.bigreactors.net.message.base;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;

public abstract class ReactorMessageServer extends WorldMessageServer {
	MultiblockReactor reactor;
	
	protected ReactorMessageServer() { super(); reactor = null; }
	protected ReactorMessageServer(MultiblockReactor reactor, BlockPos referenceCoord) {
		super(referenceCoord);
		this.reactor = reactor;
	}
	protected ReactorMessageServer(MultiblockReactor reactor) {
		this(reactor, reactor.getReferenceCoord());
	}
	
	public static abstract class Handler<M extends ReactorMessageServer> extends WorldMessageServer.Handler<M> {
		protected abstract IMessage handleMessage(M message, MessageContext ctx, MultiblockReactor reactor);

		@Override
		protected IMessage handleMessage(M message, MessageContext ctx, TileEntity te) {
			if(te instanceof TileEntityReactorPartBase) {
				MultiblockReactor reactor = ((TileEntityReactorPartBase)te).getReactorController();
				if(reactor != null) {
					return handleMessage(message, ctx, reactor);
				}
				else {
					BlockPos tePosition = te.getPos();
					BRLog.error("Received ReactorMessageServer for a reactor part @ %d, %d, %d which has no attached reactor",
							tePosition.getX(), tePosition.getY(), tePosition.getZ());
				}
			}
			else {
				BRLog.error("Received ReactorMessageServer for a non-reactor-part block @ %d, %d, %d", message.x, message.y, message.z);
			}
			return null;
		}
	}
}
