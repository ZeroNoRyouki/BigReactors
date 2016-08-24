package erogenousbeef.bigreactors.common.multiblock;

public enum PowerSystem {

    RedstoneFlux("Redstone Flux", "RF", 10000000),
    Tesla("Testa", "T", 10000000);

    PowerSystem(String fullName, String unitOfMeasure, long maxEnergyStored) {

        this.fullName = fullName;
        this.unitOfMeasure = unitOfMeasure;
        this.maxCapacity = maxEnergyStored;
    }

    public final String fullName;
    public final String unitOfMeasure;
    public final long maxCapacity;
}
