package com.troller2705.createcolored.content.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankRenderer;
import com.troller2705.createcolored.content.block.IColoredBlock;
import com.troller2705.createcolored.content.blockEntities.ColoredFluidTankBlockEntity;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredFluidTankRenderer extends FluidTankRenderer
{
    public ColoredFluidTankRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    protected void renderAsBoiler(FluidTankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                                  int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
        ms.pushPose();
        var msr = TransformStack.of(ms);
        msr.translate(be.getWidth() / 2f, 0.5, be.getWidth() / 2f);

        float dialPivotY = 6f / 16;
        float dialPivotZ = 8f / 16;
        float progress = be.boiler.gauge.getValue(partialTicks);

        // Fallback values - should never actually be used
        var boilerGauge = CachedBuffers.partial(AllPartialModels.BOILER_GAUGE, blockState);
        var boilerGaugeDial = CachedBuffers.partial(AllPartialModels.BOILER_GAUGE_DIAL, blockState);

        if(blockState.getBlock() instanceof IColoredBlock coloredBlock){
            DyeColor color = coloredBlock.getColor();

            boilerGauge = CachedBuffers.partial(ColoredPartials.COLORED_BOILER_GAUGE.get(color), blockState);
            boilerGaugeDial = CachedBuffers.partial(ColoredPartials.COLORED_BOILER_GAUGE_DIAL.get(color), blockState);
        }

        for (Direction d : Iterate.horizontalDirections) {
            if (be.boiler.occludedDirections[d.get2DDataValue()])
                continue;
            ms.pushPose();
            float yRot = -d.toYRot() - 90;

            boilerGauge
                    .rotateYDegrees(yRot)
                    .uncenter()
                    .translate(be.getWidth() / 2f - 6 / 16f, 0, 0)
                    .light(light)
                    .renderInto(ms, vb);
            boilerGaugeDial
                    .rotateYDegrees(yRot)
                    .uncenter()
                    .translate(be.getWidth() / 2f - 6 / 16f, 0, 0)
                    .translate(0, dialPivotY, dialPivotZ)
                    .rotateXDegrees(-145 * progress + 90)
                    .translate(0, -dialPivotY, -dialPivotZ)
                    .light(light)
                    .renderInto(ms, vb);
            ms.popPose();
        }

        ms.popPose();
    }

}
