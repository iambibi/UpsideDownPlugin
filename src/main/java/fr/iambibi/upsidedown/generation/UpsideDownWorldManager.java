package fr.iambibi.upsidedown.generation;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.UpsideDownInfo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class UpsideDownWorldManager {

    private static UpsideDown plugin;

    private static String DIMENSION_NAME;
    private static File seedFile;
    private static FileConfiguration seedConfig;

    /**
     * Initializes the UpsideDownWorldManager with the specified dimension name.
     * @param upsideDownName The name of the upside-down dimension.
     */
    public static void init(String upsideDownName) {
        plugin = UpsideDown.getInstance();

        DIMENSION_NAME = upsideDownName;
        seedFile = new File(UpsideDown.getInstance().getDataFolder() + "/data/", "seed.yml");
        loadSeed();
    }

    /**
     * Saves the seed of the upside-down world to the configuration file.
     */
    public static void save() {
        World upsideDown = Bukkit.getWorld(DIMENSION_NAME);
        if (upsideDown == null) return;

        UpsideDown.getInstance().getSLF4JLogger().info("Saving seed: {}", upsideDown.getSeed());
        saveSeed(upsideDown.getSeed());
    }

    /**
     * Creates or loads an inverted world with the specified parameters.
     * @param name The name of the world.
     * @param originX The X coordinate of the origin.
     * @param originZ The Z coordinate of the origin.
     * @param radius The radius of the inverted world.
     * @return The created or loaded inverted world.
     */
    public static World createInvertedWorld(String name, int originX, int originZ, int radius) {
        WorldCreator creator = new WorldCreator(name);

        File worldFolder = new File(Bukkit.getWorldContainer(), name);
        long seed;
        if (!worldFolder.exists()) {
            seed = UpsideDownWorldManager.createSeed();
            creator.seed(seed);
            plugin.getSLF4JLogger().info("New UpsideDown world created with seed: {}", seed);
        } else {
            World existing = Bukkit.getWorld(DIMENSION_NAME);
            seed = (existing != null) ? existing.getSeed() : creator.seed();
            plugin.getSLF4JLogger().info("Loading existing UpsideDown world with seed: {}", seed);
        }

        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.generatorSettings("{\"biome\":\"minecraft:the_void\",\"layers\":[{\"block\":\"minecraft:air\",\"height\":1}]}");
        creator.generateStructures(false);
        creator.generator(new WallChunkGenerator(originX, originZ, radius));

        World invertedWorld = creator.createWorld();

        invertedWorld.getWorldBorder().setSize(radius*2);

        return creator.createWorld();
    }

    /**
     * Generates a non-zero random seed.
     * @return A non-zero random seed.
     */
    public static long createSeed() {
        Random random = ThreadLocalRandom.current();
        long seed = random.nextLong();

        while (seed == 0) {
            seed = random.nextLong();
        }

        return seed;
    }

    /**
     * Loads the seed configuration from the seed file.
     */
    public static void loadSeed() {
        if (!seedFile.exists()) {
            UpsideDown.getInstance().getSLF4JLogger().info("Fichier seed.yml manquant, il sera créé au saveSeed().");
        }
        seedConfig = YamlConfiguration.loadConfiguration(seedFile);
    }

    /**
     * Saves the specified seed to the configuration file.
     * @param seed The seed to save.
     */
    public static void saveSeed(long seed) {
        seedConfig.set(DIMENSION_NAME + "_seed", seed);
        try {
            seedConfig.save(seedFile);
        } catch (IOException e) {
            UpsideDown.getInstance().getSLF4JLogger().error("Cannot save seed of {}", DIMENSION_NAME, e);
        }
    }

    /**
     * Checks if the seed of the upside-down world has changed.
     * @return True if the seed has changed, false otherwise.
     */
    public static boolean hasSeedChanged() {
        long saved = seedConfig.getLong(DIMENSION_NAME + "_seed", -1);
        World upsideDown = Bukkit.getWorld(DIMENSION_NAME);
        if (upsideDown == null) return false;

        long current = upsideDown.getSeed();
        return saved != current;
    }
}
