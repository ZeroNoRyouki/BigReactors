package erogenousbeef.bigreactors.common.multiblock;

public interface IPowerProvider {

    /**
     * Check if this provider is connected to a consumer device or a power network
     *
     * @return true if the provider is connected, false otherwise
     */
    boolean isProviderConnected();

    /**
     * Provide the given amount of power to the connected consumer device or a power network
     *
     * @param units power units to provide
     * @return power units remaining after consumption
     */
    long onProvidePower(long units);
}
