package fr.iambibi.upsidedown.generation.mirror;

import fr.iambibi.upsidedown.utils.CoordinatesUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Painting;

import java.util.Arrays;

public class MirrorPainting {
    /**
     * Mirrors a Painting along the X axis and spawns it in the target world
     *
     * @param source      The source Painting to mirror
     * @param targetWorld The target World to spawn the mirrored Painting in
     * @param originX     The X origin for mirroring
     */
    public static void mirrorAndSpawn(Painting source, World targetWorld, int originX) {
        Art art = source.getArt();
        BlockFace srcFacing = source.getFacing();
        BlockFace mirroredFacing = MirrorBlockData.mirrorBlockFaceX(srcFacing);

        int width = art.getBlockWidth();
        int height = art.getBlockHeight();

        Location sourceLoc = source.getLocation();
        Block srcAnchor = calculatePaintingAnchor(sourceLoc, srcFacing, width, height);

        int[] mirrored = CoordinatesUtils.convertCoordinates(
                srcAnchor.getX(),
                srcAnchor.getY(),
                srcAnchor.getZ(),
                originX
        );

        Block targetAnchor = targetWorld.getBlockAt(
                mirrored[0],
                mirrored[1],
                mirrored[2]
        );

        Location spawnLoc = calculateCenterFromAnchor(
                targetAnchor.getLocation(),
                mirroredFacing,
                width,
                height
        );

        targetWorld.spawn(spawnLoc, Painting.class, p -> {
            p.setFacingDirection(mirroredFacing, true);
            p.setArt(art, true);
        });
    }

    /**
     * Calculates the anchor block of a painting based on its center location, facing direction, width, and height.
     *
     * @param center The center location of the painting
     * @param facing The facing direction of the painting
     * @param width  The width of the painting in blocks
     * @param height The height of the painting in blocks
     * @return The anchor block of the painting
     */
    private static Block calculatePaintingAnchor(Location center, BlockFace facing, int width, int height) {
        int cx = center.getBlockX();
        double cy = center.getY();
        int cz = center.getBlockZ();

        int anchorX = cx;
        int anchorY = (int) (cy - (height / 2.0)) - 1;
        int anchorZ = cz;

        switch (facing) {
            case NORTH:
                anchorX += (width - 1) / 2;
                anchorZ += 1;
                break;
            case SOUTH:
                anchorX -= (width - 1) / 2;
                anchorZ -= 1;
                break;
            case WEST:
                anchorX -= (width - 1) - 1;
                anchorZ += (width - 1) - 3;
                break;
            case EAST:
                anchorX += (width - 1) - 1;
                anchorZ += (width - 1) - 2;
                break;
        }

        return center.getWorld().getBlockAt(anchorX, anchorY, anchorZ);
    }

    /**
     * Calculates the center location of a painting based on its anchor location, facing direction, width, and height.
     *
     * @param anchor The anchor location of the painting
     * @param facing The facing direction of the painting
     * @param width  The width of the painting in blocks
     * @param height The height of the painting in blocks
     * @return The center location of the painting
     */
    private static Location calculateCenterFromAnchor(Location anchor, BlockFace facing, int width, int height) {
        int ax = anchor.getBlockX();
        int ay = anchor.getBlockY();
        int az = anchor.getBlockZ();

        // bancal mais ça "marche"
        double cx = ax;
        double cy = ay + (height / 2.0);
        double cz = az;

        switch (facing) {
            case NORTH:
                cx += (width - 1) / 2.0;
                cz -= 1;
                break;
            case SOUTH:
                cx -= (width - 1) / 2.0;
                cz += 1;
                break;
            case WEST:
                cx += (width - 1) - 1;
                cz -= (width - 1) - 1;
                break;
            case EAST:
                cx -= (width - 1) - 1;
                cz -= (width - 1) - 3;
                break;
        }

        return new Location(
                anchor.getWorld(),
                cx + 0.5,
                cy + 0.5,
                cz + 0.5
        );
    }
}