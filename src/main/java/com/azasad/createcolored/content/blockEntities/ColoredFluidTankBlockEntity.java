package com.azasad.createcolored.content.blockEntities;

import com.azasad.createcolored.ColoredConnectivityHandler;
import com.azasad.createcolored.CreateColored;
import com.azasad.createcolored.IConnectableBlockEntity;
import com.azasad.createcolored.content.block.ColoredFluidTankBlock;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.DyeColor;
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
    public boolean canConnectWith(BlockPos pos, BlockEntityType<?> type, BlockView level) {
        BlockEntity be = level.getBlockEntity(pos);
        CreateColored.LOGGER.info("Testing BE at: " + this.getPos().toString() + " Against: " + be.getPos().toString() );
        return false;
    }
}
