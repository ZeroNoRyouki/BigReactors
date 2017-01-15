package erogenousbeef.bigreactors.api;

public final class EnergyConversion {

    public static float getRFFromVolumeAndTemp(int volume, float temperature) {
        return temperature * (float)volume * RFPerCentigradePerUnitVolume;
    }

    public static float getTempFromVolumeAndRF(int volume, float rf) {
        return rf / ((float)volume * RFPerCentigradePerUnitVolume);
    }

    private static float RFPerCentigradePerUnitVolume = 10f;
}
