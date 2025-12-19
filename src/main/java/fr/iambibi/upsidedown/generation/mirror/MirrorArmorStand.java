package fr.iambibi.upsidedown.generation.mirror;

import fr.iambibi.upsidedown.utils.CoordinatesUtils;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;

public class MirrorArmorStand {

    /**
     * Mirrors an ArmorStand along the X axis and spawns it in the target world
     *
     * @param source      The source ArmorStand to mirror
     * @param targetWorld The target World to spawn the mirrored ArmorStand in
     * @param originX     The X origin for mirroring
     */
    public static void mirrorAndSpawn(ArmorStand source, World targetWorld, int originX) {
        Location mirroredLoc = CoordinatesUtils.convertLocation(source.getLocation(), originX);

        targetWorld.spawn(mirroredLoc, ArmorStand.class, stand -> {
            stand.setCanPickupItems(false);
            stand.setInvulnerable(true);
            stand.setGravity(source.hasGravity());
            stand.setVisible(source.isVisible());

            stand.setArms(source.hasArms());
            stand.setBasePlate(source.hasBasePlate());
            stand.setSmall(source.isSmall());

            stand.getEquipment().setHelmet(source.getEquipment().getHelmet());
            stand.getEquipment().setChestplate(source.getEquipment().getChestplate());
            stand.getEquipment().setLeggings(source.getEquipment().getLeggings());
            stand.getEquipment().setBoots(source.getEquipment().getBoots());
            stand.getEquipment().setItemInMainHand(source.getEquipment().getItemInMainHand());
            stand.getEquipment().setItemInOffHand(source.getEquipment().getItemInOffHand());

            stand.setPersistent(true);
            stand.setSilent(true);
        });
    }
}