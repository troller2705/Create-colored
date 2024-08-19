package com.azasad.createcolored.content.models;

import com.azasad.createcolored.content.block.ColoredFluidPipeBlock;
import com.azasad.createcolored.content.block.IColoredBlock;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.Arrays;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class ColoredPipeAttachmentModel extends ForwardingBakedModel {

    public ColoredPipeAttachmentModel(BakedModel template) {
        wrapped = template;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView world, BlockState state, BlockPos pos,
                               Supplier<Random> randomSupplier, RenderContext context) {
        ColoredPipeModelData data = new ColoredPipeModelData();

        //Populate attachment list
        RenderAttachedBlockView attachmentView = (RenderAttachedBlockView) world;
        Object attachment = attachmentView.getBlockEntityRenderAttachment(pos);
        if (attachment instanceof FluidTransportBehaviour.AttachmentTypes[] attachments) {
            for (int i = 0; i < attachments.length; i++) {
                data.putAttachment(Iterate.directions[i], attachments[i]);
            }
        }

        // bracket logic
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(world, pos,
                BracketedBlockEntityBehaviour.TYPE);
        if (bracket != null) {
            data.putBracket(bracket.getBracket());
        }

        data.setEncased(ColoredFluidPipeBlock.shouldDrawCasing(state));

        //Get color
        if(state.getBlock() instanceof IColoredBlock pipe) {
            data.setColor(pipe.getColor());
        }

        super.emitBlockQuads(world, state, pos, randomSupplier, context);
        addQuads(world, state, pos, randomSupplier, context, data);
    }

    private void addQuads(BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context,
                          ColoredPipeModelData pipeData) {

        for (Direction d : Iterate.directions) {
            FluidTransportBehaviour.AttachmentTypes type = pipeData.getAttachment(d);
            for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials partial : type.partials) {
                ColoredPartials.COLORED_PIPE_ATTACHMENTS.get(partial)
                        .get(pipeData.getColor())
                        .get(d.asString())
                        .get()
                        .emitBlockQuads(world, state, pos, randomSupplier, context);
            }
        }

        if (pipeData.isEncased())
            ColoredPartials.COLORED_FLUID_PIPE_CASINGS.get(pipeData.getColor()).get()
                    .emitBlockQuads(world, state, pos, randomSupplier, context);
        BakedModel bracket = pipeData.getBracket();
        if (bracket != null) {
            bracket.emitBlockQuads(world, state, pos, randomSupplier, context);
        }
    }

    private static class ColoredPipeModelData {
        private final FluidTransportBehaviour.AttachmentTypes[] attachments;
        private DyeColor color;
        private boolean encased;
        private BakedModel bracket;

        public ColoredPipeModelData() {
            attachments = new FluidTransportBehaviour.AttachmentTypes[6];
            Arrays.fill(attachments, FluidTransportBehaviour.AttachmentTypes.NONE);
        }

        public void putBracket(BlockState state) {
            if (state != null) {
                this.bracket = MinecraftClient.getInstance()
                        .getBlockRenderManager()
                        .getModel(state);
            }
        }

        public DyeColor getColor() { return this.color; }
        public void setColor(DyeColor color) { this.color = color; }

        public BakedModel getBracket() {
            return this.bracket;
        }

        public void putAttachment(Direction face, FluidTransportBehaviour.AttachmentTypes rim) {
            attachments[face.getId()] = rim;
        }

        public FluidTransportBehaviour.AttachmentTypes getAttachment(Direction face) {
            return attachments[face.getId()];
        }

        public boolean isEncased() {
            return this.encased;
        }

        public void setEncased(Boolean encased) {
            this.encased = encased;
        }
    }
}
