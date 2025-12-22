package fr.iambibi.upsidedown.datapack.injectors;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DimensionTypeInjector {
    //todo: waiting https://github.com/PaperMC/Paper/pull/12922

    public static final String DIMENSION_TYPE_NAME = "upsidedown";
    private static final String DIMENSION_TYPES_DIR = "dimension_type";

    public static void inject(Path namespaceDir) throws IOException {
        Path dimensionTypesDir = namespaceDir.resolve(DIMENSION_TYPES_DIR);
        Files.createDirectories(dimensionTypesDir);

        JsonObject root = new JsonObject();

        root.addProperty("has_skylight", true);
        root.addProperty("has_ceiling", false);
        root.addProperty("coordinate_scale", 1.0);
        root.addProperty("ambient_light", 0);
        root.addProperty("logical_height", 384);
        root.addProperty("skybox", "end");
        root.addProperty("infiniburn", "#infiniburn_overworld");
        root.addProperty("min_y", -64);
        root.addProperty("height", 384);
        root.addProperty("monster_spawn_light_level", 0);
        root.addProperty("monster_spawn_block_light_limit", 0);

        Files.writeString(
                dimensionTypesDir.resolve(DIMENSION_TYPE_NAME + ".json"),
                new GsonBuilder().setPrettyPrinting().create().toJson(root)
        );
    }
}
