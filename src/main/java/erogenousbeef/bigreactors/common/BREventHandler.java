package erogenousbeef.bigreactors.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import erogenousbeef.bigreactors.utils.StaticUtils;

public class BREventHandler {

	@SubscribeEvent
	public void chunkSave(ChunkDataEvent.Save saveEvent) {
		if(BigReactors.enableWorldGen) {
			NBTTagCompound saveData = saveEvent.getData();
			
			saveData.setInteger("BigReactorsWorldGen", BRConfig.WORLDGEN_VERSION);
			saveData.setInteger("BigReactorsUserWorldGen", BigReactors.userWorldGenVersion);
		}
	}
	
	@SubscribeEvent
	public void chunkLoad(ChunkDataEvent.Load loadEvent) {
		if(!BigReactors.enableWorldRegeneration || !BigReactors.enableWorldGen) {
			return;
		}

		NBTTagCompound loadData = loadEvent.getData();
		if(loadData.getInteger("BigReactorsWorldGen") == BRConfig.WORLDGEN_VERSION &&
				loadData.getInteger("BigReactorsUserWorldGen") == BigReactors.userWorldGenVersion) {
			return;
		}

		int dimensionId = loadEvent.getWorld().provider.getDimension();
		
		if(!StaticUtils.WorldGen.shouldGenerateInDimension(dimensionId)) {
			return;
		}
		
		ChunkCoordIntPair coordPair = loadEvent.getChunk().getChunkCoordIntPair();
		BigReactors.tickHandler.addRegenChunk(dimensionId, coordPair);
	}
	
}
