package com.troller2705.createcolored.content.block;

import com.troller2705.createcolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.*;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.data.Iterate;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.Arrays;

import static com.simibubi.create.content.fluids.pipes.FluidPipeBlockRotation.FACING_TO_PROPERTY_MAP;

public class ColoredFluidPipeBlock extends FluidPipeBlock implements IColoredBlock {
    protected final DyeColor color;

    public ColoredFluidPipeBlock(Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    public static BlockState updateConnections(BlockGetter world, BlockPos pos, BlockState state, @Nullable Direction ignored) {
        BlockState newState = state;
        for (Direction d : Iterate.directions) {
            if (d == ignored)
                continue;
            boolean shouldConnect = canConnectToColored(world, pos, state, d);
            newState = newState.setValue(FACING_TO_PROPERTY_MAP.get(d), shouldConnect);
        }
        return newState;
    }

    public static boolean canConnectToColored(BlockGetter world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos neighbourPos = pos.offset(direction.getNormal());
        BlockState neighbourState = world.getBlockState(neighbourPos);

        //if it is a colored tank, and is not the same color as the pipe, don't connect
        if(ColoredFluidTankBlock.isColoredTank(neighbourState)){
            ColoredFluidPipeBlock block = (ColoredFluidPipeBlock) state.getBlock();
            ColoredFluidTankBlock other = (ColoredFluidTankBlock) neighbourState.getBlock();
            if(block.color != other.getColor())
                return false;
        }

        //Has fluid capability
        if (FluidPropagator.hasFluidCapability(world, neighbourPos, direction.getOpposite()))
            return true;
        //Is a vanilla fluid target
        if (VanillaFluidTargets.canProvideFluidWithoutCapability(neighbourState))
            return true;

        //is any type of pipe
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(world, neighbourPos, BracketedBlockEntityBehaviour.TYPE);
        if (isPipe(neighbourState)) {
            //if is a colored pipe and not of the same color, then we don't connect
            if (isColoredPipe(neighbourState) && !sameColor(state, neighbourState)) {
                return false;
            }

            //else just check for brackets and such
            return bracket == null || !bracket.isBracketPresent() ||
                    FluidPropagator.getStraightPipeAxis(neighbourState) == direction.getAxis();
        }

        //if it is a glass pipe and is not of the same color, we don't connect
        if (neighbourState.getBlock() instanceof ColoredGlassFluidPipeBlock glassPipe) {
            ColoredFluidPipeBlock current = (ColoredFluidPipeBlock) state.getBlock();
            if (!current.getColor().equals(glassPipe.getColor()))
                return false;
        }

        FluidTransportBehaviour transport = BlockEntityBehaviour.get(world, neighbourPos, FluidTransportBehaviour.TYPE);
        //If it can transport fluid, connect else we don't connect;
        if (transport == null)
            return false;
        return transport.canHaveFlowToward(neighbourState, direction.getOpposite());
    }

    public static BlockState addOppositeSide(BlockState state, Direction dir) {
        return state.setValue(FACING_TO_PROPERTY_MAP.get(dir), true).setValue(FACING_TO_PROPERTY_MAP.get(dir.getOpposite()), true);
    }

    public static boolean sameColor(BlockState state, BlockState neighbour) {
        return state.getBlock().equals(neighbour.getBlock());
    }

    public static boolean isColoredPipe(BlockState state) {
        return state.getBlock() instanceof ColoredFluidPipeBlock;
    }

    public static boolean isColoredGlassPipe(BlockState state) {
        return state.getBlock() instanceof ColoredGlassFluidPipeBlock;
    }

    public static boolean shouldDrawCasing(BlockState state) {
        if (!isColoredPipe(state))
            return false;
        for (Direction.Axis axis : Iterate.axes) {
            int connections = 0;
            for (Direction direction : Iterate.directions)
                if (direction.getAxis() != axis && isOpenAt(state, direction))
                    connections++;
            if (connections > 2)
                return true;
        }
        return false;
    }

    public static boolean shouldDrawRim(BlockGetter world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos offsetPos = pos.offset(direction.getNormal());
        BlockState facingState = world.getBlockState(offsetPos);
        if (facingState.getBlock() instanceof EncasedPipeBlock)
            return true;
        if (!canConnectToColored(world, pos, state, direction))
            return true;
        return !isColoredPipe(facingState);
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (tryRemoveBracket(context))
            return InteractionResult.SUCCESS;
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Direction.Axis axis = getAxis(state);
        if (axis == null) {
            Vec3 clickLocation = context.getClickLocation();
            double closest = Float.MAX_VALUE;
            Direction argClosest = Direction.UP;
            for (Direction direction : Iterate.directions) {
                if (clickedFace.getAxis() == direction.getAxis())
                    continue;
                Vec3 centerOf = Vec3.atCenterOf(pos.relative(direction));
                double distance = centerOf.distanceToSqr(clickLocation);
                if (distance < closest) {
                    closest = distance;
                    argClosest = direction;
                }
            }
            axis = argClosest.getAxis();
        }
        if (clickedFace.getAxis() == axis)
            return InteractionResult.PASS;
        if (!world.isClientSide()) {
            withBlockEntityDo(world, pos, fpte -> fpte.getBehaviour(FluidTransportBehaviour.TYPE).interfaces.values().stream().filter(pc -> pc != null && pc.hasFlow()).findAny().ifPresent($ -> AllAdvancements.GLASS_PIPE.awardTo(context.getPlayer())));
            FluidTransportBehaviour.cacheFlows(world, pos);
            world.setBlockAndUpdate(pos, ColoredBlocks.DYED_GLASS_PIPES.get(color).getDefaultState().setValue(GlassFluidPipeBlock.AXIS, axis).setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED)));
            FluidTransportBehaviour.loadFlows(world, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    private Direction.Axis getAxis(BlockState state) {
        return FluidPropagator.getStraightPipeAxis(state);
    }

    @Override
    public InteractionResult  onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);
        DyeColor color = getColorFromStack(heldItem);
        if (color != null) {
            if (!world.isClientSide())
                world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.1f - world.random.nextFloat() * .2f);
            applyDye(state, world, pos, color);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    private static DyeColor getColorFromStack(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof DyeItem dyeItem) {
            return dyeItem.getDyeColor();
        }
        return null;
    }

