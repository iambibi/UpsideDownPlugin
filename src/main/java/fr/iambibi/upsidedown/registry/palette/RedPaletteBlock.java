package fr.iambibi.upsidedown.registry.palette;

import fr.iambibi.upsidedown.registry.Palette;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;
import java.util.Set;

public class RedPaletteBlock implements Palette.BlockPalette {

    private final List<Palette.BlockReplacement> replacements = List.of(
            new Palette.BlockReplacement(
                    Set.of(Material.DEEPSLATE),
                    Set.of(Material.NETHERRACK, Material.DEEPSLATE)
            ),
            new Palette.BlockReplacement(
                    Set.of(Material.TUFF),
                    Set.of(Material.NETHERRACK, Material.TUFF)
            ),
            new Palette.BlockReplacement(
                    Set.of(Material.ANDESITE),
                    Set.of(Material.NETHERRACK, Material.ANDESITE)
            ),
            new Palette.BlockReplacement(
                    Set.of(Material.DIORITE),
                    Set.of(Material.NETHERRACK, Material.DIORITE)
            ),
            new Palette.BlockReplacement(
                    Tag.LEAVES.getValues(),
                    Set.of(Material.NETHER_WART_BLOCK)
            ),
            new Palette.BlockReplacement(
                    Set.of(Material.CACTUS),
                    Set.of(Material.SHROOMLIGHT)
            ),
            new Palette.BlockReplacement(
                    Tag.FLOWERS.getValues(),
                    Set.of(Material.CRIMSON_ROOTS, Material.CRIMSON_FUNGUS)
            ),
            new Palette.BlockReplacement(
                    Set.of(Material.TORCH, Material.TALL_GRASS, Material.SHORT_GRASS, Material.SWEET_BERRY_BUSH, Material.VINE),
                    Set.of(Material.AIR)
            ),
            new Palette.BlockReplacement(
                    Set.of(Material.LANTERN),
                    Set.of(Material.SOUL_LANTERN)
            )
    );

    @Override
    public String id() {
        return "red";
    }

    @Override
    public List<Palette.BlockReplacement> replacements() {
        return replacements;
    }
}
