package fr.iambibi.upsidedown.generation;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class UpsideDownBiomeProvider extends BiomeProvider {
    private double originX;
    private double originZ;

    public UpsideDownBiomeProvider(double originX, double originZ) {
        this.originX = originX;
        this.originZ = originZ;
    }

    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        int chunkOriginX = (int) Math.floor(originX) >> 4;
        int chunkOriginZ = (int) Math.floor(originZ) >> 4;

        if (chunkX == chunkOriginX && chunkZ == chunkOriginZ) {
            return UpsideDownBiome.ORIGIN.getBiome();
        }

        return UpsideDownBiome.INVERTED.getBiome();
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return Arrays.stream(UpsideDownBiome.values())
                .map(UpsideDownBiome::getBiome)
                .toList();
    }
}
