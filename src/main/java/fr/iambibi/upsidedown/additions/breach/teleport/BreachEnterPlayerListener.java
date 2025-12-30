package fr.iambibi.upsidedown.additions.breach.teleport;

import fr.iambibi.upsidedown.additions.breach.BreachManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

public class BreachEnterPlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGateway(EntityPortalEnterEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (player.isInsideVehicle()) return;
        if (e.getPortalType() != PortalType.END_GATEWAY) return;

        Block portal = e.getLocation().getBlock();
        if (!BreachManager.isInUpsideDownPortal(portal)) return;

        if (!BreachPortalManager.canEnter(player)) return;

        e.setCancelled(true);
        BreachPortalManager.markEnter(player);

        BreachTeleportUtils.teleport(player, portal);
    }
}