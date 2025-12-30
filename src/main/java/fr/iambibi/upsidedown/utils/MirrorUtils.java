package fr.iambibi.upsidedown.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class MirrorUtils {
    /**
     * Converts coordinates (x, y, z) from a source world to the inverted world
     * by applying an axial reflection relative to the specified origin.
     *
     * @param x X coordinate in the source world
     * @param y Y coordinate in the source world
     * @param z Z coordinate in the source world
     * @param originX origin X for the reflection
     * @return an int[] {x', y', z'} corresponding to coordinates in the inverted world
     */
    public static int[] convertCoordinates(int x, int y, int z, int originX) {
        int mirroredX = originX - (x - originX);
        return new int[]{mirroredX, y, z};
    }

    public static Location convertLocation(Location loc, int originX) {
        double mirroredX = originX - (loc.getX() - originX);

        World invertedWorld = Bukkit.getWorld(WorldUtils.getInvertedWorldName(loc.getWorld().getName()));

        if (invertedWorld == null) return null;

        return new Location(
                invertedWorld,
                mirroredX,
                loc.getY(),
                loc.getZ(),
                loc.getYaw(),
                loc.getPitch()
        );
    }

    public static Block convertBlock(Block block, int originX) {
        Location location = block.getLocation();
        double mirroredX = originX - (location.getX() - originX);

        World invertedWorld = Bukkit.getWorld(WorldUtils.getInvertedWorldName(block.getWorld().getName()));

        if (invertedWorld == null) return null;

        return invertedWorld.getBlockAt(
                (int) Math.floor(mirroredX),
                block.getY(),
                block.getZ()
        );
    }

    public static float mirrorYaw(float yaw) {
        float mirrored = 180f - yaw;

        if (mirrored > 180f) mirrored -= 360f;
        if (mirrored < -180f) mirrored += 360f;

        return mirrored;
    }
}
