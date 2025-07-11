package com.troller2705.createcolored.content.block;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.troller2705.createcolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ColoredGlassFluidPipeBlock extends GlassFluidPipeBlock implements IColoredBlock {
    protected final DyeColor color;

    public ColoredGlassFluidPipeBlock(Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public boolean tryRemoveBracket(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Optional<ItemStack> bracket = removeBracket(world, pos, false);
        BlockState blockState = world.getBlockState(pos);
        if (bracket.isPresent())
        {
            Player player = context.getPlayer();
            if (player != null && !world.isClientSide() && !player.isCreative())
            {
                ItemStack stackToGive = bracket.get();
                if (!player.getInventory().add(stackToGive))
                {
                    player.drop(stackToGive, false);
                }
            }
            if (!world.isClientSide() && ColoredBlocks.DYED_PIPES.get(color).has(blockState)) {
                Direction.Axis preferred = FluidPropagator.getStraightPipeAxis(blockState);
                Direction preferredDirection =
                        preferred == null ? Direction.UP : Direction.get(Direction.AxisDirection.POSITIVE, preferred);
                BlockState updated = ColoredBlocks.DYED_PIPES.get(color).get()
                        .updateBlockState(blockState, preferredDirection, null, world, pos);
                if (updated != blockState)
                    world.setBlockAndUpdate(pos, updated);
            }
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        DyeColor color = getColorFromStack(heldItem);
        if (color != null) {
            if (!world.isClientSide())
                world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.1f - world.random.nextFloat() * .2f);
            applyDye(state, world, pos, color);
            return InteractionResult.SUCCESS;
        }

        if (!AllBlocks.COPPER_CASING.isIn(player.getItemInHand(hand)))
            return InteractionResult.PASS;
        if (world.isClientSide())
            return InteractionResult.SUCCESS;
        BlockState newState = ColoredBlocks.DYED_ENCASED_PIPES.get(this.color).get().defaultBlockState();
        for (Direction d : Iterate.directionsInAxis(getAxis(state)))
            newState = newState.setValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(d), true);
        FluidTransportBehaviour.cacheFlows(world, pos);
        world.setBlock(pos, newState, Block.UPDATE_ALL);
        FluidTransportBehaviour.loadFlows(world, pos);
        return InteractionResult.SUCCESS;
    }

    @Nullable
    private static DyeColor getColorFromStack(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof DyeItem dyeItem) {
            return dyeItem.getDyeColor();
        }
        return null;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (tryRemoveBracket(context))
            return InteractionResult.SUCCESS;
        BlockState newState;
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidTransportBehaviour.cacheFlows(world, pos);
        newState = toColoredPipe(world, pos, state).setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));

        world.setBlock(pos, newState, Block.UPDATE_ALL);
        FluidTransportBehaviour.loadFlows(world, pos);
        return InteractionResult.SUCCESS;
    }

    public BlockState toColoredPipe(LevelAccessor world, BlockPos pos, BlockState state) {
        Direction side = Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(AXIS));
        Map<Direction, BooleanProperty> facingToPropertyMap = FluidPipeBlock.PROPERTY_BY_DIRECTION;
        return ColoredBlocks.DYED_PIPES.get(color).get()
                .updateBlockState(ColoredBlocks.DYED_PIPES.get(color).getDefaultState()
                        .setValue(facingToPropertyMap.get(side), true)
                        .setValue(facingToPropertyMap.get(side.getOpposite()), true), side, null, world, pos);
    }

    public void applyDye(BlockState state, Level world, BlockPos pos, @Nullable DyeColor color) {
        BlockState newState =
                (color == null ? ColoredBlocks.DYED_GLASS_PIPES.get(DyeColor.WHITE) : ColoredBlocks.DYED_GLASS_PIPES.get(color)).getDefaultState();

        //Dye the block itself
        if (state != newState) {
            world.setBlock(pos, newState, Block.UPDATE_ALL);
        }
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        return ItemRequirement.of(ColoredBlocks.DYED_PIPES.get(color).getDefaultState(), be);
    }

    @Override
    public BlockEntityType<? extends StraightPipeBlockEntity> getBlockEntityType() {
        return ColoredBlockEntities.COLORED_GLASS_FLUID_PIPE_ENTITY.get();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return ColoredBlocks.DYED_PIPES.get(color).asStack();
    }
}
