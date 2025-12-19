package fr.iambibi.upsidedown.generation.mirror;

import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.Chest;

import java.util.Set;

public class MirrorBlockData {

    /**
     * Mirrors BlockData along the X axis
     *
     * @param data The BlockData to mirror
     * @return The mirrored BlockData
     */
    public static BlockData mirrorBlockData(BlockData data) {

        if (data instanceof Chest chest) {
            chest.setFacing(mirrorBlockFaceX(chest.getFacing()));

            if (chest.getType() == Chest.Type.LEFT) {
                chest.setType(Chest.Type.RIGHT);
            } else if (chest.getType() == Chest.Type.RIGHT) {
                chest.setType(Chest.Type.LEFT);
            }

            return chest;
        }

        // stairs, pistons, observers, etc.
        if (data instanceof Directional directional) {
            directional.setFacing(mirrorBlockFaceX(directional.getFacing()));
            return directional;
        }

        // signs, skulls
        if (data instanceof Rotatable rotatable) {
            rotatable.setRotation(mirrorBlockFaceX(rotatable.getRotation()));
            return rotatable;
        }

        // rails
        if (data instanceof Rail rail) {
            rail.setShape(mirrorRailShapeX(rail.getShape()));
            return rail;
        }

        // fences, walls, panes, vines, ladders
        if (data instanceof MultipleFacing multi) {
            Set<BlockFace> original = Set.copyOf(multi.getFaces());

            for (BlockFace face : multi.getAllowedFaces()) {
                multi.setFace(face, false);
            }

            for (BlockFace face : original) {
                multi.setFace(mirrorBlockFaceX(face), true);
            }
            return multi;
        }

        return data;
    }

    /**
     * Mirrors a BlockFace along the X axis
     * @param face The BlockFace to mirror
     * @return The mirrored BlockFace
     */
    public static BlockFace mirrorBlockFaceX(BlockFace face) {
        return switch (face) {
            case EAST -> BlockFace.WEST;
            case WEST -> BlockFace.EAST;
            default -> face;
        };
    }

    /**
     * Mirrors a Rail.Shape along the X axis
     * @param shape The Rail.Shape to mirror
     * @return The mirrored Rail.Shape
     */
    private static Rail.Shape mirrorRailShapeX(Rail.Shape shape) {
        return switch (shape) {

            case EAST_WEST -> Rail.Shape.EAST_WEST;
            case NORTH_SOUTH -> Rail.Shape.NORTH_SOUTH;

            case ASCENDING_EAST -> Rail.Shape.ASCENDING_WEST;
            case ASCENDING_WEST -> Rail.Shape.ASCENDING_EAST;

            case ASCENDING_NORTH -> Rail.Shape.ASCENDING_NORTH;
            case ASCENDING_SOUTH -> Rail.Shape.ASCENDING_SOUTH;

            case NORTH_EAST -> Rail.Shape.NORTH_WEST;
            case NORTH_WEST -> Rail.Shape.NORTH_EAST;
            case SOUTH_EAST -> Rail.Shape.SOUTH_WEST;
            case SOUTH_WEST -> Rail.Shape.SOUTH_EAST;
        };
    }
}