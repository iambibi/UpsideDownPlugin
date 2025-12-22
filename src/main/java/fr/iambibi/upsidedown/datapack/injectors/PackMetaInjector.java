package fr.iambibi.upsidedown.datapack.injectors;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PackMetaInjector {
    private static final String PACK_DESCRIPTION = "UpsideDown Datapack";
    private static final double PACK_FORMAT = 94.1;

    public static void inject(Path datapackDir) throws IOException {
        JsonObject root = new JsonObject();

        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", PACK_FORMAT);
        pack.addProperty("min_format", PACK_FORMAT);
        pack.addProperty("max_format", PACK_FORMAT);
        pack.addProperty("description", PACK_DESCRIPTION);

        root.add("pack", pack);

        Files.writeString(
                datapackDir.resolve("pack.mcmeta"),
                new GsonBuilder().setPrettyPrinting().create().toJson(root)
        );
    }
}
