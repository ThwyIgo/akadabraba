package net.thwy.akadabraba;

import com.google.common.base.Predicates;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityPose;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

// Check net.minecraft.item.EnderEyeItem#useOnBlock:57
public class ModStructures {
    public static final BlockPattern SMALL_ALTAR = BlockPatternBuilder.start()
            .aisle(
                    "ccccc",
                    "ccccc",
                    "ccccc",
                    "ccccc",
                    "ccccc")
            .aisle(
                    "p???p",
                    "?????",
                    "??e??",
                    "?????",
                    "p???p"
            )
            .aisle(
                    "p???p",
                    "?????",
                    "?????",
                    "?????",
                    "p???p"
            )
            .aisle(
                    "p???p",
                    "?????",
                    "?????",
                    "?????",
                    "p???p"
            )
            .aisle( //    east
                    // south north
                    //    west
                    "svvvs",
                    ">ggg<",
                    ">ggg<",
                    ">ggg<",
                    "s^^^s"
            )
            .where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY))
            .where('c', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(
                    Blocks.CHISELED_QUARTZ_BLOCK)))
            .where('p', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(
                    Blocks.QUARTZ_PILLAR).or(BlockStatePredicate.forBlock(
                    ModBlocks.MAGICAL_QUARTZ_PILLAR))))
            .where('e', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(
                    Blocks.ENCHANTING_TABLE)))
            .where('s', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(
                    Blocks.SMOOTH_QUARTZ_STAIRS)))
            .where('v', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(
                    Blocks.SMOOTH_QUARTZ_STAIRS).with(HorizontalFacingBlock.FACING, Predicates.equalTo(Direction.WEST))))
            .where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(
                    Blocks.SMOOTH_QUARTZ_STAIRS).with(HorizontalFacingBlock.FACING, Predicates.equalTo(Direction.EAST))
            ))
            .where('>', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(
                    Blocks.SMOOTH_QUARTZ_STAIRS).with(HorizontalFacingBlock.FACING, Predicates.equalTo(Direction.NORTH))
            ))
            .where('<', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(
                    Blocks.SMOOTH_QUARTZ_STAIRS).with(HorizontalFacingBlock.FACING, Predicates.equalTo(Direction.SOUTH))
            ))
            .where('g', CachedBlockPosition.matchesBlockState(bs -> bs.isIn(ConventionalBlockTags.GLASS_BLOCKS)))
            .build();

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            // What if the player is holding shift but not crouching? i.g. swimming
            if (!player.getPose().equals(EntityPose.CROUCHING) &&
                    world.getBlockState(pos).isOf(Blocks.ENCHANTING_TABLE)) {
                BlockPattern.Result result = SMALL_ALTAR.searchAround(world, pos.down());
                if (result != null) {
                    Akadabraba.LOGGER.info("WORKED");
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
}
