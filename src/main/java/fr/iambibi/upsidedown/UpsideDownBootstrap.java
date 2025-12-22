package fr.iambibi.upsidedown;

import fr.iambibi.upsidedown.datapack.UpsideDownDatapack;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.event.RegistryEvents;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

@SuppressWarnings("UnstableApiUsage")
public class UpsideDownBootstrap implements PluginBootstrap {
    // todo: dimension types and timeline injector : wait https://github.com/PaperMC/Paper/pull/12922

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    try {
                        event.registrar().removeDiscoveredPack(UpsideDownDatapack.DATAPACK_ID);
                        event.registrar().discoverPack(UpsideDownDatapack.build(context), UpsideDownDatapack.DATAPACK_ID);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        ));
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new UpsideDown();
    }

}

