package fr.iambibi.upsidedown.generation.generator.step;

import fr.iambibi.upsidedown.datapack.UpsideDownDatapack;
import fr.iambibi.upsidedown.generation.generator.GenerationContext;
import fr.iambibi.upsidedown.utils.FeaturesUtils;
import fr.iambibi.upsidedown.utils.StructureUtils;
import net.minecraft.core.BlockPos;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FloatingIslandGenerator implements GenerationStep {
    private final List<Location> positions;

    private final List<String> FLOATING_ISLAND_FEATURES = List.of(
            "island_1",
            "island_2",
            "island_3",
            "island_4",
            "island_5",
            "island_6"
    );

    public FloatingIslandGenerator(List<Location> positions) {
        this.positions = positions;
        StructureUtils.preloadStructures(FLOATING_ISLAND_FEATURES);
    }

    @Override
    public void start(GenerationContext ctx, Runnable onComplete) {
        int[] processed = {0};

        ctx.plugin.getLogger().info("Starting floating islands placement (" + positions.size() + " islands)");

        Iterator<Location> iterator = positions.iterator();
        new BukkitRunnable() {
            @Override
            public void run() {
                int placed = 0;

                while (iterator.hasNext() && placed < 250) {
                    Location pos = iterator.next();

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

                    processed[0]++;
                    placed++;
                }

                int percent = (int) ((processed[0] / (double) positions.size()) * 100);
                if (processed[0] % Math.max(positions.size() / 10, 1) == 0) {
                    ctx.plugin.getLogger().info("Floating islands placement progress: " + percent + "% (" + processed[0] + "/" + positions.size() + ")");
                }

                if (!iterator.hasNext()) {
                    cancel();
                    ctx.plugin.getLogger().info("Floating islands placement finished");
                    Bukkit.getScheduler().runTaskLater(ctx.plugin, onComplete, 40L);
                }
            }
        }.runTaskTimer(ctx.plugin, 1L, 1L);
    }
}
