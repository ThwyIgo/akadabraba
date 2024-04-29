package net.thwy.akadabraba;

import com.google.common.base.Predicates;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

// Check net.minecraft.item.EnderEyeItem#useOnBlock:57
public class ModStructures {
    public static final BlockPattern SMALL_ALTAR = BlockPatternBuilder.start()
            .aisle(
                    "ccccc",
                    "ccccc",
                    "ccccc",
                    "ccccc",
                    "ccccc"
            ).aisle(
                    "p???p",
                    "?????",
                    "??e??",
                    "?????",
                    "p???p"
            ).aisle(
                    "p???p",
                    "?????",
                    "?????",
                    "?????",
                    "p???p"
            ).aisle(
                    "p???p",
                    "?????",
                    "?????",
                    "?????",
                    "p???p"
            ).aisle(//    east
                    // south north
                    //    west
                    "svvvs",
                    ">ggg<",
                    ">ggg<",
                    ">ggg<",
                    "s^^^s"
            )
            .where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY))
            .where('c', is(Blocks.CHISELED_QUARTZ_BLOCK))
            .where('p', is(Blocks.QUARTZ_PILLAR).or(is(
                    ModBlocks.MAGICAL_QUARTZ_PILLAR)))
            .where('e', is(Blocks.ENCHANTING_TABLE))
            .where('s', is(Blocks.SMOOTH_QUARTZ_STAIRS))
            .where('v', is(Blocks.SMOOTH_QUARTZ_STAIRS, with(HorizontalFacingBlock.FACING, Predicates.equalTo(Direction.WEST))))
            .where('^', is(Blocks.SMOOTH_QUARTZ_STAIRS, with(HorizontalFacingBlock.FACING, Predicates.equalTo(Direction.EAST))))
            .where('>', is(Blocks.SMOOTH_QUARTZ_STAIRS, with(HorizontalFacingBlock.FACING, Predicates.equalTo(Direction.NORTH))))
            .where('<', is(Blocks.SMOOTH_QUARTZ_STAIRS, with(HorizontalFacingBlock.FACING, Predicates.equalTo(Direction.SOUTH))))
            .where('g', CachedBlockPosition.matchesBlockState(bs -> bs.isIn(ConventionalBlockTags.GLASS_BLOCKS)))
            .build();

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            if (!world.isClient &&
                    !player.shouldCancelInteraction() &&
                    world.getBlockState(pos).isOf(Blocks.ENCHANTING_TABLE)) {
                final BlockPattern.Result result = SMALL_ALTAR.searchAround(world, pos.down());
                if (result != null) {
                    BlockPos frontTopLeft = result.getFrontTopLeft();
                    BlockPos backBottomRight = frontTopLeft.add(-4, +4, -4);

                    final var commandManager = Objects.requireNonNull(world.getServer()).getCommandManager();
                    final var source = world.getServer().getCommandSource().withSilent();

                    // Replace blocks with their magical version
                    final String command = "fill "
                            + Stream.of(frontTopLeft.getX(), frontTopLeft.getY(), frontTopLeft.getZ(),
                                    backBottomRight.getX(), backBottomRight.getY(), backBottomRight.getZ())
                            .flatMapToInt(x -> (x + " ").codePoints())
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            + Registries.BLOCK.getId(ModBlocks.MAGICAL_QUARTZ_PILLAR)
                            + " replace "
                            + Registries.BLOCK.getId(Blocks.QUARTZ_PILLAR);

                    commandManager.executeWithPrefix(source, command);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }

    @SafeVarargs
    private static Predicate<CachedBlockPosition> is(Block block, Function<BlockStatePredicate, BlockStatePredicate>... withs) {
        BlockStatePredicate predicate = BlockStatePredicate.forBlock(block);

        for (var w : withs)
            w.apply(predicate);

        return CachedBlockPosition.matchesBlockState(predicate);
    }

    private static <T extends Comparable<T>> Function<BlockStatePredicate, BlockStatePredicate>
    with(Property<T> property, Predicate<Object> predicate) {
        return blockStatePredicate -> blockStatePredicate.with(property, predicate);
    }
}
