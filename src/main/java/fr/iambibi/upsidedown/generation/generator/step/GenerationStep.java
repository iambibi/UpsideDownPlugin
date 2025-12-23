package fr.iambibi.upsidedown.generation.generator.step;

import fr.iambibi.upsidedown.generation.generator.GenerationContext;

public interface GenerationStep {
    void start(GenerationContext context, Runnable onComplete);
}
