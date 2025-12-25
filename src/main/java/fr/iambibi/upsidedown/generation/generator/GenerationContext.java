package fr.iambibi.upsidedown.generation.generator;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.UpsideDownInfo;
import org.bukkit.World;

public class GenerationContext {

    public final UpsideDown plugin;
    public final World sourceWorld;
    public final World targetWorld;
    public final int radius;
    public final int originX;
    public final int originZ;

    public GenerationContext(UpsideDownInfo info) {
        this.plugin = UpsideDown.getInstance();
        this.sourceWorld = info.sourceWorld();
        this.targetWorld = info.invertedWorld();
        this.radius = info.radius();
        this.originX = info.originX();
        this.originZ = info.originZ();
    }

    /**
     * Checks if the given coordinates are inside the defined radius from the origin.
     * @param x X coordinate to check.
     * @param z Z coordinate to check.
     * @return True if inside radius, false otherwise.
     */
    public boolean isInsideRadius(int x, int z) {
        double dx = x - originX;
        double dz = z - originZ;
        return dx * dx + dz * dz <= radius * radius;
    }
}

