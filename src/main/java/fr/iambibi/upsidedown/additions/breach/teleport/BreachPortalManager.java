package fr.iambibi.upsidedown.additions.breach.teleport;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.additions.breach.BreachManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

public final class BreachPortalManager {

    private static final Set<UUID> insideBreach = new HashSet<>();
    private static final Set<UUID> cooldownBreach = new HashSet<>();

    public static void start() {
        Bukkit.getScheduler().runTaskTimer(
                UpsideDown.getInstance(),
                () -> {
                    Iterator<UUID> it = insideBreach.iterator();

                    while (it.hasNext()) {
                        UUID uuid = it.next();
                        Player player = Bukkit.getPlayer(uuid);

                        if (player == null) {
                            it.remove();
                            cooldownBreach.remove(uuid);
                            continue;
                        }

                        if (!isStillInBreach(player)) {
                            it.remove();

                            Bukkit.getScheduler().runTaskLater(
                                    UpsideDown.getInstance(),
                                    () -> cooldownBreach.remove(uuid),
                                    10L
                            );
                        }
                    }
                },
                0L,
                10L
        );
    }

    public static boolean canEnter(Player player) {
        return !cooldownBreach.contains(player.getUniqueId());
    }

    public static void markEnter(Player player) {
        UUID uuid = player.getUniqueId();
        insideBreach.add(uuid);
        cooldownBreach.add(uuid);
    }

    private static boolean isStillInBreach(Player player) {
        Entity ref = player.isInsideVehicle()
                ? player.getVehicle()
                : player;

        if (ref == null) return false;

        BoundingBox box = ref.getBoundingBox();

        for (Block block : getNearbyGatewayBlocks(ref.getLocation(), 1)) {
            if (BreachManager.isInUpsideDownPortal(block)
                    && block.getBoundingBox().overlaps(box)) {
                return true;
            }
        }
        return false;
    }

    private static List<Block> getNearbyGatewayBlocks(Location center, int radius) {
        List<Block> blocks = new ArrayList<>();

        World world = center.getWorld();
        if (world == null) return blocks;

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int y = cy - radius; y <= cy + radius; y++) {
                for (int z = cz - radius; z <= cz + radius; z++) {

                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.END_GATEWAY) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }
}
