package fr.iambibi.upsidedown.additions;

import fr.iambibi.upsidedown.utils.ParticleUtils;
import fr.iambibi.upsidedown.utils.WorldUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
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
                    Particle.FLASH,
                    Color.WHITE
            );

            ParticleUtils.sendParticlePacket(
                    onlinePlayer,
                    origin,
                    Particle.FLASH,
                    Color.PURPLE
            );
        }

        if (ticks % LIGHTNING_INTERVAL == 0) {
            targetWorld.spawn(origin, LightningStrike.class, e ->
                    e.setFlashCount(4));

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!WorldUtils.isInUpsideDown(onlinePlayer)) continue;

                targetWorld.strikeLightningEffect(onlinePlayer.getLocation().add(0, -100, 0));
            }
        }
    }
}
