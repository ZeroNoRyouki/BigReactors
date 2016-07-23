package erogenousbeef.bigreactors.utils.intermod;

import erogenousbeef.bigreactors.init.BrBlocks;
import net.minecraftforge.fml.common.Optional;
import dan200.computercraft.api.ComputerCraftAPI;

public class ModHelperComputerCraft extends ModHelperBase {

	@Optional.Method(modid = "ComputerCraft")
	@Override
	public void register() {

		ComputerCraftAPI.registerPeripheralProvider(BrBlocks.reactorComputerPort);
        // TODO add back when turbine computer port is ready
		//ComputerCraftAPI.registerPeripheralProvider(BrBlocks.blockTurbinePart);
	}
}