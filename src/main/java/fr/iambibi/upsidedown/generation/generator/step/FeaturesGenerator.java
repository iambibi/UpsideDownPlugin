package fr.iambibi.upsidedown.generation.generator.step;

import fr.iambibi.upsidedown.generation.generator.GenerationContext;
import fr.iambibi.upsidedown.utils.CoordinatesUtils;
import net.minecraft.core.BlockPos;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static fr.iambibi.upsidedown.generation.UpsideDownBiomeProvider.RED_INVERTED_SOURCE;

public class FeaturesGenerator implements GenerationStep {


    @Override
    public void start(GenerationContext ctx, Runnable onComplete) {
        int chunkRadius = ctx.radius / 16 + 1;
        int totalChunks = (chunkRadius * 2 + 1) * (chunkRadius * 2 + 1);
        int[] processedChunks = {0};

        ctx.plugin.getLogger().info("Start features generation");

        // liste synchronisées pour l'accès multi-thread
        List<BlockPos> sculkFeaturesCollected = Collections.synchronizedList(new ArrayList<>());
        List<Location> floatingIslandCollected = Collections.synchronizedList(new ArrayList<>());

        new BukkitRunnable() {
            int chunkX = -chunkRadius;
            int chunkZ = -chunkRadius;

            @Override
            public void run() {
                for (int batchIndex = 0; batchIndex < 300; batchIndex++) {
                    if (chunkX > chunkRadius) {
                        cancel();
                        ctx.plugin.getLogger().info("Features collection finished. Found " + sculkFeaturesCollected.size() + " sculk positions and " + floatingIslandCollected.size() + " floating island positions");

                        new SculkFeaturesGenerator(sculkFeaturesCollected).start(
                                ctx,
                                () ->
                                    new FloatingIslandGenerator(floatingIslandCollected).start(ctx, onComplete)
                        );
                        return;
                    }

                    if (chunkZ > chunkRadius) {
                        chunkZ = -chunkRadius;
                        chunkX++;
                        continue;
                    }

                    int finalChunkX = chunkX;
                    int finalChunkZ = chunkZ;

                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            int globalX = finalChunkX * 16 + x;
                            int globalZ = finalChunkZ * 16 + z;

                            if (!ctx.isInsideRadius(globalX, globalZ)) continue;

                            int surfaceY = ctx.sourceWorld.getHighestBlockYAt(globalX, globalZ);
                            if (surfaceY <= ctx.sourceWorld.getMinHeight()) continue;

                            Biome biome = ctx.sourceWorld.getBiome(globalX, surfaceY, globalZ);

                            // Sculk Features
                            if (ThreadLocalRandom.current().nextDouble() <= 0.037 && !RED_INVERTED_SOURCE.contains(biome)) {
                                int[] mirrored = CoordinatesUtils.convertCoordinates(
                                        globalX, surfaceY, globalZ, ctx.originX
                                );

                                sculkFeaturesCollected.add(new BlockPos(
                                        mirrored[0],
                                        mirrored[1],
                                        mirrored[2]
                                ));
                            }

                            // Floating Island Features
                            if (RED_INVERTED_SOURCE.contains(biome)) {
                                for (int y = surfaceY + 40; y < surfaceY + 140; y++) {
                                    if (ThreadLocalRandom.current().nextDouble() <= 0.017) {
                                        floatingIslandCollected.add(CoordinatesUtils.convertLocation(
                                                new Location(
                                                        ctx.sourceWorld,
                                                        globalX,
                                                        y,
                                                        globalZ
                                                ),
                                                ctx.originX
                                        ));
                                    }
                                }
                            }
                        }
                    }

                    processedChunks[0]++;

                    int percent = (int) ((processedChunks[0] / (double) totalChunks) * 100);
                    if (processedChunks[0] % Math.max(totalChunks / 2, 1) == 0) {
                        ctx.plugin.getLogger().info("Features collection progress: " + percent + "% (" + processedChunks[0] + "/" + totalChunks + " chunks)");
                    }

                    chunkZ++;
                }
            }
        }.runTaskTimer(ctx.plugin, 0L, 1L);
    }
}