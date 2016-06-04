package erogenousbeef.bigreactors.utils.intermod;

import net.minecraftforge.fml.common.Optional;
import dan200.computercraft.api.ComputerCraftAPI;
import erogenousbeef.bigreactors.common.BigReactors;

public class ModHelperComputerCraft extends ModHelperBase {

	@Optional.Method(modid = "ComputerCraft")
	@Override
	public void register() {
		ComputerCraftAPI.registerPeripheralProvider(BigReactors.blockReactorPart);
		ComputerCraftAPI.registerPeripheralProvider(BigReactors.blockTurbinePart);
	}
}
