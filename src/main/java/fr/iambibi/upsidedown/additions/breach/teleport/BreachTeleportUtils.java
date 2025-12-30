package fr.iambibi.upsidedown.additions.breach.teleport;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.utils.MirrorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BreachTeleportUtils {

    public static void teleport(Player player, Block portal) {

        BreachPortalManager.markEnter(player);

        Vector velocity = player.getVelocity();
        boolean gliding = player.isGliding();
        boolean swimming = player.isSwimming();

        Pose pose = player.getPose();

        Location to = MirrorUtils.convertLocation(
                portal.getLocation(),
                UpsideDown.getInfo().originX()
        ).add(0.5, 0, 0.5);

        to.setYaw(MirrorUtils.mirrorYaw(player.getYaw()));
        to.setPitch(getAdaptedPitch(player));

        player.teleport(to, PlayerTeleportEvent.TeleportCause.PLUGIN);

        Bukkit.getScheduler().runTask(
                UpsideDown.getInstance(),
                () -> {
                    player.setVelocity(new Vector(
                            -velocity.getX(),
                            -velocity.getY(),
                            velocity.getZ()
                    ));

                    if (gliding) {
                        player.setGliding(true);
                    }

                    player.setPose(pose);
                }
        );
    }

    private static float getAdaptedPitch(Player player) {
        if (player.isGliding()) {
            return -player.getPitch();
        }
        return player.getPitch();
    }

    public static void teleportWithVehicle(Player player, Entity vehicle, Block portal) {
        BreachPortalManager.markEnter(player);

        List<Entity> passengers = new ArrayList<>(vehicle.getPassengers());
        passengers.forEach(vehicle::removePassenger);

        Location to = MirrorUtils.convertLocation(
                portal.getLocation(),
                UpsideDown.getInfo().originX()
        ).add(0.5, 0, 0.5);

        to.setYaw(MirrorUtils.mirrorYaw(player.getYaw()));
        to.setPitch(player.getPitch());

        vehicle.teleport(to, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.teleport(to, PlayerTeleportEvent.TeleportCause.PLUGIN);

        Bukkit.getScheduler().runTask(
                UpsideDown.getInstance(),
                () -> passengers.forEach(vehicle::addPassenger)
        );
    }
}
