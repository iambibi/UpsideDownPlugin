package fr.iambibi.upsidedown;

import io.papermc.paper.datapack.Datapack;
import lombok.Getter;
import org.bukkit.Bukkit;
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
        Datapack pack = this.getServer().getDatapackManager().getPack(getPluginMeta().getName() + "/omc");
        if (pack != null) {
            if (pack.isEnabled()) {
                getSLF4JLogger().info("\u001B[32m✔ Lancement du datapack réussi\u001B[0m");
            } else {
                getSLF4JLogger().warn("\u001B[31m✘ Lancement du datapack échoué\u001B[0m");
            }
        }
    }

    @Override
    public void onDisable() {

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
