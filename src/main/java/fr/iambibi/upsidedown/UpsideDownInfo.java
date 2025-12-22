package fr.iambibi.upsidedown;

import fr.iambibi.upsidedown.generation.palette.Palette;
import org.bukkit.World;

public record UpsideDownInfo(World sourceWorld, World invertedWorld, int originX, int originY, int originZ, int radius, Palette.BlockPalette palette) {

    public String getSourceWorldName() {
        return sourceWorld.getName();
    }

    public String getInvertedWorldName() {
        return invertedWorld.getName();
    }
}
