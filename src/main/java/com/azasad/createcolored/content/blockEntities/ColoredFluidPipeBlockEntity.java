package com.azasad.createcolored.content.blockEntities;

import com.azasad.createcolored.content.block.ColoredFluidPipeBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import java.util.List;

public class ColoredFluidPipeBlockEntity extends FluidPipeBlockEntity {
    public ColoredFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new StandardPipeFluidTransportBehaviour(this));
        behaviours.add(new BracketedBlockEntityBehaviour(this, this::canHaveBracket));
        registerAwardables(behaviours, FluidPropagator.getSharedTriggers());
    }

    private boolean canHaveBracket(BlockState state) {
        return !(state.getBlock() instanceof EncasedPipeBlock);
    }

    static class StandardPipeFluidTransportBehaviour extends FluidTransportBehaviour {
        public StandardPipeFluidTransportBehaviour(SmartBlockEntity be) {
            super(be);
        }

        @Override
        public boolean canHaveFlowToward(BlockState state, Direction direction) {
            return (ColoredFluidPipeBlock.isColoredPipe(state) || state.getBlock() instanceof EncasedPipeBlock)
                    && state.get(ColoredFluidPipeBlock.FACING_PROPERTIES.get(direction));
        }

        @Override
        public AttachmentTypes getRenderedRimAttachment(BlockRenderView world, BlockPos pos, BlockState state,
                                                        Direction direction) {
            AttachmentTypes attachment = super.getRenderedRimAttachment(world, pos, state, direction);

            BlockPos offsetPos = pos.offset(direction);
            BlockState otherState = world.getBlockState(offsetPos);

            if(state.getBlock() instanceof EncasedPipeBlock && attachment != AttachmentTypes.DRAIN)
                return AttachmentTypes.NONE;

            if(attachment == AttachmentTypes.RIM && !ColoredFluidPipeBlock.isColoredPipe(otherState) &&
                    !AllBlocks.MECHANICAL_PUMP.has(otherState) && !AllBlocks.ENCASED_FLUID_PIPE.has(otherState)) {
                FluidTransportBehaviour pipeBehaviour = BlockEntityBehaviour.get(world, offsetPos, FluidTransportBehaviour.TYPE);
                if(pipeBehaviour != null)
                    if(pipeBehaviour.canHaveFlowToward(otherState, direction.getOpposite()))
                        return AttachmentTypes.CONNECTION;
            }

            if(attachment == AttachmentTypes.RIM && !ColoredFluidPipeBlock.shouldDrawRim(world, pos, state, direction))
                return AttachmentTypes.CONNECTION;
            if(attachment == AttachmentTypes.NONE && state.get(ColoredFluidPipeBlock.FACING_PROPERTIES.get(direction)))
                return AttachmentTypes.CONNECTION;

            return attachment;
        }
    }
}
