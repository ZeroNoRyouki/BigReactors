package erogenousbeef.bigreactors.net.message.base;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A message to a client user which is grounded in world-space.
 * Generally, such messages will be sent FROM the server.
 * @author Erogenous Beef
 */
public abstract class WorldMessageClient extends WorldMessage {
	protected WorldMessageClient() { super(); }
	protected WorldMessageClient(BlockPos position) {
		super(position.getX(), position.getY(), position.getZ());
	}

	public abstract static class Handler<M extends WorldMessageClient> extends WorldMessage.Handler<M> {
		@SideOnly(Side.CLIENT)
		@Override
		protected World getWorld(MessageContext ctx) {
			return FMLClientHandler.instance().getClient().theWorld;
		}
	}
}
