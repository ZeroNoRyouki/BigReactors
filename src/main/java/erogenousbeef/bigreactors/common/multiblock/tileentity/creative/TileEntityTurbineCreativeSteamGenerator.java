package erogenousbeef.bigreactors.common.multiblock.tileentity.creative;

import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartStandard;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityTurbineCreativeSteamGenerator extends TileEntityTurbinePartStandard implements ITickableMultiblockPart {

	public TileEntityTurbineCreativeSteamGenerator() {
		super();
	}

	@Override
	public void onMultiblockServerTick() {
		if(isConnected() && getTurbine().getActive()) {
			Fluid steam = FluidRegistry.getFluid("steam");
			
			getTurbine().fill(MultiblockTurbine.TANK_INPUT, new FluidStack(steam, getTurbine().getMaxIntakeRate()), true);
		}
	}
}
