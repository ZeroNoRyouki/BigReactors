package erogenousbeef.bigreactors.common.multiblock.tileentity.creative;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePart;
import erogenousbeef.bigreactors.init.BrFluids;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityTurbineCreativeSteamGenerator extends TileEntityTurbinePart implements ITickableMultiblockPart {

	public TileEntityTurbineCreativeSteamGenerator() {
		super();
	}

	@Override
	public void onMultiblockServerTick() {

		final MultiblockTurbine turbine = this.getTurbine();

		if (null != turbine && turbine.getActive()) {

			FluidStack steam = new FluidStack(BrFluids.fluidSteam, turbine.getMaxIntakeRate());

			turbine.getFluidHandler(IInputOutputPort.Direction.Input).fill(steam, true);
		}
	}
}
