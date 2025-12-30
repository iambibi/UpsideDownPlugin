package fr.iambibi.upsidedown.generation;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.additions.SeedManager;
import fr.iambibi.upsidedown.utils.MirrorUtils;
import org.bukkit.*;

import java.io.File;

public class UpsideDownWorldManager {

    private static UpsideDown plugin;

    public static String DIMENSION_NAME;

    /**
     * Initializes the UpsideDownWorldManager with the specified dimension name.
     * @param upsideDownName The name of the upside-down dimension.
     */
    public static void init(String upsideDownName) {
        plugin = UpsideDown.getInstance();

        DIMENSION_NAME = upsideDownName;
    }

    /**
     * Creates or loads an inverted world with the specified parameters.
     * @param sourceWorld The source world to invert.
     * @param name The name of the world.
     * @param originX The X coordinate of the origin.
     * @param originZ The Z coordinate of the origin.
     * @param radius The radius of the inverted world.
     * @return The created or loaded inverted world.
     */
    public static World createInvertedWorld(World sourceWorld, String name, int originX, int originZ, int radius) {
        WorldCreator creator = new WorldCreator(name);

        File worldFolder = new File(Bukkit.getWorldContainer(), name);
        long seed;
        if (!worldFolder.exists()) {
            seed = SeedManager.createSeed();
            creator.seed(seed);
            plugin.getSLF4JLogger().info("New UpsideDown world created with seed: {}", seed);
        } else {
            World existing = Bukkit.getWorld(name);
            seed = (existing != null) ? existing.getSeed() : creator.seed();
            plugin.getSLF4JLogger().info("Loading existing UpsideDown world with seed: {}", seed);
        }

        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.biomeProvider(new UpsideDownBiomeProvider(sourceWorld, originX, originZ));
        creator.generatorSettings("{\"biome\":\"minecraft:the_void\",\"layers\":[{\"block\":\"minecraft:air\",\"height\":1}]}");
        creator.generateStructures(false);
        creator.generator(new WallChunkGenerator(originX, originZ, radius));

        World invertedWorld = creator.createWorld();

        invertedWorld.getWorldBorder().setSize(radius*2);
        invertedWorld.getWorldBorder().setCenter(originX, originZ);

        invertedWorld.setSpawnLocation(MirrorUtils.convertLocation(sourceWorld.getSpawnLocation(), originX));

        invertedWorld.setGameRule(GameRules.LOCATOR_BAR, false);
        invertedWorld.setGameRule(GameRules.KEEP_INVENTORY, false);
        invertedWorld.setGameRule(GameRules.ADVANCE_TIME, false);
        
        invertedWorld.setTime(18000);

        return invertedWorld;
    }
}
