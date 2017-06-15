package erogenousbeef.bigreactors.common.compat;

import dan200.computercraft.api.ComputerCraftAPI;
import erogenousbeef.bigreactors.init.BrBlocks;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class ModComputerCraft extends ModCompact {

    @Override
    public void onPostInit(FMLPostInitializationEvent fmlPostInitializationEvent) {

        ComputerCraftAPI.registerPeripheralProvider(BrBlocks.reactorComputerPort);
        ComputerCraftAPI.registerPeripheralProvider(BrBlocks.turbineComputerPort);
    }
}
