package fr.iambibi.upsidedown.generation;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.UpsideDownInfo;
import fr.iambibi.upsidedown.utils.BlockDataUtils;
import fr.iambibi.upsidedown.utils.CoordinatesUtils;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;

public class InvertedGenerator {

    private final UpsideDown plugin;
    private final World sourceWorld;
    private final World targetWorld;
    private final int radius;
    private final int originX;
    private final int originZ;

    public InvertedGenerator(UpsideDownInfo info) {
        this.plugin = UpsideDown.getInstance();
        this.sourceWorld = info.sourceWorld();
        this.targetWorld = info.invertedWorld();
        this.radius = info.radius();
        this.originX = info.originX();
        this.originZ = info.originZ();
    }

    public void generate() {
        int chunkRadius = radius / 16 + 1;
        int totalChunks = (chunkRadius * 2 + 1) * (chunkRadius * 2 + 1);
        int[] processedChunks = {0};

        plugin.getLogger().info("Start UpsideDown generation (" + totalChunks + " chunks to process)");

        new BukkitRunnable() {

            int chunkX = -chunkRadius;
            int chunkZ = -chunkRadius;

            @Override
            public void run() {
                if (chunkX > chunkRadius) {
                    cancel();
                    plugin.getLogger().info("UpsideDown world generation finished (" + processedChunks[0] + " chunks)");
                    return;
                }

                if (chunkZ > chunkRadius) {
                    chunkZ = -chunkRadius;
                    chunkX++;
                }

                ChunkSnapshot snapshot = sourceWorld
                        .getChunkAt(chunkX, chunkZ)
                        .getChunkSnapshot();

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < sourceWorld.getMaxHeight(); y++) {

                            BlockData data = snapshot.getBlockData(x, y, z);
                            if (data.getMaterial().isAir()) continue;

                            int globalX = chunkX * 16 + x;
                            int globalZ = chunkZ * 16 + z;

                            if (!isInsideRadius(globalX, globalZ)) continue;

                            int[] mirrored = CoordinatesUtils.convertCoordinates(
                                    globalX, y, globalZ, originX
                            );

                            int mx = mirrored[0];
                            int mz = mirrored[2];

                            int targetChunkX = mx >> 4;
                            int targetChunkZ = mz >> 4;
                            int bx = mx & 0xF;
                            int bz = mz & 0xF;
                            int finalY = y;

                            BlockData mirroredData = BlockDataUtils.mirrorBlockData(data.clone());

                            Bukkit.getScheduler().runTask(plugin, () ->
                                    targetWorld.getChunkAt(targetChunkX, targetChunkZ)
                                            .getBlock(bx, finalY, bz)
                                            .setBlockData(mirroredData, false)
                            );
                        }
                    }
                }

                processedChunks[0]++;

                int percent = (int) ((processedChunks[0] / (double) totalChunks) * 100);
                if (processedChunks[0] % Math.max(totalChunks / 10, 1) == 0) {
                    plugin.getLogger().info("UpsideDown generation progress: " + percent + "% (" + processedChunks[0] + "/" + totalChunks + " chunks)");
                }

                chunkZ++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private boolean isInsideRadius(int x, int z) {
        double dx = x - originX;
        double dz = z - originZ;
        return dx * dx + dz * dz <= radius * radius;
    }
}
