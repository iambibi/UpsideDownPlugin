package fr.iambibi.upsidedown.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public class FeaturesUtils {

    /**
     * Places a configured feature at the specified position in the target world.
     *
     * @param targetWorld The Bukkit world where the feature will be placed.
     * @param namespace   The namespace of the configured feature.
     * @param featureId   The ID of the configured feature.
     * @param pos         The position where the feature will be placed.
     */
    public static void placeFeature(World targetWorld, String namespace, String featureId, BlockPos pos) {
        ServerLevel level = ((CraftWorld) targetWorld).getHandle();
        ConfiguredFeature<?, ?> configuredFeature = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getValue(Identifier.fromNamespaceAndPath(namespace, featureId));
        if (configuredFeature == null) {
            System.out.println("Configured feature not found: " + namespace + ":" + featureId);
            return;
        }

        configuredFeature.place(level, level.getChunkSource().getGenerator(), level.getRandom(), pos);
    }
}
