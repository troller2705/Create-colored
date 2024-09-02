package com.azasad.createcolored.content.blockEntities;

import com.azasad.createcolored.ColoredConnectivityHandler;
import com.azasad.createcolored.IConnectableBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class ColoredFluidTankBlockEntity extends FluidTankBlockEntity implements IConnectableBlockEntity {
    public ColoredFluidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void updateConnectivity() {
        updateConnectivity = false;
        if (world.isClient)
            return;
        if (!isController())
            return;
        ColoredConnectivityHandler.formMulti(this);
    }

    @Override
    public boolean canConnectWith(BlockPos otherPos, BlockView level) {
        BlockEntity be = level.getBlockEntity(otherPos);
        if (be instanceof ColoredFluidTankBlockEntity) {
            return be.getCachedState().getBlock().equals(this.getCachedState().getBlock());
        }
        return false;
    }
}
