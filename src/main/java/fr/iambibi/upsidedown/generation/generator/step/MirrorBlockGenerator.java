package fr.iambibi.upsidedown.generation.generator.step;

import fr.iambibi.upsidedown.generation.UpsideDownBiome;
import fr.iambibi.upsidedown.generation.UpsideDownBiomeProvider;
import fr.iambibi.upsidedown.generation.generator.GenerationContext;
import fr.iambibi.upsidedown.generation.mirror.MirrorBlockData;
import fr.iambibi.upsidedown.generation.mirror.MirrorBlockStates;
import fr.iambibi.upsidedown.utils.CoordinatesUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MirrorBlockGenerator implements GenerationStep {

    private final int CHUNKS_BY_TICK = 20;
    @Override
    public void start(GenerationContext ctx, Runnable onComplete) {
        ctx.plugin.getLogger().info("Start UpsideDown block generation");

        int chunkRadius = ctx.radius / 16 + 1;
        int totalChunks = (chunkRadius * 2 + 1) * (chunkRadius * 2 + 1);
        int[] processedChunks = {0};

        ctx.plugin.getLogger().info("Start UpsideDown generation (" + totalChunks + " chunks to process)");

        Map<UpsideDownBiome, Map<Material, List<Material>>> paletteCache = Arrays.stream(UpsideDownBiome.values())
                .collect(HashMap::new,
                        (map, biome) -> map.put(biome, biome.getPalette().buildReplacementCache()),
                        HashMap::putAll);

        new BukkitRunnable() {
            int chunkX = -chunkRadius;
            int chunkZ = -chunkRadius;

            @Override
            public void run() {
                for (int batchIndex = 0; batchIndex < CHUNKS_BY_TICK; batchIndex++) {
                    if (chunkX > chunkRadius) {
                        cancel();
                        ctx.plugin.getLogger().info("UpsideDown world generation finished (" + processedChunks[0] + " chunks)");
                        Bukkit.getScheduler().runTaskLater(ctx.plugin, onComplete, 40L);
                        return;
                    }

                    if (chunkZ > chunkRadius) {
                        chunkZ = -chunkRadius;
                        chunkX++;
                        continue;
                    }

                    processAndPlaceChunk(ctx, chunkX, chunkZ, paletteCache);

                    processedChunks[0]++;
                    int percent = (int) ((processedChunks[0] / (double) totalChunks) * 100);
                    if (processedChunks[0] % Math.max(totalChunks / 20, 1) == 0) {
                        ctx.plugin.getLogger().info("UpsideDown generation progress: " + percent + "% (" + processedChunks[0] + "/" + totalChunks + " chunks)");
                    }

                    chunkZ++;
                }
            }

            private void processAndPlaceChunk(GenerationContext ctx, int chunkX, int chunkZ,
                                              Map<UpsideDownBiome, Map<Material, List<Material>>> paletteCache) {
                ChunkSnapshot snapshot = ctx.sourceWorld.getChunkAt(chunkX, chunkZ).getChunkSnapshot(true, true, false);
                ctx.sourceWorld.unloadChunkRequest(chunkX, chunkZ);

                Map<Long, List<BlockPlacement>> localBlocks = new HashMap<>();

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < ctx.sourceWorld.getMaxHeight(); y++) {
                            BlockData data = snapshot.getBlockData(x, y, z);
                            if (data.getMaterial().isAir()) continue;

                            int globalX = chunkX * 16 + x;
                            int globalZ = chunkZ * 16 + z;
                            if (!ctx.isInsideRadius(globalX, globalZ)) continue;

                            int[] mirrored = CoordinatesUtils.convertCoordinates(globalX, y, globalZ, ctx.originX);
                            int mx = mirrored[0];
                            int mz = mirrored[2];

                            BlockData mirroredData = MirrorBlockData.mirrorBlockData(data.clone());
                            UpsideDownBiome upsideDownBiome = UpsideDownBiomeProvider.getAssociatedBiome(snapshot.getBiome(x, y, z));
                            mirroredData = upsideDownBiome.getPalette().applyToBlockData(mirroredData, paletteCache.get(upsideDownBiome));

                            int targetChunkX = mx >> 4;
                            int targetChunkZ = mz >> 4;
                            long chunkKey = ((long) targetChunkX << 32) | (targetChunkZ & 0xFFFFFFFFL);

                            BlockState sourceState = null;
                            if (MirrorBlockStates.BLOCK_STATES_SUPPORTED.contains(mirroredData.getMaterial())) {
                                sourceState = ctx.sourceWorld.getBlockAt(globalX, y, globalZ).getState();
                            }

                            localBlocks.computeIfAbsent(chunkKey, k -> new ArrayList<>())
                                    .add(new BlockPlacement(mx & 0xF, y, mz & 0xF, mirroredData, sourceState));
                        }
                    }
                }

                for (Map.Entry<Long, List<BlockPlacement>> entry : localBlocks.entrySet()) {
                    long chunkKey = entry.getKey();
                    int targetChunkX = (int) (chunkKey >> 32);
                    int targetChunkZ = (int) chunkKey;

                    Chunk targetChunk = ctx.targetWorld.getChunkAt(targetChunkX, targetChunkZ);

                    for (BlockPlacement p : entry.getValue()) {
                        Block targetBlock = targetChunk.getBlock(p.x, p.y, p.z);
                        targetBlock.setBlockData(p.data, false);

                        if (p.state != null) {
                            MirrorBlockStates.mirrorBlockState(p.state, targetBlock.getState());
                        }
                    }
                }
            }
        }.runTaskTimer(ctx.plugin, 0L, 1L);
    }

    private static class BlockPlacement {
        int x, y, z;
        BlockData data;
        BlockState state;

        BlockPlacement(int x, int y, int z, BlockData data, BlockState state) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.data = data;
            this.state = state;
        }
    }
}
