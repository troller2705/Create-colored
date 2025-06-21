package com.troller2705.createcolored;

import com.mojang.logging.LogUtils;
import com.troller2705.createcolored.content.ColoredTags;
import com.troller2705.createcolored.content.block.ColoredBlocks;
import com.troller2705.createcolored.content.blockEntities.ColoredBlockEntities;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
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
    public static final String MODID = "create-colored";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public CreateColored(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

//        modContainer.registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        LOGGER.info("Registering create-colored blocks!");
        ColoredTags.initialize();
        ColoredBlockEntities.initialize();
        ColoredBlocks.initialize();
        REGISTRATE.register();
    }
}