package fr.iambibi.upsidedown.generation.palette;

import java.util.HashMap;
import java.util.Map;

public class PaletteRegistry {

    private static final Map<String, Palette.BlockPalette> PALETTES = new HashMap<>();
    static {
        register(new BluePaletteBlock());
    }

    public static void register(Palette.BlockPalette palette) {
        PALETTES.put(palette.id(), palette);
    }

    public static Palette.BlockPalette get(String id) {
        return PALETTES.get(id);
    }
}
