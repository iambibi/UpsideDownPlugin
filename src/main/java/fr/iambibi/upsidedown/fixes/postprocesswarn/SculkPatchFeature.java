package fr.iambibi.upsidedown.fixes.postprocesswarn;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;

//todo: remove this if https://mojira.dev/MC-249656 gets fixed
public class SculkPatchFeature {

    public static void place(LevelAccessor level, BlockPos origin, RandomSource random, SculkPatchConfiguration config) {
        placeSculkBase(level, origin, config);

        SculkSpreader spreader = SculkSpreader.createLevelSpreader();

        int totalRounds = config.spreadRounds + config.growthRounds;

        for (int round = 0; round < totalRounds; round++) {

            for (int c = 0; c < config.chargeCount; c++) {
                spreader.addCursors(origin, config.amountPerCharge);
            }

            boolean shouldConvert = round < config.spreadRounds;

            for (int i = 0; i < config.spreadAttempts; i++) {
                spreader.updateCursors(
                        level,
                        origin,
                        random,
                        shouldConvert
                );
            }

            spreader.clear();
        }

        BlockPos below = origin.below();
        if (random.nextFloat() < config.catalystChance
                && level.getBlockState(below)
                .isFaceSturdy(level, below, Direction.UP)) {

            level.setBlock(
                    origin,
                    Blocks.SCULK_CATALYST.defaultBlockState(),
                    Block.UPDATE_ALL
            );
        }

        int shriekers = Mth.nextInt(
                random,
                config.shriekerMin,
                config.shriekerMax
        );

        for (int i = 0; i < shriekers; i++) {
            BlockPos pos = origin.offset(
                    random.nextInt(config.shriekerRadius * 2 + 1) - config.shriekerRadius,
                    0,
                    random.nextInt(config.shriekerRadius * 2 + 1) - config.shriekerRadius
            );

            if (level.getBlockState(pos).isAir()
                    && level.getBlockState(pos.below())
                    .isFaceSturdy(level, pos.below(), Direction.UP)) {

                level.setBlock(
                        pos,
                        Blocks.SCULK_SHRIEKER
                                .defaultBlockState()
                                .setValue(SculkShriekerBlock.CAN_SUMMON, true),
                        Block.UPDATE_ALL
                );
            }
        }

        SculkVeinBlock vein = (SculkVeinBlock) Blocks.SCULK_VEIN;

        for (int i = 0; i < config.extraVeinAttempts; i++) {
            BlockPos pos = origin.offset(
                    random.nextInt(config.veinRadius * 2 + 1) - config.veinRadius,
                    random.nextInt(3) - 1,
                    random.nextInt(config.veinRadius * 2 + 1) - config.veinRadius
            );

            vein.getSpreader().spreadAll(
                    level.getBlockState(pos),
                    level,
                    pos,
                    false
            );
        }
    }

    private static void placeSculkBase(LevelAccessor level, BlockPos origin, SculkPatchConfiguration config) {
        for (int x = -config.seedRadius; x <= config.seedRadius; x++) {
            for (int z = -config.seedRadius; z <= config.seedRadius; z++) {

                BlockPos pos = origin.offset(x, 0, z);
                BlockPos below = pos.below();

                if (level.getBlockState(below).is(BlockTags.SCULK_REPLACEABLE)) {
                    level.setBlock(
                            below,
                            Blocks.SCULK.defaultBlockState(),
                            Block.UPDATE_ALL
                    );
                }
            }
        }
    }
}
