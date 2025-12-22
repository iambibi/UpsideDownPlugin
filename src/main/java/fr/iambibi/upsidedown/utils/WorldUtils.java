package fr.iambibi.upsidedown.utils;

import fr.iambibi.upsidedown.generation.UpsideDownWorldManager;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldUtils {
    public static boolean isUpsideDownWorld(World world) {
        return world.getName().equals(UpsideDownWorldManager.DIMENSION_NAME);
    }

    public static boolean isInUpsideDown(Player player) {
        return isUpsideDownWorld(player.getWorld());
    }
}
