package erogenousbeef.bigreactors.common.multiblock.computer;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorComputerPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

public abstract class ReactorComputer extends MachineComputer {

    ReactorComputer(@Nonnull TileEntityReactorComputerPort computerPort) {
        this._computerPort = computerPort;
    }

    @Nonnull
    protected TileEntityReactorComputerPort getComputerPort() {
        return this._computerPort;
    }

    // Multiblock methods

    boolean getMultiblockAssembled() {
        return this._computerPort.isMachineAssembled();
    }

    boolean getConnected() {
        return this._computerPort.isConnected();
    }

    // Reactor methods

    int getEnergyStored() throws Exception {
        return (int)this.getReactorControllerOrFail().getEnergyStored();
    }

    int getNumberOfControlRods() throws Exception {
        return this.getReactorControllerOrFail().getFuelRodCount();
    }

    boolean getActive() throws Exception {
        return this.getReactorControllerOrFail().getActive();
    }

    float getFuelTemperature() throws Exception {
        return this.getReactorControllerOrFail().getFuelHeat();
    }

    float getCasingTemperature() throws Exception {
        return this.getReactorControllerOrFail().getReactorHeat();
    }

    int getFuelAmount() throws Exception {
        return this.getReactorControllerOrFail().getFuelAmount();
    }

    int getWasteAmount() throws Exception {
        return this.getReactorControllerOrFail().getWasteAmount();
    }

    int getFuelAmountMax() throws Exception {
        return this.getReactorControllerOrFail().getCapacity();
    }

    String getControlRodName(int index) throws Exception {
        return this.getControlRodByIndex(index).getName();
    }

    int getControlRodLevel(int index) throws Exception {
        return this.getControlRodByIndex(index).getControlRodInsertion();
    }

    float getEnergyProducedLastTick() throws Exception {
        return this.getReactorControllerOrFail().getEnergyGeneratedLastTick();
    }

    float getHotFluidProducedLastTick() throws Exception {

        final MultiblockReactor reactor = this.getReactorControllerOrFail();

        return reactor.isPassivelyCooled() ? 0f : reactor.getEnergyGeneratedLastTick();
    }

    boolean isActivelyCooled() throws Exception {
        return !this.getReactorControllerOrFail().isPassivelyCooled();
    }

    int getCoolantAmount() throws Exception {
        return this.getReactorControllerOrFail().getCoolantContainer().getCoolantAmount();
    }

    int getCoolantAmountMax() throws Exception {
        return this.getReactorControllerOrFail().getCoolantContainer().getCapacity();
    }

    String getCoolantType() throws Exception {

        final Fluid fluidType = this.getReactorControllerOrFail().getCoolantContainer().getCoolantType();

        return null == fluidType ? null : fluidType.getName();
    }

    int getHotFluidAmount() throws Exception {
        return this.getReactorControllerOrFail().getCoolantContainer().getVaporAmount();
    }

    int getHotFluidAmountMax() throws Exception {
        return this.getReactorControllerOrFail().getCoolantContainer().getCapacity();
    }

    String getHotFluidType() throws Exception {

        final Fluid fluidType = this.getReactorControllerOrFail().getCoolantContainer().getVaporType();

        return null == fluidType ? null : fluidType.getName();
    }

    float getFuelReactivity() throws Exception {
        return this.getReactorControllerOrFail().getFuelFertility() * 100f;
    }

    float getFuelConsumedLastTick() throws Exception {
        return this.getReactorControllerOrFail().getFuelConsumedLastTick();
    }

    BlockPos getMinimumCoordinate() throws Exception {
        return this.getReactorControllerOrFail().getMinimumCoord();
    }

    BlockPos getMaximumCoordinate() throws Exception {
        return this.getReactorControllerOrFail().getMaximumCoord();
    }

    BlockPos getControlRodLocation(int index) throws Exception {

        final BlockPos reactorMinCoord = this.getReactorControllerOrFail().getMinimumCoord();
        final BlockPos rodCoord = this.getControlRodByIndex(index).getWorldPosition();

        return rodCoord.subtract(reactorMinCoord);
    }

    void setActive(boolean active) throws Exception {
        this.getReactorControllerOrFail().setActive(active);
    }

    void setAllControlRodLevels(int newLevel) throws Exception {
        this.getReactorControllerOrFail().setAllControlRodInsertionValues(newLevel);
    }

    void setControlRodLevel(int index, int newLevel) throws Exception {
        this.getControlRodByIndex(index).setControlRodInsertion((short)newLevel);
    }

    void setControlRodName(int index, String newName) throws Exception {
        this.getControlRodByIndex(index).setName(newName);
    }

    void doEjectWaste() throws Exception {
        this.getReactorControllerOrFail().ejectWaste(false, null);
    }

    void doEjectFuel() throws Exception {
        this.getReactorControllerOrFail().ejectFuel(false, null);
    }

    long getEnergyCapacity() throws Exception {
        return this.getReactorControllerOrFail().getEnergyCapacity();
    }

    private MultiblockReactor getReactorControllerOrFail() throws Exception {

        if (!this._computerPort.isConnected())
            throw new Exception("Unable to access reactor - port is not connected");

        return this._computerPort.getReactorController();
    }

    private TileEntityReactorControlRod getControlRodByIndex(int index) throws Exception {

        final TileEntityReactorControlRod controlRod = this.getReactorControllerOrFail().getControlRodByIndex(index);

        if (null == controlRod)
            throw new IndexOutOfBoundsException(String.format("Invalid argument %d, control rod index is out of bounds", index));

        return controlRod;
    }

    private final TileEntityReactorComputerPort _computerPort;
}
