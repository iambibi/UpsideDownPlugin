package fr.iambibi.upsidedown.generation;

import fr.iambibi.upsidedown.UpsideDownInfo;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class WallChunkGenerator extends ChunkGenerator {

    private final int originX, originZ, radius;

    public WallChunkGenerator(int originX, int originZ, int radius) {
        this.originX = originX;
        this.originZ = originZ;
        this.radius = radius;
    }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                int globalX = chunkX * 16 + x;
                int globalZ = chunkZ * 16 + z;

                double distance = Math.sqrt(Math.pow(globalX - originX, 2) + Math.pow(globalZ - originZ, 2));

                if (distance > radius) {
                    for (int y = 0; y < worldInfo.getMaxHeight(); y++) {
                        chunkData.setBlock(x, y, z, Material.BEDROCK);
                    }
                } else {
                    for (int y = 0; y < worldInfo.getMaxHeight(); y++) {
                        chunkData.setBlock(x, y, z, Material.AIR);
                    }
                }
            }
        }
    }
}
