package fr.iambibi.upsidedown.datapack;

import fr.iambibi.upsidedown.UpsideDownBootstrap;
import fr.iambibi.upsidedown.datapack.injectors.BiomeInjector;
import fr.iambibi.upsidedown.datapack.injectors.PackMetaInjector;
import fr.iambibi.upsidedown.datapack.injectors.SculkFeatureInjector;
import fr.iambibi.upsidedown.generation.UpsideDownBiome;
import fr.iambibi.upsidedown.utils.FilesUtils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Particle;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class UpsideDownDatapack {
    public static final String DATAPACK_ID = "upsidedown_datapack";
    public static final String DATAPACK_NAMESPACE = "upsidedown";

    public static final String DIMENSION_NAME = "upsidedown";

    public static URI build(BootstrapContext context) throws IOException {
        Path datapackDir = context.getDataDirectory().resolve(DATAPACK_ID);

        FilesUtils.deleteDirectory(datapackDir);

        Files.createDirectories(datapackDir);

        // pack.mcmeta
        PackMetaInjector.inject(datapackDir);

        Path namespaceDir = datapackDir.resolve("data/" + DATAPACK_NAMESPACE);
        Files.createDirectories(namespaceDir);

        //DimensionTypeInjector.inject(namespaceDir);

        // ** BIOMES INJECT **
        BiomeInjector invertedBiome = new BiomeInjector()
                .hasPrecipitation(false)
                .dryFoliageColor("#003d73")
                .grassColor("#435f87")
                .waterColor("#01013f")
                .foliageColor("#003d73")
                .ambientParticle(Map.of(
                        Particle.SHRIEK, 0.01,
                        Particle.SONIC_BOOM, 0.001,
                        Particle.WARPED_SPORE, 0.08
                ))
                .addAttribute("visual/sky_light_color", "#062142")
                .addAttribute("visual/sky_light_factor", 0.15)
                .addAttribute("visual/star_brightness", 1.0)
                .addAttribute("visual/cloud_color", "#353255a6")
                .addAttribute("visual/fog_color", "#242d37")
                .addAttribute("visual/fog_start_distance", 8.0)
                .addAttribute("visual/fog_end_distance", 112.0)
                .addAttribute("visual/sky_color", "#0f1f4d")
                .ambientSoundMood(
                        Sound.sound(Key.key("minecraft:ambient.warped_forest.mood"), Sound.Source.AMBIENT, 2f, 0.4f),
                        6000,
                        2,
                        8
                ).ambientSoundLoop(
                        Sound.sound(Key.key("minecraft:ambient.warped_forest.loop"), Sound.Source.AMBIENT, 1f, 0.3f)
                ).ambientSoundAddition(
                        Sound.sound(Key.key("minecraft:entity.warden.sonic_boom"), Sound.Source.AMBIENT, 0.2f, 0.4f),
                        0.0003
                ).ambientSoundAddition(
                        Sound.sound(Key.key("minecraft:entity.warden.emerge"), Sound.Source.AMBIENT, 0.1f, 0.1f),
                        0.000111
                ).ambientSoundAddition(
                        Sound.sound(Key.key("minecraft:entity.warden.dig"), Sound.Source.AMBIENT, 0.1f, 0.1f),
                        0.00111
                );

        invertedBiome.build(namespaceDir, "inverted");

        BiomeInjector originBiome = new BiomeInjector()
                .hasPrecipitation(false)
                .dryFoliageColor("#003d73")
                .grassColor("#010156")
                .waterColor("#01013f")
                .foliageColor("#003d73")
                .ambientParticle(Map.of(
                        Particle.OMINOUS_SPAWNING, 0.04,
                        Particle.ENCHANT, 0.01
                ))
                .addAttribute("visual/star_brightness", 1.0)
                .addAttribute("visual/fog_color", "#817770")
                .addAttribute("visual/water_fog_color", "#556980")
                .addAttribute("visual/fog_start_distance", 16.0)
                .addAttribute("visual/fog_end_distance", 192.0)
                .addAttribute("visual/sky_color", "#b9b9b9")
                .ambientSoundLoop(
                        Sound.sound(Key.key("minecraft:block.beacon.ambient"), Sound.Source.AMBIENT, 1f, 0.1f)
                )
                .ambientSoundAddition(
                        Sound.sound(Key.key("minecraft:entity.warden.sonic_charge"), Sound.Source.AMBIENT, 0.8f, 0.1f),
                        0.0003
                ).ambientSoundAddition(
                        Sound.sound(Key.key("minecraft:block.beacon.power_select"), Sound.Source.AMBIENT, 1f, 0.1f),
                        0.0011
                ).ambientSoundAddition(
                        Sound.sound(Key.key("minecraft:block.beacon.deactivate"), Sound.Source.AMBIENT, 0.8f, 0.1f),
                        0.00031
                );

        originBiome.build(namespaceDir, "origin");

        // ** CONFIGURED FEATURES INJECT **
        new SculkFeatureInjector()
                .amountPerCharge(32)
                .chargeCount(8)
                .spreadAttempts(64)
                .spreadRounds(1)
                .extraRareGrowths(0)
                .growthRounds(0)
                .catalystChance(0.2)
                .build(namespaceDir, "sculk_patch_buffed");

        return datapackDir.toUri();

    }
}
