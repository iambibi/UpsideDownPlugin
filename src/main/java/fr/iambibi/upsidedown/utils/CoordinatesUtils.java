package fr.iambibi.upsidedown.utils;

import org.bukkit.Location;

public class CoordinatesUtils {
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

        float yaw = loc.getYaw();
        float mirroredYaw = -yaw;

        if (mirroredYaw > 180) mirroredYaw -= 360;
        if (mirroredYaw < -180) mirroredYaw += 360;

        return new Location(
                loc.getWorld(),
                mirroredX,
                loc.getY(),
                loc.getZ(),
                mirroredYaw,
                loc.getPitch()
        );
    }
}
