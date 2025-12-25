package fr.iambibi.upsidedown.datapack;

import fr.iambibi.upsidedown.datapack.injectors.BiomeInjector;
import fr.iambibi.upsidedown.datapack.injectors.PackMetaInjector;
import fr.iambibi.upsidedown.registry.UpsideDownBiomeRegistry;
import fr.iambibi.upsidedown.utils.FilesUtils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Particle;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class UpsideDownDatapack {
    public static final String DATAPACK_ID = "upsidedown_datapack";
    public static final String DATAPACK_NAMESPACE = "upsidedown";

    public static final String DIMENSION_NAME = "upsidedown";

    public static URI build(BootstrapContext context) throws IOException {
        Path datapackDir = context.getDataDirectory().resolve(DATAPACK_ID);

        FilesUtils.deleteDirectory(datapackDir);

        Files.createDirectories(datapackDir);

        // pack.mcmeta
        PackMetaInjector.inject(datapackDir);

        Path namespaceDir = datapackDir.resolve("data/" + DATAPACK_NAMESPACE);
        Files.createDirectories(namespaceDir);

        //DimensionTypeInjector.inject(namespaceDir);

        // ** BIOMES INJECT **
        for (UpsideDownBiomeRegistry biomeRegistry : UpsideDownBiomeRegistry.values()) {
            BiomeInjector injector = biomeRegistry.getInjector();

            injector.build(namespaceDir, biomeRegistry.getBiomeKey().value());
        }


        return datapackDir.toUri();

    }
}
