package com.troller2705.createcolored;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllCreativeModeTabs;
import com.troller2705.createcolored.content.ColoredCreativeTabs;
import com.troller2705.createcolored.content.ColoredTags;
import com.troller2705.createcolored.content.block.ColoredBlocks;
import com.troller2705.createcolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(CreateColored.MODID)
public class CreateColored {
    public static final String MODID = "create_colored";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateColored.MODID);

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    static {
        REGISTRATE
                .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    public CreateColored(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);

        ColoredTags.initialize();
        ColoredBlocks.initialize();
        ColoredBlockEntities.initialize();
        ColoredCreativeTabs.initialize();

        REGISTRATE
                .defaultCreativeTab(ColoredCreativeTabs.MAIN, "colored_creative_tab");

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        LOGGER.info("Registering create-colored blocks!");

    }
}