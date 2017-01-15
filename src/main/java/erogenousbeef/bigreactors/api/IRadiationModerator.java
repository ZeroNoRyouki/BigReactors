package erogenousbeef.bigreactors.api;

import erogenousbeef.bigreactors.api.data.RadiationData;
import erogenousbeef.bigreactors.api.data.RadiationPacket;

public interface IRadiationModerator {
	void moderateRadiation(RadiationData returnData, RadiationPacket radiation);
}
