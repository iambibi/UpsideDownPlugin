package fr.iambibi.upsidedown.generation.generator.step;

import fr.iambibi.upsidedown.fixes.postprocesswarn.SculkPatchFeature;
import fr.iambibi.upsidedown.generation.generator.GenerationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;

public class SculkFeaturesGenerator implements GenerationStep {

    private final int SCULK_FEATURE_BY_TICK = 1000;

    private final List<BlockPos> positions;
    public SculkFeaturesGenerator(List<BlockPos> positions) {
        this.positions = positions;
    }

    //todo remove temporary sculk feature generator and integrate NMS code modified without PostProcessing
    @Override
    public void start(GenerationContext ctx, Runnable onComplete) {
        int[] processed = {0};

        ctx.plugin.getLogger().info("Starting sculk features placement (" + positions.size() + " features)");

        ServerLevel level = ((CraftWorld) ctx.targetWorld).getHandle();

        Iterator<BlockPos> iterator = positions.iterator();
        new BukkitRunnable() {
            @Override
            public void run() {
                int placed = 0;

                while (iterator.hasNext() && placed < SCULK_FEATURE_BY_TICK) {
                    BlockPos pos = iterator.next().above();


                    try {
                        SculkPatchFeature.place(level, pos, level.random, new fr.iambibi.upsidedown.fixes.postprocesswarn.SculkPatchConfiguration());
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
        }.runTaskTimer(ctx.plugin, 0L, 1L);
    }
}
