package erogenousbeef.bigreactors.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BREventHandler {

	@SubscribeEvent
	public void chunkSave(ChunkDataEvent.Save saveEvent) {

		if (BigReactors.CONFIG.enableWorldGen) {

			NBTTagCompound saveData = saveEvent.getData();
			
			saveData.setInteger("BigReactorsWorldGen", BigReactors.WORLDGEN_VERSION);
			saveData.setInteger("BigReactorsUserWorldGen", BigReactors.CONFIG.userWorldGenVersion);
		}
	}
	
	@SubscribeEvent
	public void chunkLoad(ChunkDataEvent.Load loadEvent) {

		NBTTagCompound loadData = loadEvent.getData();
		int dimensionId = loadEvent.getWorld().provider.getDimension();

		if (!BigReactors.CONFIG.enableWorldRegeneration || !BigReactors.CONFIG.enableWorldGen ||
			(loadData.getInteger("BigReactorsWorldGen") == BigReactors.WORLDGEN_VERSION &&
			 loadData.getInteger("BigReactorsUserWorldGen") == BigReactors.CONFIG.userWorldGenVersion) ||
			!BigReactors.WHITELIST_WORLDGEN_ORES.shouldGenerateIn(dimensionId))
			return;

		BigReactors.TICK_HANDLER.addRegenChunk(dimensionId, loadEvent.getChunk().getChunkCoordIntPair());
	}
}
