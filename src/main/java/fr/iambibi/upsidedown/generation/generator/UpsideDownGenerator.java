package fr.iambibi.upsidedown.generation.generator;

import fr.iambibi.upsidedown.UpsideDownInfo;
import fr.iambibi.upsidedown.generation.generator.step.EntitesGenerator;
import fr.iambibi.upsidedown.generation.generator.step.FeaturesGenerator;
import fr.iambibi.upsidedown.generation.generator.step.GenerationStep;
import fr.iambibi.upsidedown.generation.generator.step.MirrorBlockGenerator;

import java.util.*;

public class UpsideDownGenerator {

    private final GenerationContext context;
    private final Queue<GenerationStep> steps = new ArrayDeque<>();

    public UpsideDownGenerator(UpsideDownInfo info) {
        this.context = new GenerationContext(info);

        steps.add(new MirrorBlockGenerator());
        steps.add(new FeaturesGenerator());
        steps.add(new EntitesGenerator());
    }

    public void start() {
        context.plugin.getLogger().info("Start UpsideDown generation");

        runNextStep();
    }

    private void runNextStep() {
        GenerationStep step = steps.poll();
        if (step == null) {
            context.plugin.getLogger().info("UpsideDown generation COMPLETE");
            return;
        }

        step.start(context, this::runNextStep);
    }
}
