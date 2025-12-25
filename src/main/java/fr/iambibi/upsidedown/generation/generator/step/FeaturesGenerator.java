package fr.iambibi.upsidedown.generation.generator.step;

import fr.iambibi.upsidedown.fixes.postprocesswarn.SculkPatchConfiguration;
import fr.iambibi.upsidedown.fixes.postprocesswarn.SculkPatchFeature;
import fr.iambibi.upsidedown.generation.generator.GenerationContext;
import fr.iambibi.upsidedown.utils.CoordinatesUtils;
import fr.iambibi.upsidedown.utils.StructureUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static fr.iambibi.upsidedown.generation.UpsideDownBiomeProvider.RED_INVERTED_SOURCE;

public class FeaturesGenerator implements GenerationStep {

    private final int FEATURES_PER_TICK = 4;
    private final List<String> FLOATING_ISLAND_FEATURES = List.of(
            "island_1",
            "island_2",
            "island_3",
            "island_4",
            "island_5",
            "island_6"
    );

    public FeaturesGenerator() {
        StructureUtils.preloadStructures(FLOATING_ISLAND_FEATURES);
    }

    @Override
    public void start(GenerationContext ctx, Runnable onComplete) {
        int chunkRadius = ctx.radius / 16 + 1;
        int totalChunks = (chunkRadius * 2 + 1) * (chunkRadius * 2 + 1);
        int[] processedChunks = {0};

        ctx.plugin.getLogger().info("Start features generation");

        new BukkitRunnable() {

            int chunkX = -chunkRadius;
            int chunkZ = -chunkRadius;

            @Override
            public void run() {
                for (int batchIndex = 0; batchIndex <FEATURES_PER_TICK; batchIndex++) {
                    if (chunkX > chunkRadius) {
                        cancel();
                        ctx.plugin.getLogger().info("Feature generation finished");
                        onComplete.run();
                        return;
                    }

                    if (chunkZ > chunkRadius) {
                        chunkZ = -chunkRadius;
                        chunkX++;
                        continue;
                    }

                    processChunk(ctx, chunkX, chunkZ);
                    chunkZ++;
                    processedChunks[0]++;
                    int percent = (int) ((processedChunks[0] / (double) totalChunks) * 100);
                    if (processedChunks[0] % Math.max(totalChunks / 10, 1) == 0) {
                        ctx.plugin.getLogger().info("Features placement progress: " + percent + "% (" + processedChunks[0] + "/" + totalChunks + ")");
                    }
                }
            }

            private void processChunk(GenerationContext ctx, int chunkX, int chunkZ) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {

                        int globalX = chunkX * 16 + x;
                        int globalZ = chunkZ * 16 + z;

                        if (!ctx.isInsideRadius(globalX, globalZ)) continue;

                        int surfaceY = ctx.sourceWorld.getHighestBlockYAt(globalX, globalZ);
                        if (surfaceY <= ctx.sourceWorld.getMinHeight()) continue;

                        Biome biome = ctx.sourceWorld.getBiome(globalX, surfaceY, globalZ);

                        int[] mirrored = CoordinatesUtils.convertCoordinates(
                                globalX, surfaceY, globalZ, ctx.originX
                        );

                        if (ThreadLocalRandom.current().nextDouble() <= 0.037
                                && !RED_INVERTED_SOURCE.contains(biome)) {

                            ServerLevel level = ((CraftWorld) ctx.targetWorld).getHandle();
                            BlockPos pos = new BlockPos(
                                    mirrored[0],
                                    mirrored[1],
                                    mirrored[2]
                            );
                            SculkPatchFeature.place(level, pos, level.random, new SculkPatchConfiguration());

                        }

                        if (RED_INVERTED_SOURCE.contains(biome)) {
                            for (int y = Math.min(300, surfaceY + 40); y < Math.min(300, surfaceY + 140); y++) {
                                if (ThreadLocalRandom.current().nextDouble() <= 0.37) {
                                    Location pos = new Location(
                                            ctx.targetWorld,
                                            mirrored[0],
                                            y,
                                            mirrored[2]
                                    );
                                    StructureUtils.placeStructure(
                                            StructureUtils.getCachedStructure(FLOATING_ISLAND_FEATURES.get(ThreadLocalRandom.current().nextInt(FLOATING_ISLAND_FEATURES.size()))),
                                            pos,
                                            true,
                                            true,
                                            true
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(ctx.plugin, 0L, 1L);
    }
}