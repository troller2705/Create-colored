package com.troller2705.createcolored.content.block;

import com.troller2705.createcolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ColoredEncasedPipeBlock extends EncasedPipeBlock implements IColoredBlock {
    private final DyeColor color;

    public ColoredEncasedPipeBlock(Properties properties, Supplier<Block> casing, DyeColor color) {
        super(properties, casing);
        this.color = color;
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult result, BlockGetter level, BlockPos pos, Player player) {
        return ColoredBlocks.DYED_PIPES.get(color).asStack();
    }


    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (world.isClientSide())
            return InteractionResult.SUCCESS;

        world.levelEvent(2001, pos, Block.getId(state)); // Play break particles
        BlockState equivalentPipe = transferSixWayProperties(state, ColoredBlocks.DYED_PIPES.get(color).get().defaultBlockState());

        Direction firstFound = Direction.UP;
        for (Direction d : Iterate.directions)
            if (state.getValue(FACING_TO_PROPERTY_MAP.get(d))) {
                firstFound = d;
                break;
            }

        FluidTransportBehaviour.cacheFlows(world, pos);
        world.setBlock(pos, ColoredBlocks.DYED_PIPES.get(color).get()
                .updateBlockState(equivalentPipe, firstFound, null, world, pos), 3);
        FluidTransportBehaviour.loadFlows(world, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return ColoredBlockEntities.COLORED_ENCASED_FLUID_PIPE.get();
    }


    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        return InteractionResult.PASS;
    }
}
