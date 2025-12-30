package fr.iambibi.upsidedown.additions.breach;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.additions.SeedManager;
import fr.iambibi.upsidedown.additions.breach.teleport.BreachEnterPlayerListener;
import fr.iambibi.upsidedown.additions.breach.teleport.BreachEnterVehicleListener;
import fr.iambibi.upsidedown.additions.breach.teleport.BreachPortalManager;
import fr.iambibi.upsidedown.generation.UpsideDownWorldManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.CraftWorld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BreachManager {

    private static File file;
    private static FileConfiguration config;

    private static final List<BreachGateway> breachs = new ArrayList<>();

    public static void init() {
        UpsideDown.registerEvents(
                new BreachEnterPlayerListener(),
                new BreachEnterVehicleListener()
        );
        ConfigurationSerialization.registerClass(BreachGateway.class);
        file = new File(plugin.getDataFolder() + "/data/", "breachs.yml");
        load();
        BreachPortalManager.start();
    }

    public static final NamespacedKey BREACH_KEY = NamespacedKey.fromString("breach", UpsideDown.getInstance());
    private static final UpsideDown plugin = UpsideDown.getInstance();

    public static void load() {
        if (!file.exists()) {
            plugin.getSLF4JLogger().info("Fichier manquant, il sera créé au save().");
        }

        config = YamlConfiguration.loadConfiguration(file);

        World upsideDown = Bukkit.getWorld(UpsideDownWorldManager.DIMENSION_NAME);
        if (upsideDown == null) {
            plugin.getSLF4JLogger().error("Le monde {} est introuvable !", UpsideDownWorldManager.DIMENSION_NAME);
            return;
        }

        if (config.contains("breachs")) {
            for (Object obj : config.getList("breachs")) {
                if (obj instanceof BreachGateway breach) {
                    breachs.add(breach);
                }
            }
        }

        if (SeedManager.hasSeedChanged(UpsideDownWorldManager.DIMENSION_NAME)) {
            for (BreachGateway breach : new ArrayList<>(breachs)) {
                Block block = breach.mainLoc().getBlock();
                if (!block.getChunk().isLoaded())
                    block.getChunk().load();
                block.setType(Material.OBSIDIAN);
            }
            config.set("breachs", new ArrayList<>());
            save();
            return;
            // In other cases, if the main world has changed, then the upside-down will collapse.
        }
    }

    public static void save() {
        config.set("breachs", breachs);

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addBreach(Block mainBlock, Block upsideDownBlock) {
        BreachGateway breach = new BreachGateway(mainBlock.getLocation(), upsideDownBlock.getLocation());
        breachs.add(breach);
        save();

    }

    public static void removeBreach(Block block) {
        breachs.removeIf(breach -> breach.mainLoc().getBlock().equals(block) || breach.upsidedownLoc().getBlock().equals(block));
        save();
    }

    public static boolean isInUpsideDownPortal(Block block) {
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
        TheEndGatewayBlockEntity be = (TheEndGatewayBlockEntity) level.getBlockEntity(pos);
        if (be == null) return false;

        return be.persistentDataContainer.has(BREACH_KEY);
    }
}
