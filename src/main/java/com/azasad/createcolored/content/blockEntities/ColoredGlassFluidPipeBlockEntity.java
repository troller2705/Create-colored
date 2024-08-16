package com.azasad.createcolored.content.blockEntities;

import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class ColoredGlassFluidPipeBlockEntity extends StraightPipeBlockEntity {

    public ColoredGlassFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}