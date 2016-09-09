package erogenousbeef.bigreactors.net.message.base;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessage;
import it.zerono.mods.zerocore.lib.network.ModTileEntityMessageHandlerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class TurbineMessageServer extends ModTileEntityMessage {

	protected final MultiblockTurbine TURBINE;

	protected TurbineMessageServer() {
		this.TURBINE = null;
	}

	protected TurbineMessageServer(MultiblockTurbine turbine) {

		super(turbine.getReferenceCoord());
		this.TURBINE = turbine;
	}

	public static abstract class Handler<MessageT extends TurbineMessageServer> extends ModTileEntityMessageHandlerServer<MessageT> {

		@Override
		protected void processTileEntityMessage(MessageT message, MessageContext ctx, TileEntity tileEntity) {

			BlockPos position = null != tileEntity ? tileEntity.getPos() : null;

			if (tileEntity instanceof TileEntityTurbinePartBase) {

				MultiblockTurbine turbine = ((TileEntityTurbinePartBase)tileEntity).getTurbine();

				if (null != turbine) {

					this.processTurbineMessage(message, ctx, turbine);

				} else {

					BRLog.error("Received TurbineMessageServer for a turbine part @ %d, %d, %d which has no attached turbine",
							position.getX(), position.getY(), position.getZ());
				}
			} else if (null != position) {

				BRLog.error("Received TurbineMessageServer for a non-turbine-part block @ %d, %d, %d",
						position.getX(), position.getY(), position.getZ());
			}
		}

		protected abstract void processTurbineMessage(final MessageT message, final MessageContext ctx, final MultiblockTurbine turbine);
	}
}
