package com.troller2705.createcolored.content;

import com.troller2705.createcolored.CreateColored;
import com.troller2705.createcolored.content.block.ColoredBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.registries.DeferredHolder;


public class ColoredCreativeTabs {
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> COLORED_CREATIVE_TAB;

    public static void initialize(){
        COLORED_CREATIVE_TAB = CreateColored.CREATIVE_MODE_TABS.register("colored_creative_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.create-colored.creative_tab"))
                .icon(() -> ColoredBlocks.DYED_PIPES.get(DyeColor.ORANGE).asStack())
                .displayItems(((itemDisplayParameters, output) -> {
                    for (DyeColor color : DyeColor.values()) {
                        output.accept(ColoredBlocks.DYED_PIPES.get(color).asStack());
                        output.accept(ColoredBlocks.DYED_FLUID_TANKS.get(color).asStack());
                    }
                }))
                .build());
    }

}


