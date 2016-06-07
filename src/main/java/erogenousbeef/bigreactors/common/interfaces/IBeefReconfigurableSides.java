package erogenousbeef.bigreactors.common.interfaces;

import cofh.api.tileentity.IReconfigurableSides;

public interface IBeefReconfigurableSides extends IReconfigurableSides {

	/**
	 * Return the icon which should be used for a given side.
	 * Note: Passes the unrotated world side.
	 */
	// TODO icon replacement?
	public Object getIconForSide(int referenceSide);
	
}
