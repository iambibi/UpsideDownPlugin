package fr.iambibi.upsidedown.generation.mirror;

import fr.iambibi.upsidedown.utils.MirrorUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;

public class MirrorItemFrame {

    /**
     * Mirrors an ItemFrame along the X axis and spawns it in the target world
     *
     * @param source      The source ItemFrame to mirror
     * @param targetWorld The target World to spawn the mirrored ItemFrame in
     * @param originX     The X origin for mirroring
     */
    public static void mirrorAndSpawn(ItemFrame source, World targetWorld, int originX) {
        Location loc = source.getLocation();
        Block attachedBlock =
                loc.getBlock().getRelative(source.getAttachedFace());

        int[] mirrored = MirrorUtils.convertCoordinates(
                attachedBlock.getX(),
                attachedBlock.getY(),
                attachedBlock.getZ(),
                originX
        );

        BlockFace mirroredFace =
                MirrorBlockData.mirrorBlockFaceX(source.getFacing());

        Block targetSupport = targetWorld.getBlockAt(
                mirrored[0],
                mirrored[1],
                mirrored[2]
        );

        Location spawnLoc = targetSupport.getRelative(mirroredFace)
                        .getLocation()
                        .add(0.5, 0.5, 0.5);

        targetWorld.spawn(spawnLoc, ItemFrame.class, itemFrame -> {
            itemFrame.setFacingDirection(mirroredFace, true);
            itemFrame.setItem(source.getItem(), false);
            itemFrame.setRotation(mirrorItemFrameRotation(source.getRotation()));
            itemFrame.setVisible(source.isVisible());
            itemFrame.setFixed(source.isFixed());
        });
    }

    /**
     * Mirrors a Rotation along the X axis (for item frames)
     * @param rotation The Rotation to mirror
     * @return The mirrored Rotation
     */
    public static Rotation mirrorItemFrameRotation(Rotation rotation) {
        return switch (rotation) {
            case NONE -> Rotation.FLIPPED;
            case FLIPPED -> Rotation.NONE;

            case CLOCKWISE_45 -> Rotation.FLIPPED_45;
            case FLIPPED_45 -> Rotation.CLOCKWISE_45;

            case CLOCKWISE -> Rotation.COUNTER_CLOCKWISE;
            case COUNTER_CLOCKWISE -> Rotation.CLOCKWISE;

            case CLOCKWISE_135 -> Rotation.COUNTER_CLOCKWISE_45;
            case COUNTER_CLOCKWISE_45 -> Rotation.CLOCKWISE_135;
        };
    }
}