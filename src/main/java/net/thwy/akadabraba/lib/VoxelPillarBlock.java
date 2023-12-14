package net.thwy.akadabraba.lib;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class VoxelPillarBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty UP = ConnectingBlock.UP;
    public static final BooleanProperty DOWN = ConnectingBlock.DOWN;

    public VoxelPillarBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(UP, false).with(DOWN, false));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());

        // Is setting UP and DOWN needed here? getStateForNeighborUpdate() should already take care of it
        final World worldView = ctx.getWorld();
        final BlockPos blockPos = ctx.getBlockPos();
        final Block blockBelow = worldView.getBlockState(blockPos.down()).getBlock();
        final Block blockAbove = worldView.getBlockState(blockPos.up()).getBlock();

        state = state.with(DOWN, blockBelow instanceof VoxelPillarBlock);
        state = state.with(UP, blockAbove instanceof VoxelPillarBlock);

        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.equals(Direction.UP))
            state = state.with(UP, neighborState.getBlock() instanceof VoxelPillarBlock);
        if (direction.equals(Direction.DOWN))
            state = state.with(DOWN, neighborState.getBlock() instanceof VoxelPillarBlock);

        return state;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return super.rotate(state, rotation);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, UP, DOWN);
    }
}
