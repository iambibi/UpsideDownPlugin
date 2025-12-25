package fr.iambibi.upsidedown.registry;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Palette {
    public interface BlockPalette {
        String id();
        List<BlockReplacement> replacements();

        default Map<Material, List<Material>> buildReplacementCache() {
            Map<Material, List<Material>> cache = new HashMap<>();
            for (BlockReplacement replacement : replacements()) {
                List<Material> targetList = new ArrayList<>(replacement.targets());
                for (Material source : replacement.sources()) {
                    cache.putIfAbsent(source, targetList);
                }
            }
            return cache;
        }

        default BlockData applyToBlockData(BlockData data, Map<Material, List<Material>> cache) {
            Material original = data.getMaterial();
            List<Material> targets = cache.get(original);

            if (targets != null && !targets.isEmpty()) {
                Material newMaterial = pickRandom(targets);
                return newMaterial.createBlockData();
            }

            return data;
        }

        private static Material pickRandom(List<Material> materials) {
            return materials.get(ThreadLocalRandom.current().nextInt(materials.size()));
        }
    }

    public record BlockReplacement(
            Set<Material> sources,
            Set<Material> targets
    ) {}
}
