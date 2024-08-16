package com.azasad.createcolored.content.block;

import com.azasad.createcolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.simibubi.create.foundation.utility.Iterate;
import io.github.fabricators_of_create.porting_lib.util.TagUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.annotation.MethodsReturnNonnullByDefault;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ColoredGlassFluidPipeBlock extends GlassFluidPipeBlock {
    protected final DyeColor color;

    public ColoredGlassFluidPipeBlock(Settings properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    public boolean tryRemoveBracket(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Optional<ItemStack> bracket = removeBracket(world, pos, false);
        BlockState blockState = world.getBlockState(pos);
        if (bracket.isPresent()) {
            PlayerEntity player = context.getPlayer();
            if (!world.isClient && !player.isCreative())
                player.getInventory().offerOrDrop(bracket.get());
            if (!world.isClient && ColoredBlocks.DYED_PIPES.get(color).has(blockState)) {
                Direction.Axis preferred = FluidPropagator.getStraightPipeAxis(blockState);
                Direction preferredDirection =
                        preferred == null ? Direction.UP : Direction.get(Direction.AxisDirection.POSITIVE, preferred);
                BlockState updated = ColoredBlocks.DYED_PIPES.get(color).get()
                        .updateBlockState(blockState, preferredDirection, null, world, pos);
                if (updated != blockState)
                    world.setBlockState(pos, updated);
            }
            return true;
        }
        return false;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                              BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);
        DyeColor color = TagUtil.getColorFromStack(heldItem);
        if (color != null) {
            if (!world.isClient)
                world.playSound(null, pos, SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0f, 1.1f - world.random.nextFloat() * .2f);
            applyDye(state, world, pos, hit.getPos(), color);
            return ActionResult.SUCCESS;
        }

        if (!AllBlocks.COPPER_CASING.isIn(player.getStackInHand(hand)))
            return ActionResult.PASS;
        if (world.isClient)
            return ActionResult.SUCCESS;
        BlockState newState = ColoredBlocks.DYED_ENCASED_PIPES.get(this.color).get().getDefaultState();
        for (Direction d : Iterate.directionsInAxis(getAxis(state)))
            newState = newState.with(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(d), true);
        FluidTransportBehaviour.cacheFlows(world, pos);
        world.setBlockState(pos, newState);
        FluidTransportBehaviour.loadFlows(world, pos);
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
        if (tryRemoveBracket(context))
            return ActionResult.SUCCESS;
        BlockState newState;
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        FluidTransportBehaviour.cacheFlows(world, pos);
        newState = toColoredPipe(world, pos, state).
                with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED));

        world.setBlockState(pos, newState, 3);
        FluidTransportBehaviour.loadFlows(world, pos);
        return ActionResult.SUCCESS;
    }

    public BlockState toColoredPipe(WorldAccess world, BlockPos pos, BlockState state) {
        Direction side = Direction.get(Direction.AxisDirection.POSITIVE, state.get(AXIS));
        Map<Direction, BooleanProperty> facingToPropertyMap = FluidPipeBlock.FACING_PROPERTIES;
        return ColoredBlocks.DYED_PIPES.get(color).get()
                .updateBlockState(ColoredBlocks.DYED_PIPES.get(color).getDefaultState()
                                .with(facingToPropertyMap.get(side), true)
                                .with(facingToPropertyMap.get(side.getOpposite()), true),
                        side, null, world, pos);
    }

    public void applyDye(BlockState state, World world, BlockPos pos, Vec3d hit, @Nullable DyeColor color) {
        BlockState newState =
                (color == null ? ColoredBlocks.DYED_GLASS_PIPES.get(DyeColor.WHITE) : ColoredBlocks.DYED_GLASS_PIPES.get(color)).getDefaultState();

        //Dye the block itself
        if (state != newState) {
            world.setBlockState(pos, newState);
        }

        //TODO: Dye adjacent
    }

    @Override
    public BlockEntityType<? extends StraightPipeBlockEntity> getBlockEntityType() {
        return ColoredBlockEntities.COLORED_GLASS_FLUID_PIPE_ENTITY.get();
    }

    @Override
    public ItemStack getPickedStack(BlockState state, BlockView view, BlockPos pos, @Nullable PlayerEntity player, @Nullable HitResult result) {
        return ColoredBlocks.DYED_PIPES.get(color).asStack();
    }
}
