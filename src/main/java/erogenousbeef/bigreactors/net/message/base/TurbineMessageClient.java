package erogenousbeef.bigreactors.net.message.base;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;

public class TurbineMessageClient extends WorldMessageClient {
	protected MultiblockTurbine turbine;
	
	protected TurbineMessageClient() { super(); turbine = null; }
	protected TurbineMessageClient(MultiblockTurbine turbine, BlockPos referenceCoord) {
		super(referenceCoord);
		this.turbine = turbine;
	}
	protected TurbineMessageClient(MultiblockTurbine turbine) {
		this(turbine, turbine.getReferenceCoord());
	}
	
	public static abstract class Handler<M extends TurbineMessageClient> extends WorldMessageClient.Handler<M> {
		protected abstract IMessage handleMessage(M message, MessageContext ctx, MultiblockTurbine turbine);

		@Override
		protected IMessage handleMessage(M message, MessageContext ctx, TileEntity te) {
			if(te instanceof TileEntityTurbinePartBase) {
				MultiblockTurbine reactor = ((TileEntityTurbinePartBase)te).getTurbine();
				if(reactor != null) {
					return handleMessage(message, ctx, reactor);
				}
				else {
					BlockPos tePosition = te.getPos();
					BRLog.error("Received TurbineMessageClient for a turbine part @ %d, %d, %d which has no attached turbine",
							tePosition.getX(), tePosition.getY(), tePosition.getZ());
				}
			}
			else {
				BRLog.error("Received TurbineMessageClient for a non-turbine-part block @ %d, %d, %d", message.x, message.y, message.z);
			}
			return null;
		}
	}
}
