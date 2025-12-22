package fr.iambibi.upsidedown.generation;

import fr.iambibi.upsidedown.datapack.UpsideDownDatapack;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

@Getter
public enum UpsideDownBiome {
    ORIGIN(
            NamespacedKey.fromString(UpsideDownDatapack.DATAPACK_NAMESPACE + ":origin")
    ),
    INVERTED(
            NamespacedKey.fromString(UpsideDownDatapack.DATAPACK_NAMESPACE + ":inverted")
    ),
    ;
    private final Registry<@NotNull Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
    private final NamespacedKey biomeKey;
    private final Biome biome;

    UpsideDownBiome(NamespacedKey biomeKey) {
        this.biomeKey = biomeKey;
        this.biome = registry.get(biomeKey);
    }
}
