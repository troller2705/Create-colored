package com.azasad.createcolored.content.models;

import com.azasad.createcolored.ColoredHelpers;
import com.azasad.createcolored.content.blockEntities.ColoredFluidTankBlockEntity;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankCTBehaviour;
import com.simibubi.create.content.fluids.tank.FluidTankModel;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.Arrays;
import java.util.function.Supplier;

public class ColoredFluidTankModel extends CTModel {

    public static ColoredFluidTankModel standard(BakedModel originalModel, DyeColor color) {
        return new ColoredFluidTankModel(originalModel,
                ColoredSpriteShifts.DYED_FLUID_TANK.get(color),
                ColoredSpriteShifts.DYED_FLUID_TANK_TOP.get(color),
                ColoredSpriteShifts.DYED_FLUID_TANK_INNER.get(color)
        );
    }

    private ColoredFluidTankModel(BakedModel originalModel, CTSpriteShiftEntry side, CTSpriteShiftEntry top,
                           CTSpriteShiftEntry inner) {
        super(originalModel, new FluidTankCTBehaviour(side, top, inner));
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        CullData cullData = new CullData();
        for (Direction d : Iterate.horizontalDirections)
            cullData.setCulled(d, ConnectivityHandler.isConnected(blockView, pos, pos.offset(d)));

        context.pushTransform(quad -> {
            Direction cullFace = quad.cullFace();
            if (cullFace != null && cullData.isCulled(cullFace)) {
                return false;
            }
            quad.cullFace(null);
            return true;
        });

        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);

        context.popTransform();
    }

    private static class CullData {
        boolean[] culledFaces;

        public CullData() {
            culledFaces = new boolean[4];
            Arrays.fill(culledFaces, false);
        }

        void setCulled(Direction face, boolean cull) {
            if (face.getAxis()
                    .isVertical())
                return;
            culledFaces[face.getHorizontal()] = cull;
        }

        boolean isCulled(Direction face) {
            if (face.getAxis()
                    .isVertical())
                return false;
            return culledFaces[face.getHorizontal()];
        }
    }
}
