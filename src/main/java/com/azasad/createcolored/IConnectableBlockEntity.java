package com.azasad.createcolored;

import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;


public interface IConnectableBlockEntity extends IMultiBlockEntityContainer {
    boolean canConnectWith(BlockPos pos, BlockEntityType<?> type, BlockView level);
}
