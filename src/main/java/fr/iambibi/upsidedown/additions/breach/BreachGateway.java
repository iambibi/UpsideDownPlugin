package fr.iambibi.upsidedown.additions.breach;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.EndGateway;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Breach")
public record BreachGateway(Location mainLoc, Location upsidedownLoc) implements ConfigurationSerializable {
    public BreachGateway(Map<String, Object> map) {
        this(
                locFromMap((Map<String, Object>) map.get("main")),
                locFromMap((Map<String, Object>) map.get("upsidedown"))
        );
    }

    private static Location locFromMap(Map<String, Object> m) {
        return new Location(
                Bukkit.getWorld((String) m.get("world_name")),
                ((Number) m.get("x")).intValue(),
                ((Number) m.get("y")).intValue(),
                ((Number) m.get("z")).intValue()
        );
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> main = new HashMap<>();
        main.put("world_name", mainLoc.getWorld().getName());
        main.put("x", mainLoc.getBlockX());
        main.put("y", mainLoc.getBlockY());
        main.put("z", mainLoc.getBlockZ());
        map.put("main", main);

        Map<String, Object> upsidedown = new HashMap<>();
        upsidedown.put("world_name", upsidedownLoc.getWorld().getName());
        upsidedown.put("x", upsidedownLoc.getBlockX());
        upsidedown.put("y", upsidedownLoc.getBlockY());
        upsidedown.put("z", upsidedownLoc.getBlockZ());
        map.put("upsidedown", upsidedown);

        return map;
    }
}
