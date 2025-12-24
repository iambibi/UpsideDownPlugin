package fr.iambibi.upsidedown.generation;

import fr.iambibi.upsidedown.utils.CoordinatesUtils;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpsideDownBiomeProvider extends BiomeProvider {

    private final World sourceWorld;
    private final double originX;
    private final double originZ;

    public static Set<Biome> RED_INVERTED_SOURCE = Set.of(
            Biome.DESERT,
            Biome.BADLANDS,
            Biome.ERODED_BADLANDS,
            Biome.SAVANNA,
            Biome.SAVANNA_PLATEAU
    );

    public UpsideDownBiomeProvider(World sourceWorld, double originX, double originZ) {
        this.sourceWorld = sourceWorld;
        this.originX = originX;
        this.originZ = originZ;
    }

    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        int chunkOriginX = (int) Math.floor(originX) >> 4;
        int chunkOriginZ = (int) Math.floor(originZ) >> 4;

        int[] mirrorCoordinates = CoordinatesUtils.convertCoordinates(x, y, z, (int) originX);
        Biome sourceBiome = sourceWorld.getBiome(mirrorCoordinates[0], mirrorCoordinates[1], mirrorCoordinates[2]);

        if (chunkX == chunkOriginX && chunkZ == chunkOriginZ) {
            return UpsideDownBiome.ORIGIN.getBiome();
        } else {
            return getAssociatedBiome(sourceBiome).getBiome();
        }
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return Arrays.stream(UpsideDownBiome.values())
                .map(UpsideDownBiome::getBiome)
                .toList();
    }

    public static UpsideDownBiome getAssociatedBiome(Biome biome) {
        if (RED_INVERTED_SOURCE.contains(biome)) {
            return UpsideDownBiome.RED_INVERTED;
        } else {
            return UpsideDownBiome.INVERTED;
        }
    }
}
