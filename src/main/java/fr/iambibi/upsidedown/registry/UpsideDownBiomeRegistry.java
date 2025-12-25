package fr.iambibi.upsidedown.registry;

import fr.iambibi.upsidedown.datapack.UpsideDownDatapack;
import fr.iambibi.upsidedown.datapack.injectors.BiomeInjector;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
public enum UpsideDownBiomeRegistry {
    ORIGIN(
            new BiomeInjector()
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
                    ),
            NamespacedKey.fromString(UpsideDownDatapack.DATAPACK_NAMESPACE + ":origin"),
            "blue"
    ),
    INVERTED(
            new BiomeInjector()
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
                    .addAttribute("visual/star_brightness", 0.8)
                    .addAttribute("visual/cloud_color", "#353255a6")
                    .addAttribute("visual/fog_color", "#242d37")
                    .addAttribute("visual/fog_start_distance", 16.0)
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
                    ),
            NamespacedKey.fromString(UpsideDownDatapack.DATAPACK_NAMESPACE + ":inverted"),
            "blue"
    ),
    RED_INVERTED(
            new BiomeInjector()
                    .hasPrecipitation(false)
                    .dryFoliageColor("#8c0b0e")
                    .grassColor("#820003")
                    .waterColor("#530002")
                    .foliageColor("#8c0b0e")
                    .ambientParticle(Map.of(
                            Particle.TRIAL_SPAWNER_DETECTION, 0.01,
                            Particle.RAID_OMEN, 0.001,
                            Particle.CRIMSON_SPORE, 0.08
                    ))
                    .addAttribute("visual/sky_light_color", "#cc0005")
                    .addAttribute("visual/sky_light_factor", 0.45)
                    .addAttribute("visual/star_brightness", 0.1)
                    .addAttribute("visual/cloud_color", "#840d0db8")
                    .addAttribute("visual/fog_color", "#492127")
                    .addAttribute("visual/fog_start_distance", 32.0)
                    .addAttribute("visual/fog_end_distance", 112.0)
                    .addAttribute("visual/sky_color", "#aa0004")
                    .ambientSoundMood(
                            Sound.sound(Key.key("minecraft:ambient.basalt_deltas.mood"), Sound.Source.AMBIENT, 1f, 0.6f),
                            6000,
                            2,
                            8
                    ).ambientSoundLoop(
                            Sound.sound(Key.key("minecraft:ambient.crimson_forest.loop"), Sound.Source.AMBIENT, 1f, 2f)
                    ).ambientSoundAddition(
                            Sound.sound(Key.key("minecraft:entity.lightning_bolt.thunder"), Sound.Source.AMBIENT, 0.8f, 0.1f),
                            0.0003
                    ).ambientSoundAddition(
                            Sound.sound(Key.key("minecraft:block.end_portal.spawn"), Sound.Source.AMBIENT, 0.1f, 0.1f),
                            0.000111
                    ),
            NamespacedKey.fromString(UpsideDownDatapack.DATAPACK_NAMESPACE + ":red_inverted"),
            "red"
    ),
    ;

    private final NamespacedKey biomeKey;
    private final BiomeInjector injector;
    private final String paletteId;

    UpsideDownBiomeRegistry(BiomeInjector injector, NamespacedKey biomeKey, String paletteId) {
        this.injector = injector;
        this.biomeKey = biomeKey;
        this.paletteId = paletteId;
    }

    public Biome getBiome() {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).get(biomeKey);
    }

    public Palette.BlockPalette getPalette() {
        return PaletteRegistry.get(paletteId);
    }
}
