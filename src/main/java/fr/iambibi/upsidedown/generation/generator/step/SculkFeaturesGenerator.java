package fr.iambibi.upsidedown.generation.generator.step;

import fr.iambibi.upsidedown.datapack.UpsideDownDatapack;
import fr.iambibi.upsidedown.generation.generator.GenerationContext;
import fr.iambibi.upsidedown.utils.FeaturesUtils;
import net.minecraft.core.BlockPos;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;

public class SculkFeaturesGenerator implements GenerationStep {
    private final List<BlockPos> positions;
    public SculkFeaturesGenerator(List<BlockPos> positions) {
        this.positions = positions;
    }

    @Override
    public void start(GenerationContext ctx, Runnable onComplete) {
        int[] processed = {0};

        ctx.plugin.getLogger().info("Starting sculk features placement (" + positions.size() + " features)");

        Iterator<BlockPos> iterator = positions.iterator();
        new BukkitRunnable() {
            @Override
            public void run() {
                int placed = 0;

                while (iterator.hasNext() && placed < 250) {
                    BlockPos pos = iterator.next();

                    try {
                        FeaturesUtils.placeFeature(
                                ctx.targetWorld,
                                UpsideDownDatapack.DATAPACK_NAMESPACE,
                                "sculk_patch_buffed",
                                pos
                        );
                    } catch (Throwable ignored) {
                    }

                    processed[0]++;
                    placed++;
                }

                int percent = (int) ((processed[0] / (double) positions.size()) * 100);
                if (processed[0] % Math.max(positions.size() / 10, 1) == 0) {
                    ctx.plugin.getLogger().info("Sculk features placement progress: " + percent + "% (" + processed[0] + "/" + positions.size() + ")");
                }

                if (!iterator.hasNext()) {
                    cancel();
                    ctx.plugin.getLogger().info("Sculk features placement finished");
                    onComplete.run();
                }
            }
        }.runTaskTimer(ctx.plugin, 1L, 1L);
    }
}
