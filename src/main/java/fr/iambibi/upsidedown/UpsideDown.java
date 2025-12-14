package fr.iambibi.upsidedown;

import fr.iambibi.upsidedown.generation.InvertedGenerator;
import fr.iambibi.upsidedown.generation.UpsideDownWorldManager;
import io.papermc.paper.datapack.Datapack;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public class UpsideDown extends JavaPlugin {
    @Getter
    static UpsideDown instance;
    @Getter
    static FileConfiguration configs;

    @Override
    public void onEnable() {
        instance = this;

        /* CONFIG */
        saveDefaultConfig();
        configs = this.getConfig();

        logLoadMessage();
        Datapack pack = this.getServer().getDatapackManager().getPack(getPluginMeta().getName() + "/" + UpsideDownBootstrap.DATAPACK_ID);
        if (pack != null) {
            if (pack.isEnabled()) {
                getSLF4JLogger().info("\u001B[32m✔ Lancement du datapack réussi\u001B[0m");
            } else {
                getSLF4JLogger().warn("\u001B[31m✘ Lancement du datapack échoué\u001B[0m");
            }
        }

        String mainWorldName = getConfig().getString("main_world");
        String upsideDownWorldName = getConfig().getString("upsidedown_world");

        int radius = getConfig().getInt("radius-inverted");

        int originX = getConfig().getInt("origin.x");
        int originY = getConfig().getInt("origin.y");
        int originZ = getConfig().getInt("origin.z");

        if (mainWorldName == null || upsideDownWorldName == null) {
            getSLF4JLogger().error("Erreur de config : un des éléments de config sont nul !");
            return;
        }

        World mainWorld = Bukkit.getWorld(mainWorldName);
        if (mainWorld == null) {
            getSLF4JLogger().error("Le monde principal '{}' est introuvable ! Veuillez vérifier la configuration.", mainWorldName);
            return;
        }

        /* INIT */
        UpsideDownWorldManager.init(upsideDownWorldName);

        /* MAIN */
        World upsideDownWorld = UpsideDownWorldManager.createInvertedWorld(upsideDownWorldName, originX, originZ, radius);

        UpsideDownInfo info = new UpsideDownInfo(
                mainWorld,
                upsideDownWorld,
                originX,
                originY,
                originZ,
                radius
        );

        if (upsideDownWorld == null) {
            getSLF4JLogger().error("L'UpsideDown '{}' n'a pas pu être créé !", upsideDownWorldName);
            return;
        }

        if (UpsideDownWorldManager.hasSeedChanged())
            new InvertedGenerator(info).generate();
    }

    @Override
    public void onDisable() {
        UpsideDownWorldManager.save();
    }

    public static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }

    private void logLoadMessage() {
        Logger log = getSLF4JLogger();

        String pluginVersion = getPluginMeta().getVersion();
        String server = Bukkit.getName() + " " + Bukkit.getVersion();

        log.info("\u001B[1;34m██╗   ██╗ ██████╗ ███████╗██╗██████╗ ███████╗     ██████╗  ██████╗ ██╗    ██╗███╗   ██╗\u001B[0m");
        log.info("\u001B[1;34m██║   ██║ ██╔══██╗██╔════╝██║██╔══██╗██╔════╝     ██╔══██╗██╔═══██╗██║    ██║████╗  ██║\u001B[0m");
        log.info("\u001B[1;34m██║   ██║ ██████╔╝███████╗██║██║  ██║█████╗       ██║  ██║██║   ██║██║ █╗ ██║██╔██╗ ██║\u001B[0m");
        log.info("\u001B[1;34m██║   ██║ ██╔═══╝ ╚════██║██║██║  ██║██╔══╝       ██║  ██║██║   ██║██║███╗██║██║╚██╗██║\u001B[0m");
        log.info("\u001B[1;34m╚██████╔╝ ██║     ███████║██║██████╔╝███████╗     ██████╔╝╚██████╔╝╚███╔███╔╝██║ ╚████║\u001B[0m");
        log.info("\u001B[1;34m ╚═════╝  ╚═╝     ╚══════╝╚═╝╚═════╝ ╚══════╝     ╚═════╝  ╚═════╝  ╚══╝╚══╝ ╚═╝  ╚═══╝\u001B[0m");

        log.info("\u001B[0;90mUpside Down v{} — running on {}\u001B[0m", pluginVersion, server);
    }
}
