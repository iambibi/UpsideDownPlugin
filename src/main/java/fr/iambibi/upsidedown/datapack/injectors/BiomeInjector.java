package fr.iambibi.upsidedown.datapack.injectors;

import com.google.common.collect.BiMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Particle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class BiomeInjector {

    private final JsonObject root = new JsonObject();
    private final JsonObject effects = new JsonObject();
    private final JsonObject attributes = new JsonObject();


    public BiomeInjector() {
        root.addProperty("has_precipitation", true);
        root.addProperty("temperature", 0.5);
        root.addProperty("downfall", 0.5);

        root.add("effects", effects);
        root.add("attributes", attributes);
        root.add("carvers", new JsonArray());
        root.add("features", new JsonArray());
        root.add("spawn_costs", new JsonObject());
        root.add("spawners", new JsonObject());
    }

    public BiomeInjector hasPrecipitation(boolean b) {
        root.addProperty("has_precipitation", b);
        return this;
    }

    public BiomeInjector ambientParticle(Map<Particle, Double> particles) {
        JsonArray array = new JsonArray();

        for (Map.Entry<Particle, Double> entry : particles.entrySet()) {
            JsonObject obj = new JsonObject();
            Particle particle = entry.getKey();
            JsonObject particleObj = new JsonObject();
            particleObj.addProperty("type", particle.getKey().toString());
            particleObj.addProperty("delay", 1);
            obj.add("particle", particleObj);
            obj.addProperty("probability", entry.getValue());
            array.add(obj);
        }

        attributes.add("minecraft:visual/ambient_particles", array);
        return this;
    }

    public BiomeInjector waterColor(String color) {
        effects.addProperty("water_color", color);
        return this;
    }

    public BiomeInjector grassColor(String color) {
        effects.addProperty("grass_color", color);
        return this;
    }

    public BiomeInjector foliageColor(String color) {
        effects.addProperty("foliage_color", color);
        return this;
    }

    public BiomeInjector dryFoliageColor(String color) {
        effects.addProperty("dry_foliage_color", color);
        return this;
    }

    public BiomeInjector addAttribute(String key, Object value) {
        key = "minecraft:" + key;
        if (value instanceof Number) attributes.addProperty(key, (Number) value);
        else if (value instanceof Boolean) attributes.addProperty(key, (Boolean) value);
        else attributes.addProperty(key, value.toString());
        return this;
    }

    public BiomeInjector ambientSoundLoop(Sound loopSound) {
        JsonObject ambient = attributes.has("minecraft:audio/ambient_sounds")
                ? attributes.getAsJsonObject("minecraft:audio/ambient_sounds")
                : new JsonObject();
        ambient.addProperty("loop", loopSound.name().asString());
        attributes.add("minecraft:audio/ambient_sounds", ambient);
        return this;
    }

    public BiomeInjector ambientSoundMood(Sound moodSound, int tickDelay, int offset, int blockSearchExtent) {
        JsonObject ambient = attributes.has("minecraft:audio/ambient_sounds")
                ? attributes.getAsJsonObject("minecraft:audio/ambient_sounds")
                : new JsonObject();

        JsonObject mood = new JsonObject();
        mood.addProperty("sound", moodSound.name().asString());
        mood.addProperty("tick_delay", tickDelay);
        mood.addProperty("offset", offset);
        mood.addProperty("block_search_extent", blockSearchExtent);
        ambient.add("mood", mood);

        attributes.add("minecraft:audio/ambient_sounds", ambient);
        return this;
    }

    public BiomeInjector ambientSoundAddition(Sound additionSound, double tickChance) {
        JsonObject ambient = attributes.has("minecraft:audio/ambient_sounds")
                ? attributes.getAsJsonObject("minecraft:audio/ambient_sounds")
                : new JsonObject();

        JsonArray additions;
        if (ambient.has("additions") && ambient.get("additions").isJsonArray()) {
            additions = ambient.getAsJsonArray("additions");
        } else {
            additions = new JsonArray();
        }

        JsonObject additionObj = new JsonObject();
        additionObj.addProperty("sound", additionSound.name().asString());
        additionObj.addProperty("tick_chance", tickChance);
        additions.add(additionObj);

        ambient.add("additions", additions);
        attributes.add("minecraft:audio/ambient_sounds", ambient);

        return this;
    }

    public BiomeInjector backgroundMusic(Sound sound, int minDelay, int maxDelay) {
        JsonObject bgMusic = new JsonObject();
        JsonObject defaultMusic = new JsonObject();

        defaultMusic.addProperty("sound", sound.name().asString());
        defaultMusic.addProperty("min_delay", minDelay);
        defaultMusic.addProperty("max_delay", maxDelay);

        bgMusic.add("default", defaultMusic);
        attributes.add("minecraft:audio/background_music", bgMusic);

        return this;
    }

    public void build(Path namespaceDir, String biomeName) throws IOException {
        Path biomeDir = namespaceDir.resolve("worldgen/biome");
        Files.createDirectories(biomeDir);

        Files.writeString(
                biomeDir.resolve(biomeName + ".json"),
                new GsonBuilder().setPrettyPrinting().create().toJson(root)
        );
    }
}
