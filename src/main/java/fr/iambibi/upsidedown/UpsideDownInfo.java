package fr.iambibi.upsidedown;

import org.bukkit.World;

public record UpsideDownInfo(World sourceWorld, World invertedWorld, int originX, int originY, int originZ, int radius) {

    public String getSourceWorldName() {
        return sourceWorld.getName();
    }

    public String getInvertedWorldName() {
        return invertedWorld.getName();
    }
}
