package fr.iambibi.upsidedown.additions.breach;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.utils.MirrorUtils;
import fr.iambibi.upsidedown.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.EndGateway;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static fr.iambibi.upsidedown.additions.breach.BreachManager.BREACH_KEY;

public class BreachGenerator {

    private final Random random;
    private static final BlockFace[] ALL_FACES = {
            BlockFace.UP, BlockFace.DOWN,
            BlockFace.NORTH, BlockFace.SOUTH,
            BlockFace.EAST, BlockFace.WEST
    };

    public BreachGenerator() {
        this.random = new Random();
    }

    public void createBreach(Player player) {
        Block targetBlock = player.getTargetBlock(null, 10);
        if (targetBlock.getType() == Material.AIR) return;

        BlockFace face = getBlockFace(player);
        Block startBlock = targetBlock.getRelative(face);

        int targetSize = 5 + random.nextInt(11);
        int maxWidth = 1 + random.nextInt(4);

        generateOrganicBreach(startBlock, face.getOppositeFace(), targetSize, maxWidth);
    }

    public void createBreachAt(Location location, BlockFace surfaceFace) {
        Block block = location.getBlock();
        int targetSize = 5 + random.nextInt(11);
        int maxWidth = 1 + random.nextInt(4);
        generateOrganicBreach(block, surfaceFace, targetSize, maxWidth);
    }

    public void createBreachAt(Location location, BlockFace surfaceFace, int minSize, int maxSize, int minWidth, int maxWidth) {
        Block block = location.getBlock();
        int targetSize = minSize + random.nextInt(maxSize - minSize + 1);
        int width = minWidth + random.nextInt(maxWidth - minWidth + 1);
        generateOrganicBreach(block, surfaceFace, targetSize, width);
    }

    public void createBreachAtExact(Location location, BlockFace surfaceFace, int exactSize, int exactWidth) {
        Block block = location.getBlock();
        generateOrganicBreach(block, surfaceFace, exactSize, exactWidth);
    }

    private void generateOrganicBreach(Block start, BlockFace surfaceFace, int targetSize, int maxWidth) {
        Set<Block> breachBlocks = new HashSet<>();
        Set<Block> edgeBlocks = new HashSet<>();
        Queue<BreachNode> queue = new LinkedList<>();

        queue.add(new BreachNode(start, 0, 0));
        breachBlocks.add(start);

        Vector mainDirection = getPerpendicularVector(surfaceFace);
        Vector widthDirection = getSecondPerpendicularVector(surfaceFace, mainDirection);

        while (!queue.isEmpty() && breachBlocks.size() < targetSize) {
            BreachNode node = queue.poll();
            Block current = node.block;

            for (BlockFace face : ALL_FACES) {
                Block adjacent = current.getRelative(face);

                if (breachBlocks.contains(adjacent)) continue;

                Block surface = adjacent.getRelative(surfaceFace);
                if (!surface.getType().isSolid()) continue;

                Vector toAdjacent = adjacent.getLocation().toVector()
                        .subtract(start.getLocation().toVector());

                double mainDist = Math.abs(toAdjacent.dot(mainDirection));
                double widthDist = Math.abs(toAdjacent.dot(widthDirection));

                if (widthDist > maxWidth) continue;

                double spreadChance = 0.85 - (breachBlocks.size() / (double) targetSize) * 0.3;

                if (toAdjacent.dot(mainDirection) > 0) {
                    spreadChance += 0.15;
                }

                if (random.nextDouble() < spreadChance) {
                    breachBlocks.add(adjacent);
                    queue.add(new BreachNode(adjacent, mainDist, widthDist));

                    if (random.nextDouble() < 0.3) {
                        edgeBlocks.add(adjacent);
                    }
                }
            }
        }

        for (Block block : breachBlocks) {
            if (edgeBlocks.contains(block) && random.nextDouble() < 0.6) {
                block.setType(Material.SCULK);
            } else {
                placeGatewayBlock(block);
            }
        }

        addOrganicEdge(breachBlocks, surfaceFace);
    }

