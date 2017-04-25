package erogenousbeef.bigreactors.common.compat;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class ModComputerCraft extends ModCompact {

    @Override
    public void onPostInit(FMLPostInitializationEvent fmlPostInitializationEvent) {

        // TODO add back when ComputerCraft is ready for 1.11.2
        /*
        ComputerCraftAPI.registerPeripheralProvider(BrBlocks.reactorComputerPort);
        ComputerCraftAPI.registerPeripheralProvider(BrBlocks.blockTurbinePart);
        */
    }
}
