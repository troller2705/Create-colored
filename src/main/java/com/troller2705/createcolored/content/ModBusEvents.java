package com.troller2705.createcolored.content;

import com.troller2705.createcolored.content.blockEntities.ColoredFluidTankBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        ColoredFluidTankBlockEntity.registerCapabilities(event);
    }

}
