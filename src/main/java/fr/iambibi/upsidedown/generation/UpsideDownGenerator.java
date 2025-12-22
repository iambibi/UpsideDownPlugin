package fr.iambibi.upsidedown.generation;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.UpsideDownInfo;
import fr.iambibi.upsidedown.datapack.UpsideDownDatapack;
import fr.iambibi.upsidedown.generation.mirror.*;
import fr.iambibi.upsidedown.generation.palette.Palette;
import fr.iambibi.upsidedown.utils.CoordinatesUtils;
import fr.iambibi.upsidedown.utils.FeaturesUtils;
import net.minecraft.core.BlockPos;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class UpsideDownGenerator {

    private final UpsideDown plugin;
    private final World sourceWorld;
    private final World targetWorld;
    private final int radius;
    private final int originX;
    private final int originZ;
    private final Palette.BlockPalette palette;

    public UpsideDownGenerator(UpsideDownInfo info) {
        this.plugin = UpsideDown.getInstance();
        this.sourceWorld = info.sourceWorld();
        this.targetWorld = info.invertedWorld();
        this.radius = info.radius();
        this.originX = info.originX();
        this.originZ = info.originZ();
        this.palette = info.palette();
    }

    /**
     * Generates the inverted world by mirroring blocks from the source world.
     */
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

                    Bukkit.getScheduler().runTaskLater(plugin, () -> generateFeatures(), 40L);
                    return;
                }

                if (chunkZ > chunkRadius) {
                    chunkZ = -chunkRadius;
                    chunkX++;
                }

                ChunkSnapshot snapshot = sourceWorld.getChunkAt(chunkX, chunkZ).getChunkSnapshot();

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

                            BlockData mirroredData = MirrorBlockData.mirrorBlockData(data.clone());

                            Bukkit.getScheduler().runTask(plugin, () -> {
                                Block targetBlock = targetWorld
                                        .getChunkAt(targetChunkX, targetChunkZ)
                                        .getBlock(bx, finalY, bz);
                                targetBlock.setBlockData(mirroredData, false);

                                BlockState sourceState = sourceWorld.getBlockAt(globalX, finalY, globalZ).getState();
                                MirrorBlockStates.mirrorBlockState(sourceState, targetBlock.getState());

                                palette.apply(targetBlock);
                            });
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

    public void generateFeatures() {
        int chunkRadius = radius / 16 + 1;
        int min = -chunkRadius;
        int max = chunkRadius;

        int[] processedFeatures = {0};

        plugin.getLogger().info("Start features generation");

        sourceWorld.getChunksAtAsync(
                min, min,
                max, max,
                false,
                () -> {
                    List<BlockPos> collected = new ArrayList<>();

                    for (int cx = min; cx <= max; cx++) {
                        for (int cz = min; cz <= max; cz++) {

                            for (int x = 0; x < 16; x++) {
                                for (int z = 0; z < 16; z++) {

                                    int globalX = cx * 16 + x;
                                    int globalZ = cz * 16 + z;

                                    if (!isInsideRadius(globalX, globalZ)) continue;
                                    if (ThreadLocalRandom.current().nextDouble() > 0.077) continue;

                                    int surfaceY = sourceWorld.getHighestBlockYAt(globalX, globalZ);
                                    if (surfaceY <= sourceWorld.getMinHeight()) continue;

                                    int[] mirrored = CoordinatesUtils.convertCoordinates(
                                            globalX, surfaceY, globalZ, originX
                                    );

                                    collected.add(new BlockPos(
                                            mirrored[0],
                                            mirrored[1],
                                            mirrored[2]
                                    ));
                                }
                            }
                        }
                    }

                    plugin.getLogger().info("Collected " + collected.size() + " sculk path positions");

                    Iterator<BlockPos> iterator = new ArrayList<>(collected).iterator();

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            int placed = 0;

                            while (iterator.hasNext() && placed < 250) {
                                BlockPos pos = iterator.next();

                                try {
                                    FeaturesUtils.placeFeature(
                                            targetWorld,
                                            UpsideDownDatapack.DATAPACK_NAMESPACE,
                                            "sculk_patch_buffed",
                                            pos
                                    );
                                } catch (Throwable ignored) {
                                }

                                processedFeatures[0]++;
                                placed++;

                                int percent = (int) ((processedFeatures[0] / (double) collected.size()) * 100);
                                if (processedFeatures[0] % Math.max(collected.size() / 10, 1) == 0) {
                                    plugin.getLogger().info("UpsideDown feature generation progress: " + percent + "% (" + processedFeatures[0] + "/" + collected.size() + " features)");
                                }
                            }

                            if (!iterator.hasNext()) {
                                cancel();
                                plugin.getLogger().info("Features generation finished");
                                Bukkit.getScheduler().runTaskLater(plugin, () -> invertEntities(), 40L);
                            }
                        }

                    }.runTaskTimer(plugin, 1L, 1L);
                }
        );
    }

    public void invertEntities() {
        int chunkRadius = radius / 16 + 1;
        int min = -chunkRadius;
        int max = chunkRadius;

        int totalChunks = (max - min + 1) * (max - min + 1);
        int[] processedChunks = {0};

        plugin.getLogger().info("Start invert entities (" + totalChunks + " chunks)");

        // Asynchronously load chunks to fix spawning entities in unloaded chunks
        sourceWorld.getChunksAtAsync(
                min, min,
                max, max,
                false,
                () -> {
                    for (int cx = min; cx <= max; cx++) {
                        for (int cz = min; cz <= max; cz++) {
                            Chunk chunk = sourceWorld.getChunkAt(cx, cz);

                            for (Entity entity : chunk.getEntities()) {
                                Location loc = entity.getLocation();
                                if (!isInsideRadius(loc.getBlockX(), loc.getBlockZ())) continue;

                                // Mirror supported entities
                                if (entity instanceof ItemFrame frame) {
                                    MirrorItemFrame.mirrorAndSpawn(frame, targetWorld, originX);
                                } else if (entity instanceof Painting painting) {
                                    MirrorPainting.mirrorAndSpawn(painting, targetWorld, originX);
                                } else if (entity instanceof ArmorStand stand) {
                                    MirrorArmorStand.mirrorAndSpawn(stand, targetWorld, originX);
                                }
                            }

                            processedChunks[0]++;
                            int percent = (processedChunks[0] * 100) / totalChunks;

                            if (percent % 10 == 0 &&
                                    processedChunks[0] % Math.max(totalChunks / 10, 1) == 0) {
                                plugin.getLogger().info(
                                        "Entities progress: " + percent + "% (" +
                                                processedChunks[0] + "/" + totalChunks + ")"
                                );
                            }
                        }
                    }

                    plugin.getLogger().info("UpsideDown invert entities finished");
                }
        );
    }
    /**
     * Checks if the given coordinates are inside the defined radius from the origin.
     * @param x X coordinate to check.
     * @param z Z coordinate to check.
     * @return True if inside radius, false otherwise.
     */
    private boolean isInsideRadius(int x, int z) {
        double dx = x - originX;
        double dz = z - originZ;
        return dx * dx + dz * dz <= radius * radius;
    }
}
