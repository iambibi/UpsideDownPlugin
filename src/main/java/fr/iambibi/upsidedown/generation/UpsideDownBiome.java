package fr.iambibi.upsidedown.generation;

import fr.iambibi.upsidedown.datapack.UpsideDownDatapack;
import fr.iambibi.upsidedown.generation.palette.BluePaletteBlock;
import fr.iambibi.upsidedown.generation.palette.Palette;
import fr.iambibi.upsidedown.generation.palette.PaletteRegistry;
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
            NamespacedKey.fromString(UpsideDownDatapack.DATAPACK_NAMESPACE + ":origin"), PaletteRegistry.get("blue")
    ),
    INVERTED(
            NamespacedKey.fromString(UpsideDownDatapack.DATAPACK_NAMESPACE + ":inverted"), PaletteRegistry.get("blue")
    ),
    RED_INVERTED(
            NamespacedKey.fromString(UpsideDownDatapack.DATAPACK_NAMESPACE + ":red_inverted"), PaletteRegistry.get("blue")
    ),
    ;

    private final Registry<@NotNull Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
    private final NamespacedKey biomeKey;
    private final Biome biome;
    private final Palette.BlockPalette palette;

    UpsideDownBiome(NamespacedKey biomeKey, Palette.BlockPalette palette) {
        this.biomeKey = biomeKey;
        this.biome = registry.get(biomeKey);
        this.palette = palette;
    }
}
