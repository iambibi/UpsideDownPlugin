package fr.iambibi.upsidedown.generation.generator.step;

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

public class MirrorBlockGenerator implements GenerationStep {

    @Override
    public void start(GenerationContext ctx, Runnable onComplete) {
        ctx.plugin.getLogger().info("Start UpsideDown block generation");

        int chunkRadius = ctx.radius / 16 + 1;
        int totalChunks = (chunkRadius * 2 + 1) * (chunkRadius * 2 + 1);
        int[] processedChunks = {0};

        ctx.plugin.getLogger().info("Start UpsideDown generation (" + totalChunks + " chunks to process)");

        Map<Material, List<Material>> paletteCache = ctx.palette.buildReplacementCache();

        Map<Long, List<MirrorBlockGenerator.BlockPlacement>> globalBlocksByTargetChunk = new HashMap<>();

        new BukkitRunnable() {
            int chunkX = -chunkRadius;
            int chunkZ = -chunkRadius;
            int placementTasksPending = 0;

            @Override
            public void run() {
                for (int batchIndex = 0; batchIndex < 30; batchIndex++) {
                    if (chunkX > chunkRadius) {
                        if (!globalBlocksByTargetChunk.isEmpty()) {
                            schedulePlacement(new HashMap<>(globalBlocksByTargetChunk));
                            globalBlocksByTargetChunk.clear();
                        }

                        if (placementTasksPending == 0) {
                            cancel();
                            ctx.plugin.getLogger().info("UpsideDown world generation finished (" + processedChunks[0] + " chunks)");
                            Bukkit.getScheduler().runTaskLater(ctx.plugin, onComplete, 40L);
                        }
                        return;
                    }

                    if (chunkZ > chunkRadius) {
                        chunkZ = -chunkRadius;
                        chunkX++;
                        continue;
                    }

                    ChunkSnapshot snapshot = ctx.sourceWorld.getChunkAt(chunkX, chunkZ).getChunkSnapshot();

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
                                mirroredData = ctx.palette.applyToBlockData(mirroredData, paletteCache);

                                int targetChunkX = mx >> 4;
                                int targetChunkZ = mz >> 4;
                                int bx = mx & 0xF;
                                int bz = mz & 0xF;

                                long chunkKey = ((long) targetChunkX << 32) | (targetChunkZ & 0xFFFFFFFFL);

                                globalBlocksByTargetChunk.computeIfAbsent(chunkKey, k -> new ArrayList<>())
                                        .add(new MirrorBlockGenerator.BlockPlacement(globalX, y, globalZ, bx, bz, mirroredData));
                            }
                        }
                    }

                    processedChunks[0]++;

                    int percent = (int) ((processedChunks[0] / (double) totalChunks) * 100);
                    if (processedChunks[0] % Math.max(totalChunks / 20, 1) == 0) {
                        ctx.plugin.getLogger().info("UpsideDown generation progress: " + percent + "% (" + processedChunks[0] + "/" + totalChunks + " chunks)");
                    }

                    chunkZ++;
                }

                if (globalBlocksByTargetChunk.size() > 50) {
                    schedulePlacement(new HashMap<>(globalBlocksByTargetChunk));
                    globalBlocksByTargetChunk.clear();
                }
            }

            private void schedulePlacement(Map<Long, List<MirrorBlockGenerator.BlockPlacement>> blocksToPlace) {
                placementTasksPending++;

                Bukkit.getScheduler().runTask(ctx.plugin, () -> {
                    Set<Long> chunksToLoad = blocksToPlace.keySet();
                    for (long chunkKey : chunksToLoad) {
                        int targetChunkX = (int) (chunkKey >> 32);
                        int targetChunkZ = (int) chunkKey;
                        ctx.targetWorld.getChunkAtAsync(targetChunkX, targetChunkZ);
                    }

                    Bukkit.getScheduler().runTaskLater(ctx.plugin, () -> {
                        for (Map.Entry<Long, List<MirrorBlockGenerator.BlockPlacement>> entry : blocksToPlace.entrySet()) {
                            long chunkKey = entry.getKey();
                            List<MirrorBlockGenerator.BlockPlacement> placements = entry.getValue();

                            int targetChunkX = (int) (chunkKey >> 32);
                            int targetChunkZ = (int) chunkKey;

                            Chunk targetChunk = ctx.targetWorld.getChunkAt(targetChunkX, targetChunkZ);

                            for (MirrorBlockGenerator.BlockPlacement p : placements) {
                                Block targetBlock = targetChunk.getBlock(p.bx, p.y, p.bz);
                                targetBlock.setBlockData(p.data, false);

                                BlockState sourceState = ctx.sourceWorld.getBlockAt(p.globalX, p.y, p.globalZ).getState();
                                MirrorBlockStates.mirrorBlockState(sourceState, targetBlock.getState());
                            }
                        }

                        placementTasksPending--;
                    }, 2L);
                });
            }
        }.runTaskTimer(ctx.plugin, 0L, 1L);
    }

    private static class BlockPlacement {
        int globalX, y, globalZ, bx, bz;
        BlockData data;

        BlockPlacement(int globalX, int y, int globalZ, int bx, int bz, BlockData data) {
            this.globalX = globalX;
            this.y = y;
            this.globalZ = globalZ;
            this.bx = bx;
            this.bz = bz;
            this.data = data;
        }
    }
}