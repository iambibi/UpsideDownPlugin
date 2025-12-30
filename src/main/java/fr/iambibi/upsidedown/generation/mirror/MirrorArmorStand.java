package fr.iambibi.upsidedown.generation.mirror;

import fr.iambibi.upsidedown.utils.MirrorUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

public class MirrorArmorStand {

    /**
     * Mirrors an ArmorStand along the X axis and spawns it in the target world
     *
     * @param source      The source ArmorStand to mirror
     * @param targetWorld The target World to spawn the mirrored ArmorStand in
     * @param originX     The X origin for mirroring
     */
    public static void mirrorAndSpawn(ArmorStand source, World targetWorld, int originX) {
        Location mirroredLoc = MirrorUtils.convertLocation(source.getLocation(), originX);

        mirroredLoc.setX(mirroredLoc.getX()+1);

        float yaw = mirroredLoc.getYaw();
        float mirroredYaw = -yaw;

        if (mirroredYaw > 180) mirroredYaw -= 360;
        if (mirroredYaw < -180) mirroredYaw += 360;

        mirroredLoc.setYaw(mirroredYaw);

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