package fr.iambibi.upsidedown.generation.palette;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Palette {
    public interface BlockPalette {
        String id();
        List<BlockReplacement> replacements();

        default void apply(Block block) {
            Material original = block.getType();

            for (BlockReplacement replacement : replacements()) {
                if (replacement.sources().contains(original)) {
                    Material newMaterial = pickRandom(replacement.targets());
                    block.setType(newMaterial, false);
                    return;
                }
            }
        }

        private static Material pickRandom(Set<Material> materials) {
            int index = ThreadLocalRandom.current().nextInt(materials.size());
            return materials.stream().skip(index).findFirst().orElseThrow();
        }
    }

    public record BlockReplacement(
            Set<Material> sources,
            Set<Material> targets
    ) {}
}
