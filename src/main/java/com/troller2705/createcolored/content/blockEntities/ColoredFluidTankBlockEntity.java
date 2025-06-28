package com.troller2705.createcolored.content.blockEntities;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.troller2705.createcolored.ColoredConnectivityHandler;
import com.troller2705.createcolored.IConnectableBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.troller2705.createcolored.content.block.ColoredFluidTankBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class ColoredFluidTankBlockEntity extends FluidTankBlockEntity implements IHaveGoggleInformation, IConnectableBlockEntity {
    public static BlockEntityType<ColoredFluidTankBlockEntity> TYPE;

    public ColoredFluidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide())
            return;
        if (!isController())
            return;
        ColoredConnectivityHandler.formMulti(this);
    }

    @Override
    public boolean canConnectWith(BlockPos otherPos, Level level) {
        BlockEntity be = level.getBlockEntity(otherPos);
        if (be instanceof ColoredFluidTankBlockEntity) {
            return be.getBlockState().getBlock().equals(this.getBlockState().getBlock());
        }
        return false;
    }


    public Level getWorld() {
        return level;
    }


    public BlockPos getPos() { return getBlockPos(); }


    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ColoredBlockEntities.COLORED_FLUID_TANK_ENTITY.get(),
                (be, context) -> {
                    if (be.fluidCapability == null)
                        be.refreshCapability();
                    return be.fluidCapability;
                }
        );
    }

    void refreshCapability() {
        fluidCapability = handlerForCapability();
        invalidateCapabilities();
    }

    private IFluidHandler handlerForCapability() {
        if(isController()){
            return boiler.isActive() ? boiler.createHandler() : tankInventory;
        }else{
            return getControllerBE() != null ? ((ColoredFluidTankBlockEntity)getControllerBE()).handlerForCapability() : new FluidTank(0);
        }
    }

}
