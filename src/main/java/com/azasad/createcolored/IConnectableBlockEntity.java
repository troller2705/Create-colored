package com.azasad.createcolored;

import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;


public interface IConnectableBlockEntity extends IMultiBlockEntityContainer {
    boolean canConnectWith(BlockPos pos, BlockView level);
}
