package erogenousbeef.bigreactors.common.multiblock.computer;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineComputerPort;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import javax.annotation.Nonnull;

public class TurbineComputer extends MachineComputer {

    TurbineComputer(@Nonnull TileEntityTurbineComputerPort computerPort) {
        this._computerPort = computerPort;
    }

    @Nonnull
    protected TileEntityTurbineComputerPort getComputerPort() {
        return this._computerPort;
    }

    boolean getConnected() {
        return this._computerPort.isConnected();
    }

    boolean getActive() throws Exception {
        return this.getTurbineControllerOrFail().getActive();
    }

    float getEnergyProducedLastTick() throws Exception {
        return this.getTurbineControllerOrFail().getEnergyGeneratedLastTick();
    }

    long getEnergyStored() throws Exception {
        return this.getTurbineControllerOrFail().getEnergyStored();
    }

    int getFluidAmountMax() throws Exception {
        return MultiblockTurbine.TANK_SIZE;
    }

    int getFluidFlowRate() throws Exception {
        return this.getTurbineControllerOrFail().getFluidConsumedLastTick();
    }

    int getFluidFlowRateMax() throws Exception {
        return this.getTurbineControllerOrFail().getMaxIntakeRate();
    }

    int getFluidFlowRateMaxMax() throws Exception {
        return this.getTurbineControllerOrFail().getMaxIntakeRateMax();
    }

    int getInputAmount() throws Exception {
        return this.getFluidAmount(IInputOutputPort.Direction.Input);
    }

    String getInputType() throws Exception {
        return this.getFluidName(IInputOutputPort.Direction.Input);
    }

    int getOutputAmount() throws Exception {
        return this.getFluidAmount(IInputOutputPort.Direction.Output);
    }

    String getOutputType() throws Exception {
        return this.getFluidName(IInputOutputPort.Direction.Output);
    }

    float getRotorSpeed() throws Exception {
        return this.getTurbineControllerOrFail().getRotorSpeed();
    }

    int getNumberOfBlades() throws Exception {
        return this.getTurbineControllerOrFail().getNumRotorBlades();
    }

    float getBladeEfficiency() throws Exception {
        return this.getTurbineControllerOrFail().getRotorEfficiencyLastTick() * 100f;
    }

    int getRotorMass() throws Exception {
        return this.getTurbineControllerOrFail().getRotorMass();
    }

    boolean getInductorEngaged() throws Exception {
        return this.getTurbineControllerOrFail().getInductorEngaged();
    }

    BlockPos getMinimumCoordinate() throws Exception {
        return this.getTurbineControllerOrFail().getMinimumCoord();
    }

    BlockPos getMaximumCoordinate() throws Exception {
        return this.getTurbineControllerOrFail().getMaximumCoord();
    }

    void setActive(boolean active) throws Exception {
        this.getTurbineControllerOrFail().setActive(active);
    }

    void setFluidFlowRateMax(int newRate) throws Exception {
        this.getTurbineControllerOrFail().setMaxIntakeRate(newRate);
    }

    void setVentNone() throws Exception {
        this.getTurbineControllerOrFail().setVentStatus(MultiblockTurbine.VentStatus.DoNotVent, true);
    }

    void setVentOverflow() throws Exception {
        this.getTurbineControllerOrFail().setVentStatus(MultiblockTurbine.VentStatus.VentOverflow, true);
    }

    void setVentAll() throws Exception {
        this.getTurbineControllerOrFail().setVentStatus(MultiblockTurbine.VentStatus.VentAll, true);
    }

    void setInductorEngaged(boolean engaged) throws Exception {
        this.getTurbineControllerOrFail().setInductorEngaged(engaged, true);
    }

    long getEnergyCapacity() throws Exception {
        return this.getTurbineControllerOrFail().getEnergyCapacity();
    }

    private MultiblockTurbine getTurbineControllerOrFail() throws Exception {

        if (!this._computerPort.isConnected())
            throw new Exception("Unable to access turbine - port is not connected");

        return this._computerPort.getTurbine();
    }

    private IFluidTankProperties getTankProperties(IInputOutputPort.Direction direction) throws Exception {

        MultiblockTurbine turbine = this.getTurbineControllerOrFail();
        IFluidHandler handler = turbine.getFluidHandler(direction);
        IFluidTankProperties[] properties = handler.getTankProperties();

        return null != properties && properties.length > 0 ? properties[0] : null;
    }

    private int getFluidAmount(IInputOutputPort.Direction direction) throws Exception {

        IFluidTankProperties properties = this.getTankProperties(direction);
        FluidStack stack = null != properties ? properties.getContents() : null;

        return null != stack ? stack.amount : 0;
    }

    private String getFluidName(IInputOutputPort.Direction direction) throws Exception {

        IFluidTankProperties properties = this.getTankProperties(direction);
        FluidStack stack = null != properties ? properties.getContents() : null;
        Fluid fluid = null != stack ? stack.getFluid() : null;

        return null != fluid ? fluid.getName() : null;
    }

    private final TileEntityTurbineComputerPort _computerPort;
}
