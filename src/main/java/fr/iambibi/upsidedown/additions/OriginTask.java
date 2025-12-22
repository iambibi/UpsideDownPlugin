package fr.iambibi.upsidedown.additions;

import fr.iambibi.upsidedown.utils.ParticleUtils;
import fr.iambibi.upsidedown.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class OriginTask implements Runnable {

    private static final int LIGHTNING_INTERVAL = 7 * 20; // 7 seconds
    private static final int FLASH_INTERVAL = 20; // 1 second

    private final World targetWorld;
    private final Location origin;

    private int ticks = 0;

    public OriginTask(World targetWorld, int originX, int originY, int originZ) {
        this.targetWorld = targetWorld;
        this.origin = new Location(targetWorld, originX + 0.5, originY, originZ + 0.5);
    }

    @Override
    public void run() {
        if (targetWorld == null) return;

        ticks += FLASH_INTERVAL;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!WorldUtils.isInUpsideDown(onlinePlayer)) continue;

            ParticleUtils.sendParticlePacket(
                    onlinePlayer,
                    origin,
                    Particle.FLASH
            );
        }

        if (ticks % LIGHTNING_INTERVAL == 0) {
            targetWorld.spawnEntity(origin, EntityType.LIGHTNING_BOLT);
            targetWorld.strikeLightningEffect(origin);
        }
    }
}
