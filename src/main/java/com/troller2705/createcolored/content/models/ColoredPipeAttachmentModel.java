package com.troller2705.createcolored.content.models;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import com.troller2705.createcolored.content.block.ColoredFluidPipeBlock;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ColoredPipeAttachmentModel extends BakedModelWrapperWithData {

    private static final ModelProperty<ColoredPipeModelData> PIPE_PROPERTY = new ModelProperty<>();
    private final DyeColor color;
    private final boolean ao;

    public static ColoredPipeAttachmentModel withAO(BakedModel template, DyeColor color){
        return new ColoredPipeAttachmentModel(template, color, true);
    }

    public static ColoredPipeAttachmentModel withoutAO(BakedModel template, DyeColor color){
        return new ColoredPipeAttachmentModel(template, color, false);
    }

    public ColoredPipeAttachmentModel(BakedModel template, DyeColor color, boolean ao) {
        super(template);
        this.color = color;
        this.ao = ao;
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {

        ColoredPipeModelData data = new ColoredPipeModelData();
        FluidTransportBehaviour transport = BlockEntityBehaviour.get(world, pos, FluidTransportBehaviour.TYPE);
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);

        if(transport != null){
            for (Direction d : Iterate.directions){
                data.putAttachment(d, transport.getRenderedRimAttachment(world, pos, state, d));
            }
        }
        if(bracket != null){
            data.putBracket(bracket.getBracket());
        }

        data.setEncased(ColoredFluidPipeBlock.shouldDrawCasing(world, pos, state));
        return builder.with(PIPE_PROPERTY, data);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        List<ChunkRenderTypeSet> set = new ArrayList<>();

        set.add(super.getRenderTypes(state, rand, data));
        set.add(AllPartialModels.FLUID_PIPE_CASING.get().getRenderTypes(state, rand, data));

        if (data.has(PIPE_PROPERTY)) {
            ColoredPipeModelData pipeData = data.get(PIPE_PROPERTY);
            for (Direction d : Iterate.directions) {
                FluidTransportBehaviour.AttachmentTypes type = pipeData.getAttachment(d);
                for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials partial : type.partials) {
                    ChunkRenderTypeSet attachmentRenderTypeSet = ColoredPartials.COLORED_PIPE_ATTACHMENTS
                            .get(partial)
                            .get(color)
                            .get(d.getName())
                            .get()
                            .getRenderTypes(state, rand, data);
                    set.add(attachmentRenderTypeSet);
                }
            }
        }

        return ChunkRenderTypeSet.union(set);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, data, renderType);
        if (data.has(PIPE_PROPERTY)) {
            ColoredPipeModelData pipeData = data.get(PIPE_PROPERTY);
            quads = new ArrayList<>(quads);
            addQuads(quads, state, side, rand, data, pipeData, renderType);
        }
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return ao;
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        if(ao){
            return TriState.TRUE;
        }else{
            return TriState.FALSE;
        }
    }

    private void addQuads(List<BakedQuad> quads, BlockState state, Direction side, RandomSource rand, ModelData data, ColoredPipeModelData pipeData, RenderType renderType) {
        BakedModel bracket = pipeData.getBracket();
        if (bracket != null)
            quads.addAll(bracket.getQuads(state, side, rand, data, renderType));
        for (Direction d : Iterate.directions) {
            FluidTransportBehaviour.AttachmentTypes type = pipeData.getAttachment(d);
            for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials partial : type.partials) {
                quads.addAll(ColoredPartials.COLORED_PIPE_ATTACHMENTS
                        .get(partial)
                        .get(color)
                        .get(d.getName())
                        .get()
                        .getQuads(state, side, rand, data, renderType));
            }
        }
        if (pipeData.isEncased())
            quads.addAll(ColoredPartials.COLORED_FLUID_PIPE_CASINGS
                    .get(color)
                    .get()
                    .getQuads(state, side, rand, data, renderType));
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
                this.bracket = Minecraft.getInstance()
                        .getBlockRenderer()
                        .getBlockModel(state);
            }
        }

        public BakedModel getBracket() {
            return this.bracket;
        }

        public void putAttachment(Direction face, FluidTransportBehaviour.AttachmentTypes rim) {
            attachments[face.get3DDataValue()] = rim;
        }

        public FluidTransportBehaviour.AttachmentTypes getAttachment(Direction face) {
            return attachments[face.get3DDataValue()];
        }

        public boolean isEncased() {
            return this.encased;
        }

        public void setEncased(Boolean encased) {
            this.encased = encased;
        }
    }
}
