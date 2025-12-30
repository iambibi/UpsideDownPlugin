package fr.iambibi.upsidedown.utils;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.UpsideDownInfo;
import fr.iambibi.upsidedown.generation.UpsideDownWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;

public class WorldUtils {
    public static boolean isUpsideDownWorld(World world) {
        return world.getName().equals(UpsideDownWorldManager.DIMENSION_NAME);
    }

    public static boolean isInUpsideDown(Player player) {
        return isUpsideDownWorld(player.getWorld());
    }

    public static String getInvertedWorldName(String initialName) {
        UpsideDownInfo info = UpsideDown.getInfo();
        if (Objects.equals(initialName, UpsideDownWorldManager.DIMENSION_NAME)) {
            return info.getSourceWorldName();
        } else {
            return UpsideDownWorldManager.DIMENSION_NAME;
        }
    }

    public static void deleteWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        World fallback = Bukkit.getWorlds().getFirst();

        for (Player player : world.getPlayers()) {
            player.teleport(fallback.getSpawnLocation());
        }

        Bukkit.unloadWorld(world, false);

        File folder = world.getWorldFolder();
        try {
            Files.walk(folder.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
