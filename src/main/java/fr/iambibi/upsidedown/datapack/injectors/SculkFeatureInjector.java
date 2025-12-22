package fr.iambibi.upsidedown.datapack.injectors;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Particle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SculkFeatureInjector {

    private final JsonObject root = new JsonObject();
    private final JsonObject config = new JsonObject();

    public SculkFeatureInjector() {
        root.addProperty("type", "minecraft:sculk_patch");
        root.add("config", config);
    }

    public SculkFeatureInjector amountPerCharge(int value) {
        config.addProperty("amount_per_charge", value);
        return this;
    }

    public SculkFeatureInjector catalystChance(double value) {
        config.addProperty("catalyst_chance", value);
        return this;
    }

    public SculkFeatureInjector chargeCount(int value) {
        config.addProperty("charge_count", value);
        return this;
    }

    public SculkFeatureInjector extraRareGrowths(int value) {
        config.addProperty("extra_rare_growths", value);
        return this;
    }

    public SculkFeatureInjector growthRounds(int value) {
        config.addProperty("growth_rounds", value);
        return this;
    }

    public SculkFeatureInjector spreadAttempts(int value) {
        config.addProperty("spread_attempts", value);
        return this;
    }

    public SculkFeatureInjector spreadRounds(int value) {
        config.addProperty("spread_rounds", value);
        return this;
    }

    public void build(Path namespaceDir, String featureName) throws IOException {
        Path featureDir = namespaceDir.resolve("worldgen/configured_feature");
        Files.createDirectories(featureDir);

        Files.writeString(
                featureDir.resolve(featureName + ".json"),
                new GsonBuilder().setPrettyPrinting().create().toJson(root)
        );
    }
}
