package com.troller2705.createcolored.content.item;

import com.troller2705.createcolored.ColoredConnectivityHandler;
import com.troller2705.createcolored.content.block.ColoredFluidTankBlock;
import com.troller2705.createcolored.content.blockEntities.ColoredBlockEntities;
import com.troller2705.createcolored.content.blockEntities.ColoredFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.level.Level;

public class ColoredFluidTankItem extends FluidTankItem {
    public ColoredFluidTankItem(Block block, Settings properties) {
        super(block, properties);
    }

    //TODO: Refactor this, we are calling the same two times
    @Override
    public ActionResult place(ItemPlacementContext ctx) {
        IS_PLACING_NBT = FluidTankItem.checkPlacingNbt(ctx);
        ActionResult initialResult = super.place(ctx);
        IS_PLACING_NBT = false;
        if (!initialResult.isAccepted())
            return initialResult;
        tryMultiPlace(ctx);
        return initialResult;
    }

    //TODO: Refactor this too
    private void tryMultiPlace(ItemPlacementContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if (player == null)
            return;
        if (player.isSneaking())
            return;
        Direction face = ctx.getSide();
        if (!face.getAxis()
                .isVertical())
            return;
        ItemStack stack = ctx.getStack();
        Level world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockPos placedOnPos = pos.offset(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);

        //If we aren't placing in another colored tank, abort
        if (!ColoredFluidTankBlock.isTank(placedOnState))
            return;
        ColoredFluidTankBlockEntity tankAt = ColoredConnectivityHandler.partAt(
                ColoredBlockEntities.COLORED_FLUID_TANK_ENTITY.get(), world, placedOnPos
        );
        if (tankAt == null)
            return;
        FluidTankBlockEntity controllerBE = tankAt.getControllerBE();
        if (controllerBE == null)
            return;

        int width = controllerBE.getWidth();
        if (width == 1)
            return;

        int tanksToPlace = 0;
        BlockPos startPos = face == Direction.DOWN ? controllerBE.getPos()
                .down()
                : controllerBE.getPos()
                .up(controllerBE.getHeight());

        if (startPos.getY() != pos.getY())
            return;

        //For all connected blocks in the layer below or above
        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.add(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (ColoredFluidTankBlock.isTank(blockState))
                    continue;
                if (!blockState.isReplaceable())
                    return;
                tanksToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.add(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (ColoredFluidTankBlock.isTank(blockState))
                    continue;
                ItemPlacementContext context = ItemPlacementContext.offset(ctx, offsetPos, face);
//                player.getCustomData()
//                        .method_10556("SilenceTankSound", true);
                IS_PLACING_NBT = checkPlacingNbt(context);
                place(context);
                IS_PLACING_NBT = false;
//                player.getCustomData()
//                        .method_10551("SilenceTankSound");
            }
        }
    }
}
