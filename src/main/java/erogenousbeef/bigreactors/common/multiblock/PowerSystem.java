package erogenousbeef.bigreactors.common.multiblock;

public enum PowerSystem {

    Unknown("UNKNOWN", "n/a", 0),
    RedstoneFlux("Redstone Flux", "RF", 10000000),
    Tesla("Testa", "T", 20000000);

    PowerSystem(String fullName, String unitOfMeasure, long maxEnergyStored) {

        this.fullName = fullName;
        this.unitOfMeasure = unitOfMeasure;
        this.maxEnergyStored = maxEnergyStored;
    }

    public final String fullName;
    public final String unitOfMeasure;
    public final long maxEnergyStored;
}
