package com.troller2705.createcolored.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@SuppressWarnings("ALL")
@Mixin(SteamEngineBlockEntity.class)
public class SteamEngineBlockEntityMixin extends BlockEntity {

    public SteamEngineBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    /**
     * @author ThermalCube
     * @reason Allow all colored tanks to run steam engines
     */
    @Overwrite
    public boolean isValid(){
        Direction dir = SteamEngineBlock.getConnectedDirection(((BlockEntity)(Object)this).getBlockState()).getOpposite();

        Level level = ((BlockEntity)(Object)this).getLevel();
        if (level == null)
            return false;

        var be = level.getBlockEntity(((BlockEntity)(Object)this).getBlockPos().relative(dir));
        return be instanceof FluidTankBlockEntity;
    }

}
