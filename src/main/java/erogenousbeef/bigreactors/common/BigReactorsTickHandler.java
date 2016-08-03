package erogenousbeef.bigreactors.common;

import it.zerono.mods.zerocore.lib.world.WorldGenMinableOres;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class BigReactorsTickHandler {

    public BigReactorsTickHandler(WorldGenMinableOres oresWorldGen) {
        this._oresWorldGen = oresWorldGen;
    }

	public void addRegenChunk(int dimensionId, ChunkPos chunkCoord) {
		if(chunkRegenMap == null) {
			chunkRegenMap = new HashMap<Integer, Queue<ChunkPos>>();
		}
		
		if(!chunkRegenMap.containsKey(dimensionId)) {
			LinkedList<ChunkPos> list = new LinkedList<ChunkPos>();
			list.add(chunkCoord);
			chunkRegenMap.put(dimensionId, list);
		}
		else {
			if(!chunkRegenMap.get(dimensionId).contains(chunkCoord)) {
				chunkRegenMap.get(dimensionId).add(chunkCoord);
			}
		}
	}

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {

        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END &&
                null != this.chunkRegenMap && !event.world.isRemote) {

            int dimensionId = event.world.provider.getDimension();

            if(chunkRegenMap.containsKey(dimensionId)) {
                // Split up regen so it takes at most 16 millisec per frame to allow for ~55-60 FPS
                Queue<ChunkPos> chunksToGen = chunkRegenMap.get(dimensionId);
                long startTime = System.nanoTime();
                while(System.nanoTime() - startTime < maximumDeltaTimeNanoSecs && !chunksToGen.isEmpty()) {
                    // Regenerate chunk
                    ChunkPos nextChunk = chunksToGen.poll();
                    if(nextChunk == null) { break; }

                    Random fmlRandom = new Random(event.world.getSeed());
                    long xSeed = fmlRandom.nextLong() >> 2 + 1L;
                    long zSeed = fmlRandom.nextLong() >> 2 + 1L;
                    fmlRandom.setSeed((xSeed * nextChunk.chunkXPos + zSeed * nextChunk.chunkZPos) ^ event.world.getSeed());

                    this._oresWorldGen.generateChunk(fmlRandom, nextChunk.chunkXPos, nextChunk.chunkZPos, event.world);
                }

                if(chunksToGen.isEmpty()) {
                    chunkRegenMap.remove(dimensionId);
                }
            }
        }
    }

    protected HashMap<Integer, Queue<ChunkPos>> chunkRegenMap;
    protected static final long maximumDeltaTimeNanoSecs = 16000000; // 16 milliseconds
    private final WorldGenMinableOres _oresWorldGen;
}
