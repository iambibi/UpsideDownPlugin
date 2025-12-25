package fr.iambibi.upsidedown.registry.palette;

import fr.iambibi.upsidedown.registry.Palette;
import org.bukkit.Material;
import java.util.List;
import java.util.Set;
import org.bukkit.Tag;

public class BluePaletteBlock implements Palette.BlockPalette {

    private final List<Palette.BlockReplacement> replacements = List.of(
            new Palette.BlockReplacement(
                    Set.of(Material.DEEPSLATE),
                    Set.of(Material.SCULK, Material.DEEPSLATE)
            ),
            new Palette.BlockReplacement(
                    Set.of(Material.TUFF),
                    Set.of(Material.SCULK, Material.TUFF)
            ),
            new Palette.BlockReplacement(
                    Set.of(Material.ANDESITE),
                    Set.of(Material.SCULK, Material.ANDESITE)
            ),
            new Palette.BlockReplacement(
                    Set.of(Material.DIORITE),
                    Set.of(Material.SCULK, Material.DIORITE)
            ),
            new Palette.BlockReplacement(
                    Tag.LEAVES.getValues(),
                    Set.of(Material.AIR)
            ),
            new Palette.BlockReplacement(
                    Tag.FLOWERS.getValues(),
                    Set.of(Material.SMALL_AMETHYST_BUD, Material.MEDIUM_AMETHYST_BUD)
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
        return "blue";
    }

    @Override
    public List<Palette.BlockReplacement> replacements() {
        return replacements;
    }
}
