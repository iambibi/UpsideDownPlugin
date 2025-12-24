package fr.iambibi.upsidedown.generation.generator.step;

import fr.iambibi.upsidedown.generation.generator.GenerationContext;
import fr.iambibi.upsidedown.generation.mirror.MirrorArmorStand;
import fr.iambibi.upsidedown.generation.mirror.MirrorItemFrame;
import fr.iambibi.upsidedown.generation.mirror.MirrorPainting;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.scheduler.BukkitRunnable;

public class EntitesGenerator implements GenerationStep {
    @Override
    public void start(GenerationContext ctx, Runnable onComplete) {
        int chunkRadius = ctx.radius / 16 + 1;
        int totalChunks = (chunkRadius * 2 + 1) * (chunkRadius * 2 + 1);
        int[] processedChunks = {0};

        ctx.plugin.getLogger().info("Start invert entities (" + totalChunks + " chunks)");

        new BukkitRunnable() {
            int chunkX = -chunkRadius;
            int chunkZ = -chunkRadius;

            @Override
            public void run() {
                for (int batchIndex = 0; batchIndex < 50; batchIndex++) {
                    if (chunkX > chunkRadius) {
                        cancel();
                        ctx.plugin.getLogger().info("UpsideDown invert entities finished (" + processedChunks[0] + " chunks processed)");
                        onComplete.run();
                        return;
                    }

                    if (chunkZ > chunkRadius) {
                        chunkZ = -chunkRadius;
                        chunkX++;
                        continue;
                    }

                    if (!ctx.sourceWorld.isChunkLoaded(chunkX, chunkZ)) {
                        ctx.sourceWorld.loadChunk(chunkX, chunkZ, false);
                    }

                    Chunk chunk = ctx.sourceWorld.getChunkAt(chunkX, chunkZ);

                    for (Entity entity : chunk.getEntities()) {
                        Location loc = entity.getLocation();
                        if (!ctx.isInsideRadius(loc.getBlockX(), loc.getBlockZ())) continue;

                        // Mirror supported entities
                        if (entity instanceof ItemFrame frame) {
                            MirrorItemFrame.mirrorAndSpawn(frame, ctx.targetWorld, ctx.originX);
                        } else if (entity instanceof Painting painting) {
                            MirrorPainting.mirrorAndSpawn(painting, ctx.targetWorld, ctx.originX);
                        } else if (entity instanceof ArmorStand stand) {
                            MirrorArmorStand.mirrorAndSpawn(stand, ctx.targetWorld, ctx.originX);
                        }
                    }

                    processedChunks[0]++;

                    int percent = (int) ((processedChunks[0] / (double) totalChunks) * 100);
                    if (processedChunks[0] % Math.max(totalChunks / 20, 1) == 0) {
                        ctx.plugin.getLogger().info("Entities progress: " + percent + "% (" + processedChunks[0] + "/" + totalChunks + " chunks)");
                    }

                    chunkZ++;
                }
            }
        }.runTaskTimer(ctx.plugin, 0L, 2L);
    }
}
