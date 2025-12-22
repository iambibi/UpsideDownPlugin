package fr.iambibi.upsidedown.generation;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.UpsideDownInfo;
import fr.iambibi.upsidedown.datapack.UpsideDownDatapack;
import fr.iambibi.upsidedown.generation.mirror.*;
import fr.iambibi.upsidedown.generation.palette.Palette;
import fr.iambibi.upsidedown.utils.CoordinatesUtils;
import fr.iambibi.upsidedown.utils.FeaturesUtils;
import fr.iambibi.upsidedown.utils.StructureUtils;
import net.minecraft.core.BlockPos;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static fr.iambibi.upsidedown.generation.UpsideDownBiomeProvider.RED_INVERTED_SOURCE;

public class UpsideDownGenerator {

    private final UpsideDown plugin;
    private final World sourceWorld;
    private final World targetWorld;
    private final int radius;
    private final int originX;
    private final int originZ;
    private final Palette.BlockPalette palette;

    private final List<String> FLOATING_ISLAND_FEATURES = List.of(
                "island_1",
                "island_2",
                "island_3",
                "island_4",
                "island_5",
                "island_6"
    );
    public UpsideDownGenerator(UpsideDownInfo info) {
        this.plugin = UpsideDown.getInstance();
        this.sourceWorld = info.sourceWorld();
        this.targetWorld = info.invertedWorld();
        this.radius = info.radius();
        this.originX = info.originX();
        this.originZ = info.originZ();
        this.palette = info.palette();

        StructureUtils.preloadStructures(FLOATING_ISLAND_FEATURES);
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

        int[] processedSculkFeatures = {0};
        int[] processedFloatingIslandFeatures = {0};

        plugin.getLogger().info("Start features generation");

        sourceWorld.getChunksAtAsync(
                min, min,
                max, max,
                false,
                () -> {
                    List<BlockPos> sculkFeaturesCollected = new ArrayList<>();
                    List<Location> floatingIslandCollected = new ArrayList<>();

                    for (int cx = min; cx <= max; cx++) {
                        for (int cz = min; cz <= max; cz++) {

                            for (int x = 0; x < 16; x++) {
                                for (int z = 0; z < 16; z++) {

                                    int globalX = cx * 16 + x;
                                    int globalZ = cz * 16 + z;

                                    if (!isInsideRadius(globalX, globalZ)) continue;


                                    int surfaceY = sourceWorld.getHighestBlockYAt(globalX, globalZ);
                                    if (surfaceY <= sourceWorld.getMinHeight()) continue;

                                    Biome biome = sourceWorld.getBiome(globalX, surfaceY, globalZ);

                                    // Sculk Features
                                    if (ThreadLocalRandom.current().nextDouble() <= 0.077 && !RED_INVERTED_SOURCE.contains(biome)) {

                                        int[] mirrored = CoordinatesUtils.convertCoordinates(
                                                globalX, surfaceY, globalZ, originX
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
                                                                sourceWorld,
                                                                globalX,
                                                                y,
                                                                globalZ
                                                        ),
                                                        originX
                                                ));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    plugin.getLogger().info("Collected " + sculkFeaturesCollected.size() + " sculk path positions");
                    plugin.getLogger().info("Collected " + floatingIslandCollected.size() + " sculk path positions");

                    // Sculk Features placement
                    Iterator<BlockPos> iterator = new ArrayList<>(sculkFeaturesCollected).iterator();
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

                                processedSculkFeatures[0]++;
                                placed++;

                                int percent = (int) ((processedSculkFeatures[0] / (double) sculkFeaturesCollected.size()) * 100);
                                if (processedSculkFeatures[0] % Math.max(sculkFeaturesCollected.size() / 10, 1) == 0) {
                                    plugin.getLogger().info("UpsideDown feature generation progress: " + percent + "% (" + processedSculkFeatures[0] + "/" + sculkFeaturesCollected.size() + " sculk features)");
                                }
                            }

                            if (!iterator.hasNext()) {
                                cancel();
                                plugin.getLogger().info("Features generation finished");
                                Bukkit.getScheduler().runTaskLater(plugin, () -> invertEntities(), 40L);
                            }
                        }

                    }.runTaskTimer(plugin, 1L, 1L);

                    // Floating Island placement
                    Iterator<Location> iterator1 = new ArrayList<>(floatingIslandCollected).iterator();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            int placed = 0;

                            while (iterator1.hasNext() && placed < 250) {
                                Location pos = iterator1.next();

                                try {
                                    StructureUtils.placeStructure(
                                            StructureUtils.getCachedStructure(FLOATING_ISLAND_FEATURES.get(ThreadLocalRandom.current().nextInt(FLOATING_ISLAND_FEATURES.size()))),
                                            pos,
                                            true,
                                            true,
                                            true
                                    );
                                } catch (Throwable ignored) {
                                }

                                processedFloatingIslandFeatures[0]++;
                                placed++;

                                int percent = (int) ((processedFloatingIslandFeatures[0] / (double) floatingIslandCollected.size()) * 100);
                                if (processedFloatingIslandFeatures[0] % Math.max(floatingIslandCollected.size() / 10, 1) == 0) {
                                    plugin.getLogger().info("UpsideDown feature generation progress: " + percent + "% (" + processedFloatingIslandFeatures[0] + "/" + floatingIslandCollected.size() + " islands features)");
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
