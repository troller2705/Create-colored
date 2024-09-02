package com.azasad.createcolored.content.block;

import com.azasad.createcolored.ColoredConnectivityHandler;
import com.azasad.createcolored.content.blockEntities.ColoredBlockEntities;
import com.azasad.createcolored.content.blockEntities.ColoredFluidTankBlockEntity;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ColoredFluidTankBlock extends FluidTankBlock {
    protected final DyeColor color;
    protected ColoredFluidTankBlock(Settings properties, DyeColor color) {
        super(properties, false);
        this.color = color;
    }

    public static boolean isColoredTank(BlockState state) {
        return (state.getBlock() instanceof ColoredFluidTankBlock);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof FluidTankBlockEntity))
                return;
            ColoredFluidTankBlockEntity tankBE = (ColoredFluidTankBlockEntity) be;
            world.removeBlockEntity(pos);
            ColoredConnectivityHandler.splitMulti(tankBE); //Problem lies here
        }
    }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return ColoredBlockEntities.COLORED_FLUID_TANK_ENTITY.get();
    }
}
