package com.azasad.createcolored.content.block;

import com.azasad.createcolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ColoredEncasedPipeBlock extends EncasedPipeBlock {
    private final DyeColor color;

    public ColoredEncasedPipeBlock(Settings properties, Supplier<Block> casing, DyeColor color) {
        super(properties, casing);
        this.color = color;
    }

    @Override
    public ItemStack getPickedStack(BlockState state, BlockView view, BlockPos pos, @Nullable PlayerEntity player, @Nullable HitResult result) {
        return ColoredBlocks.DYED_PIPES.get(color).asStack();
    }

    @Override
    public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        if (world.isClient)
            return ActionResult.SUCCESS;

        context.getWorld()
                .syncWorldEvent(2001, context.getBlockPos(), Block.getRawIdFromState(state));
        BlockState equivalentPipe = transferSixWayProperties(state, ColoredBlocks.DYED_PIPES.get(color).getDefaultState());

        Direction firstFound = Direction.UP;
        for (Direction d : Iterate.directions)
            if (state.get(FACING_TO_PROPERTY_MAP.get(d))) {
                firstFound = d;
                break;
            }

        FluidTransportBehaviour.cacheFlows(world, pos);
        world.setBlockState(pos, ColoredBlocks.DYED_PIPES.get(color).get()
                .updateBlockState(equivalentPipe, firstFound, null, world, pos));
        FluidTransportBehaviour.loadFlows(world, pos);
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return ColoredBlockEntities.COLORED_ENCASED_FLUID_PIPE.get();
    }
}
