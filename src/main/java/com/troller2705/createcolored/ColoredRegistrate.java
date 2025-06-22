package com.troller2705.createcolored;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.DyeColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class ColoredRegistrate extends CreateRegistrate {
    
    protected ColoredRegistrate(String modid) {
        super(modid);
    }

    public static <T extends Block> NonNullConsumer<? super T> coloredBlockModel(Supplier<NonNullBiFunction<BakedModel, DyeColor, ? extends BakedModel>> func, DyeColor color) {
        return entry -> CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> registerColoredBlockModel(entry, func, color));
    }

    @OnlyIn(Dist.CLIENT)
    private static void registerColoredBlockModel(Block entry, Supplier<NonNullBiFunction<BakedModel, DyeColor, ? extends BakedModel>> func, DyeColor color) {
        CreateClient.MODEL_SWAPPER.getCustomBlockModels()
                .register(RegisteredObjectsHelper.getKeyOrThrow(entry), model -> func.get().apply(model, color));
    }
}
