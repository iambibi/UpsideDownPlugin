package fr.iambibi.upsidedown.additions.breach.teleport;

import fr.iambibi.upsidedown.additions.breach.BreachManager;
import org.bukkit.PortalType;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

public class BreachEnterVehicleListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGateway(EntityPortalEnterEvent e) {

        if (e.getPortalType() != PortalType.END_GATEWAY) return;

        Entity vehicle = e.getEntity();
        if (vehicle.getPassengers().isEmpty()) return;

        Player player = vehicle.getPassengers().stream()
                .filter(p -> p instanceof Player)
                .map(p -> (Player) p)
                .findFirst()
                .orElse(null);

        if (player == null) return;

        Block portal = e.getLocation().getBlock();
        if (!BreachManager.isInUpsideDownPortal(portal)) return;

        if (!BreachPortalManager.canEnter(player)) return;

        e.setCancelled(true);
        BreachPortalManager.markEnter(player);

        BreachTeleportUtils.teleportWithVehicle(player, vehicle, portal);
    }
}
