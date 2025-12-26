package fr.iambibi.upsidedown.generation.mirror;

import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.Campfire;
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

        if (data instanceof Campfire campfire) {
            campfire.setSignalFire(false);

            return campfire;
        }

        // stairs, pistons, observers, etc.
        if (data instanceof Directional directional) {
            directional.setFacing(mirrorBlockFaceX(directional.getFacing()));
            return directional;
        }

        // signs, skulls
        if (data instanceof Rotatable rotatable) {

            BlockFace face = rotatable.getRotation();

            if (face == BlockFace.SELF) {
                return rotatable;
            }

            int rotation = faceToRotation(face);
            int mirrored = (16 - rotation) % 16;

            rotatable.setRotation(rotationToFace(mirrored));
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

    private static int faceToRotation(BlockFace face) {
        return switch (face) {
            case SOUTH -> 0;
            case SOUTH_SOUTH_WEST -> 1;
            case SOUTH_WEST -> 2;
            case WEST_SOUTH_WEST -> 3;
            case WEST -> 4;
            case WEST_NORTH_WEST -> 5;
            case NORTH_WEST -> 6;
            case NORTH_NORTH_WEST -> 7;
            case NORTH -> 8;
            case NORTH_NORTH_EAST -> 9;
            case NORTH_EAST -> 10;
            case EAST_NORTH_EAST -> 11;
            case EAST -> 12;
            case EAST_SOUTH_EAST -> 13;
            case SOUTH_EAST -> 14;
            case SOUTH_SOUTH_EAST -> 15;
            default -> 0;
        };
    }

    private static BlockFace rotationToFace(int rotation) {
        return switch (rotation & 15) {
            case 0 -> BlockFace.SOUTH;
            case 1 -> BlockFace.SOUTH_SOUTH_WEST;
            case 2 -> BlockFace.SOUTH_WEST;
            case 3 -> BlockFace.WEST_SOUTH_WEST;
            case 4 -> BlockFace.WEST;
            case 5 -> BlockFace.WEST_NORTH_WEST;
            case 6 -> BlockFace.NORTH_WEST;
            case 7 -> BlockFace.NORTH_NORTH_WEST;
            case 8 -> BlockFace.NORTH;
            case 9 -> BlockFace.NORTH_NORTH_EAST;
            case 10 -> BlockFace.NORTH_EAST;
            case 11 -> BlockFace.EAST_NORTH_EAST;
            case 12 -> BlockFace.EAST;
            case 13 -> BlockFace.EAST_SOUTH_EAST;
            case 14 -> BlockFace.SOUTH_EAST;
            case 15 -> BlockFace.SOUTH_SOUTH_EAST;
            default -> BlockFace.SOUTH;
        };
    }
}