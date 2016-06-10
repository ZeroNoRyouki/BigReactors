package erogenousbeef.bigreactors.common.multiblock.helpers;

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
}