    private void addOrganicEdge(Set<Block> breachBlocks, BlockFace surfaceFace) {
        Set<Block> edgeToAdd = new HashSet<>();

        for (Block block : breachBlocks) {
            for (BlockFace face : ALL_FACES) {
                Block adjacent = block.getRelative(face);

                if (!breachBlocks.contains(adjacent)) {
                    Block surface = adjacent.getRelative(surfaceFace);

                    if (surface.getType().isSolid() && random.nextDouble() < 0.4) {
                        edgeToAdd.add(adjacent);
                    }
                }
            }
        }

        for (Block block : edgeToAdd) {
            block.setType(Material.SCULK);
            Block mirroredBlock = MirrorUtils.convertBlock(block, UpsideDown.getInfo().originX());
            if (mirroredBlock == null) return;
            mirroredBlock.setType(Material.SCULK);
        }
    }

    private Vector getPerpendicularVector(BlockFace face) {
        switch (face) {
            case UP:
            case DOWN:
                return new Vector(1, 0, 0);
            case NORTH:
            case SOUTH:
                return new Vector(1, 0, 0);
            case EAST:
            case WEST:
                return new Vector(0, 0, 1);
            default:
                return new Vector(1, 0, 0);
        }
    }

    private Vector getSecondPerpendicularVector(BlockFace face, Vector first) {
        Vector normal = getFaceNormal(face);
        return first.getCrossProduct(normal).normalize();
    }

    private Vector getFaceNormal(BlockFace face) {
        switch (face) {
            case UP:
                return new Vector(0, 1, 0);
            case DOWN:
                return new Vector(0, -1, 0);
            case NORTH:
                return new Vector(0, 0, -1);
            case SOUTH:
                return new Vector(0, 0, 1);
            case EAST:
                return new Vector(1, 0, 0);
            case WEST:
                return new Vector(-1, 0, 0);
            default:
                return new Vector(0, 1, 0);
        }
    }

    private BlockFace getBlockFace(Player player) {
        Location playerLoc = player.getEyeLocation();
        Vector direction = playerLoc.getDirection().normalize();

        double dotX = Math.abs(direction.getX());
        double dotY = Math.abs(direction.getY());
        double dotZ = Math.abs(direction.getZ());

        if (dotY > dotX && dotY > dotZ) {
            return direction.getY() > 0 ? BlockFace.DOWN : BlockFace.UP;
        } else if (dotX > dotZ) {
            return direction.getX() > 0 ? BlockFace.WEST : BlockFace.EAST;
        } else {
            return direction.getZ() > 0 ? BlockFace.NORTH : BlockFace.SOUTH;
        }
    }

    private static void placeGatewayBlock(Block block) {
        String worldNameBreach = block.getWorld().getName();
        String invertedWorldNameBreach = WorldUtils.getInvertedWorldName(worldNameBreach);

        HashMap<String, Block> map = new HashMap<>();
        map.put(worldNameBreach, block);
        map.put(invertedWorldNameBreach, MirrorUtils.convertBlock(block, UpsideDown.getInfo().originX()));

        map.forEach((worldName, b) -> {
            b.setType(Material.END_GATEWAY, false);

            ServerLevel level = ((CraftWorld) b.getWorld()).getHandle();
            BlockPos pos = new BlockPos(b.getX(), b.getY(), b.getZ());
            TheEndGatewayBlockEntity be = (TheEndGatewayBlockEntity) level.getBlockEntity(pos);
            if (be == null) return;

            b.getChunk().load();
            be.persistentDataContainer.set(
                    BREACH_KEY,
                    PersistentDataType.BOOLEAN,
                    true
            );
            be.age = Integer.MIN_VALUE;
        });

        BreachManager.addBreach(map.get(worldNameBreach), map.get(invertedWorldNameBreach));
    }

    private static class BreachNode {
        Block block;
        double mainDistance;
        double widthDistance;

        BreachNode(Block block, double mainDistance, double widthDistance) {
            this.block = block;
            this.mainDistance = mainDistance;
            this.widthDistance = widthDistance;
        }
    }
}