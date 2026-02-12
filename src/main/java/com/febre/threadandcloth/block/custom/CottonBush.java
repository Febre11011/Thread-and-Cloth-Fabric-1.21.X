package com.febre.threadandcloth.block.custom;

import com.febre.threadandcloth.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class CottonBush extends TallPlantBlock implements Fertilizable {
    public static final IntProperty AGE = IntProperty.of("age", 0, 4);
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 7.0, 11.0);
    private static final VoxelShape MEDIUM_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 15.0, 13.0);
    private static final VoxelShape LARGE_SHAPE_LOWER = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    private static final VoxelShape LARGE_SHAPE_UPPER = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 8.0, 15.0);

    public CottonBush(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(AGE, 0)
                .with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public MapCodec<? extends TallPlantBlock> getCodec() {
        return createCodec(CottonBush::new);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE, HALF);
    }

    private boolean isTall(int age) {
        return age >= 2;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int age = state.get(AGE);
        DoubleBlockHalf half = state.get(HALF);

        // 1. Handle the UPPER block first
        if (half == DoubleBlockHalf.UPPER) {
            // If age is 2, 3, or 4, it uses the upper part of the model.
            // Based on your shapes, let's assume it only has an upper hitbox at these stages.
            if (age >= 2) {
                return LARGE_SHAPE_UPPER;
            }
            // If it's the upper half but the age is too low, it should have NO hitbox
            return VoxelShapes.empty();
        }

        // 2. Handle the LOWER block
        return switch (age) {
            case 0 -> SMALL_SHAPE;
            case 1 -> MEDIUM_SHAPE;
            default -> LARGE_SHAPE_LOWER; // Age 2, 3, and 4
        };
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) return;

        int age = state.get(AGE);
        if (age < 4 && world.getBaseLightLevel(pos.up(), 0) >= 9 && random.nextInt(5) == 0) {
            grow(world, random, pos, state);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        int age = state.get(AGE);
        BlockPos lowerPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        BlockState lowerState = world.getBlockState(lowerPos);

        if (age == 4) {
            int count = 4 + world.random.nextInt(2);
            dropStack(world, lowerPos, new ItemStack(ModItems.COTTON, count));

            BlockState newState = lowerState.with(AGE, 2);
            world.setBlockState(lowerPos, newState, Block.NOTIFY_LISTENERS);

            if (world.getBlockState(lowerPos.up()).isOf(this)) {
                world.setBlockState(lowerPos.up(), newState.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_LISTENERS);
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.get(HALF);

        if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            if (isTall(state.get(AGE))) {
                if (!neighborState.isOf(this) || neighborState.get(HALF) == half) {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = world.getBlockState(pos.down());
            return below.isOf(this) && below.get(HALF) == DoubleBlockHalf.LOWER;
        }
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        BlockPos lowerPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        BlockState lowerState = world.getBlockState(lowerPos);

        if (!lowerState.isOf(this)) return false;
        return state.get(AGE) < 4;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int nextAge = Math.min(4, state.get(AGE) + 1);
        DoubleBlockHalf half = state.get(HALF);

        BlockPos otherHalfPos = (half == DoubleBlockHalf.LOWER) ? pos.up() : pos.down();
        BlockState otherHalfState = world.getBlockState(otherHalfPos);

        world.setBlockState(pos, state.with(AGE, nextAge), Block.NOTIFY_LISTENERS);

        if (otherHalfState.isOf(this)) {
            world.setBlockState(otherHalfPos, otherHalfState.with(AGE, nextAge), Block.NOTIFY_LISTENERS);
        }
        else if (half == DoubleBlockHalf.LOWER && nextAge == 2 && world.isAir(otherHalfPos)) {
            world.setBlockState(otherHalfPos, this.getDefaultState()
                    .with(HALF, DoubleBlockHalf.UPPER)
                    .with(AGE, nextAge), Block.NOTIFY_LISTENERS);
        }
    }
}