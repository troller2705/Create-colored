package com.troller2705.createcolored.content.block;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.troller2705.createcolored.content.blockEntities.ColoredBlockEntities;
import com.troller2705.createcolored.content.blockEntities.ColoredFluidTankBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.DyeColor;

import org.jetbrains.annotations.Nullable;

public class ColoredFluidTankBlock extends FluidTankBlock {

    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    private final DyeColor color;

    public ColoredFluidTankBlock(DyeColor color) {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
                .noOcclusion(), false);
        this.color = color;
        registerDefaultState(this.defaultBlockState().setValue(COLOR, color));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(COLOR);
    }

    public DyeColor getColor() {
        return color;
    }

    public static boolean isColoredTank(BlockState state) {
        return state.getBlock() instanceof ColoredFluidTankBlock;
    }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return ColoredBlockEntities.COLORED_FLUID_TANK_ENTITY.get();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide ? null :
                (type == ColoredFluidTankBlockEntity.TYPE
                        ? (lvl, pos, st, be) -> ((ColoredFluidTankBlockEntity) be).tick()
                        : null);
    }
}
