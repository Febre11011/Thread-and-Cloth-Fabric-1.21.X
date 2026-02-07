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
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class CottonBush extends PlantBlock implements Fertilizable {
    public static final IntProperty AGE = IntProperty.of("age", 0, 4);
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public CottonBush(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(AGE, 0)
                .with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
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

        // Pokud spodní blok není naše kytka, nelze hnojit
        if (!lowerState.isOf(this)) return false;
        return state.get(AGE) < 4;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int nextAge = Math.min(4, state.get(AGE) + 1); // Předpokládám MAX_AGE = 3
        DoubleBlockHalf half = state.get(HALF);

        // 1. Najdeme pozici druhé poloviny
        BlockPos otherHalfPos = (half == DoubleBlockHalf.LOWER) ? pos.up() : pos.down();
        BlockState otherHalfState = world.getBlockState(otherHalfPos);

        // 2. Aplikujeme růst na aktuální blok
        world.setBlockState(pos, state.with(AGE, nextAge), Block.NOTIFY_LISTENERS);

        // 3. Aplikujeme růst na druhou polovinu (pokud existuje a je to stejný typ keře)
        if (otherHalfState.isOf(this)) {
            world.setBlockState(otherHalfPos, otherHalfState.with(AGE, nextAge), Block.NOTIFY_LISTENERS);
        }
        // Speciální případ: Pokud keř právě vyrostl do výšky (přechod z 1 bloku na 2)
        else if (half == DoubleBlockHalf.LOWER && nextAge == 2 && world.isAir(otherHalfPos)) {
            world.setBlockState(otherHalfPos, this.getDefaultState()
                    .with(HALF, DoubleBlockHalf.UPPER)
                    .with(AGE, nextAge), Block.NOTIFY_LISTENERS);
        }
    }
}