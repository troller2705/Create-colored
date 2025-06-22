package com.troller2705.createcolored.content.blockEntities;

import com.troller2705.createcolored.CreateColored;
import com.troller2705.createcolored.content.block.ColoredBlocks;
import com.simibubi.create.content.fluids.pipes.TransparentStraightPipeRenderer;
import com.simibubi.create.content.fluids.tank.FluidTankRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ColoredBlockEntities {

    public static final BlockEntityEntry<ColoredFluidPipeBlockEntity> COLORED_FLUID_PIPE_ENTITY = CreateColored.REGISTRATE
            .blockEntity("colored_fluid_pipe", ColoredFluidPipeBlockEntity::new)
            .validBlocks(ColoredBlocks.DYED_PIPES.toArray())
            .register();

    public static final BlockEntityEntry<ColoredFluidTankBlockEntity> COLORED_FLUID_TANK_ENTITY = CreateColored.REGISTRATE
            .blockEntity("fluid_tank", ColoredFluidTankBlockEntity::new)
            .validBlocks(ColoredBlocks.DYED_FLUID_TANKS.toArray())
            .renderer(() -> FluidTankRenderer::new)
            .register();

    public static final BlockEntityEntry<ColoredFluidPipeBlockEntity> COLORED_ENCASED_FLUID_PIPE = CreateColored.REGISTRATE
            .blockEntity("colored_encased_fluid_pipe", ColoredFluidPipeBlockEntity::new)
            .validBlocks(ColoredBlocks.DYED_ENCASED_PIPES.toArray())
            .register();

    public static final BlockEntityEntry<ColoredGlassFluidPipeBlockEntity> COLORED_GLASS_FLUID_PIPE_ENTITY = CreateColored.REGISTRATE
            .blockEntity("colored_glass_fluid_pipe", ColoredGlassFluidPipeBlockEntity::new)
            .validBlocks(ColoredBlocks.DYED_GLASS_PIPES.toArray())
            .renderer(() -> TransparentStraightPipeRenderer::new)
            .register();

    public static void initialize() {

    }
}
