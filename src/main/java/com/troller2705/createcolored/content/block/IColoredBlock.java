package com.troller2705.createcolored.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface IColoredBlock {
    DyeColor getColor();

    InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray);
}
