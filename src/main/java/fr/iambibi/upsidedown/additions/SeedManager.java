package fr.iambibi.upsidedown.additions;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.generation.UpsideDownWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SeedManager {
    private static File seedFile;
    private static FileConfiguration seedConfig;

    private static Set<String> WORLD_SEED_NAMES;

    public static void init() {
        seedFile = new File(UpsideDown.getInstance().getDataFolder() + "/data/", "seed.yml");
        loadSeed();

        WORLD_SEED_NAMES = Set.of(
                UpsideDownWorldManager.DIMENSION_NAME,
                UpsideDown.getInfo().getSourceWorldName()
        );
    }

    /**
     * Saves the seed of the upside-down world to the configuration file.
     */
    public static void save() {
        for (String worldName : WORLD_SEED_NAMES) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            UpsideDown.getInstance().getSLF4JLogger().info("Saving seed of {}: {}", worldName, world.getSeed());
            saveSeed(worldName, world.getSeed());
        }
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
    public static void saveSeed(String dimensionName, long seed) {
        seedConfig.set(dimensionName + "_seed", seed);
        try {
            seedConfig.save(seedFile);
        } catch (IOException e) {
            UpsideDown.getInstance().getSLF4JLogger().error("Cannot save seed of {}", dimensionName, e);
        }
    }

    /**
     * Checks if the seed of the upside-down world has changed.
     * @return True if the seed has changed, false otherwise.
     */
    public static boolean hasSeedChanged(String worldName) {
        long saved = seedConfig.getLong(worldName + "_seed", -1);
        World upsideDown = Bukkit.getWorld(worldName);
        if (upsideDown == null) return false;

        long current = upsideDown.getSeed();
        return saved != current;
    }
}