    public void applyDye(BlockState state, Level world, BlockPos pos, @Nullable DyeColor color) {
        BlockState newState =
                (color == null ? ColoredBlocks.DYED_PIPES.get(DyeColor.WHITE) : ColoredBlocks.DYED_PIPES.get(color)).getDefaultState();

        //Update newState block state
        newState = updateBlockState(newState, Direction.UP, null, world, pos);

        //Dye the block itself
        if (state != newState) {
            world.setBlockAndUpdate(pos, newState);
        }
    }

    public BlockState updateBlockState(BlockState state, Direction preferredDirection, @Nullable Direction ignore, BlockGetter world, BlockPos pos) {
        // Do nothing if we are bracketed
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);
        if (bracket != null && bracket.isBracketPresent())
            return state;

        // get and store initial state
        BlockState prevState = state;
        int prevStateSides = (int) Arrays.stream(Iterate.directions).map(FACING_TO_PROPERTY_MAP::get).filter(prevState::getValue).count();

        //Update pipe connections
        state = updateConnections(world, pos, state, ignore);

        // see if it has enough connections
        Direction connectedDirection = null;
        for (Direction d : Iterate.directions) {
            if (isOpenAt(state, d)) {
                if (connectedDirection != null) {
                    return state;
                }
                connectedDirection = d;
            }
        }

        // //add opposite end if only one connection
        if (connectedDirection != null) {
            return state.setValue(FACING_TO_PROPERTY_MAP.get(connectedDirection.getOpposite()), true);
        }
        // return pipe facing at the opposite of the direction of the previous
        // state
        if (prevStateSides == 2) {
            Direction foundDir = null;
            for (Direction d : Iterate.directions) {
                if (prevState.getValue(FACING_TO_PROPERTY_MAP.get(d))) {
                    foundDir = d;
                    break;
                }
            }
            if (foundDir != null)
                return addOppositeSide(state, foundDir);
        }
        return addOppositeSide(state, preferredDirection);
    }

    @Override
    public BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return ColoredBlockEntities.COLORED_FLUID_PIPE_ENTITY.get();
    }
}
