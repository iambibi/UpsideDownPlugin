package fr.iambibi.upsidedown.registry;

import fr.iambibi.upsidedown.registry.palette.BluePaletteBlock;
import fr.iambibi.upsidedown.registry.palette.RedPaletteBlock;
import net.minecraft.world.level.chunk.PaletteResize;

import java.util.HashMap;
import java.util.Map;

public class PaletteRegistry {

    private static final Map<String, Palette.BlockPalette> PALETTES = new HashMap<>();

    public static void init() {
        register(new BluePaletteBlock());
        register(new RedPaletteBlock());
    }

    public static void register(Palette.BlockPalette palette) {
        PALETTES.put(palette.id(), palette);
    }

    public static Palette.BlockPalette get(String id) {
        return PALETTES.get(id);
    }
}
