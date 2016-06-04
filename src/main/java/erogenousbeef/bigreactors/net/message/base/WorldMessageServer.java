package erogenousbeef.bigreactors.net.message.base;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * A user-generated message to the server which is grounded in world-space.
 * Generally, such messages will be sent FROM the client.
 * @author Erogenous Beef
 */
public abstract class WorldMessageServer extends WorldMessage {
	protected WorldMessageServer() { super(); }
	protected WorldMessageServer(BlockPos position) { super(position.getX(), position.getY(), position.getZ()); }

	public abstract static class Handler<M extends WorldMessageServer> extends WorldMessage.Handler<M> {
		@Override 
		protected World getWorld(MessageContext ctx) {
			return ctx.getServerHandler().playerEntity.worldObj;
		}
	}
}
