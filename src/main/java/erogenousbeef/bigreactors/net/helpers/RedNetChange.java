/* TODO put back in when MineFactory Reloaded is available for MC 1.9.x
package erogenousbeef.bigreactors.net.helpers;

import io.netty.buffer.ByteBuf;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorRedNetPort.CircuitType;
import net.minecraft.util.math.BlockPos;

public class RedNetChange {
	int channelID;
	CircuitType circuitType;
	boolean pulseOrToggle;
	BlockPos coord;
	
	public RedNetChange(int channelID, CircuitType circuitType, boolean pulseOrToggle, BlockPos coord) {
		this.channelID = channelID;
		this.circuitType = circuitType;
		this.pulseOrToggle = pulseOrToggle;
		this.coord = coord;
	}
	
	public static RedNetChange fromBytes(ByteBuf buf) {
		int channelID = buf.readInt();
		CircuitType type = CircuitType.s_Types[buf.readInt()];
		boolean pulseOrToggle = false;

		if(CircuitType.canBeToggledBetweenPulseAndNormal(type)) {
			pulseOrToggle = buf.readBoolean();
		}

		BlockPos coord = null;
		if(CircuitType.hasCoordinate(type)) {
			boolean coordNull = buf.readBoolean();
			if(!coordNull) {
				coord = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
			}
		}
		
		return new RedNetChange(channelID, type, pulseOrToggle, coord);
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(channelID);
		buf.writeInt(circuitType.ordinal());
		
		if(CircuitType.canBeToggledBetweenPulseAndNormal(circuitType)) {
			buf.writeBoolean(pulseOrToggle);
		}
		
		if(CircuitType.hasCoordinate(circuitType)) {
			buf.writeBoolean(coord == null);
			if(coord != null) {
				buf.writeInt(coord.getX());
				buf.writeInt(coord.getY());
				buf.writeInt(coord.getZ());
			}
		}
	}

	public int getChannel() { return channelID; }
	public CircuitType getType() { return this.circuitType; }
	public BlockPos getCoord() { return coord; }
	public boolean getPulseOrToggle() { return this.pulseOrToggle; }
}
*/