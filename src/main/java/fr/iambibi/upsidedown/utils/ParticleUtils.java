package fr.iambibi.upsidedown.utils;

import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleUtils {
    public static void sendParticlePacket(Player player, Location loc, Particle particle) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                CraftParticle.createParticleParam(particle, null),
                false,
                false,
                loc.getX(), loc.getY(), loc.getZ(),
                0.2f, 0.2f, 0.2f,
                0.01f,
                3
        );

        nmsPlayer.connection.send(packet);
    }

    public static <T> void sendParticlePacket(Player player, Location location, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed, T data) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                CraftParticle.createParticleParam(particle, data),
                false,
                false,
                location.x(), location.y(), location.z(),
                (float) offsetX, (float) offsetY, (float) offsetZ,
                (float) speed,
                count
        );

        nmsPlayer.connection.send(packet);
    }
}
