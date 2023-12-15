package net.thwy.akadabraba;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.thwy.akadabraba.lib.VerticalConnectingBlock;

import java.util.stream.Stream;

public class MagicalPillarBlock extends VerticalConnectingBlock {
    private static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(0, 0, 0, 16, 2, 16),
            Block.createCuboidShape(0, 14, 0, 16, 16, 16),
            Block.createCuboidShape(2, 2, 2, 14, 14, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE_UP = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 0, 16, 2, 16),
            Block.createCuboidShape(2, 2, 2, 14, 16, 14),
            BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_DOWN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(2, 0, 2, 14, 14, 14),
            Block.createCuboidShape(0, 14, 0, 16, 16, 16),
            BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_BOTH = Block.createCuboidShape(2, 0, 2, 14, 16, 14);

    public MagicalPillarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final boolean up = state.get(UP), down = state.get(DOWN);

        if (!(up || down))
            return SHAPE;
        if (up && down)
            return SHAPE_BOTH;
        if (up)
            return SHAPE_UP;
        else
            return SHAPE_DOWN;
    }
}
