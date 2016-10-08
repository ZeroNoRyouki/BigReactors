package erogenousbeef.bigreactors.common.multiblock.helpers;

import erogenousbeef.bigreactors.common.multiblock.RotorBladeState;
import erogenousbeef.bigreactors.common.multiblock.RotorShaftState;
import net.minecraft.util.EnumFacing;

public class RotorInfo {
	// Location of bearing
	public int x, y, z;
	
	// Rotor direction
	public EnumFacing rotorDirection;
	
	// Rotor length
	public int rotorLength = 0;
	
	// Array of arrays, containing rotor lengths
	public int[][] bladeLengths = null;

	public RotorShaftState[] shaftStates = null;
	public RotorBladeState[][] bladeStates = null;
}
