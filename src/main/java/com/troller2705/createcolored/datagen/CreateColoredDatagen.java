//package com.troller2705.createcolored.datagen;
//
//import com.troller2705.createcolored.CreateColored;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.server.packs.PackType;
//import net.neoforged.bus.api.IEventBus;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.ModContainer;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.fml.common.Mod;
//import net.neoforged.fml.config.ModConfig;
//import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
//import net.neoforged.neoforge.common.NeoForge;
//import net.neoforged.neoforge.common.data.ExistingFileHelper;
//import net.neoforged.neoforge.data.event.GatherDataEvent;
//import net.neoforged.neoforge.registries.DeferredRegister;
//import org.slf4j.Logger;
//
//@EventBusSubscriber(modid = CreateColored.MODID, bus = EventBusSubscriber.Bus.MOD)
//public class CreateColoredDatagen {
//
//    @SubscribeEvent
//    public static void gatherData(GatherDataEvent event) {
//        DataGenerator generator = event.getGenerator();
//        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
//    }
//
//
//    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
//        ExistingFileHelper helper = ExistingFileHelper.withResourcesFromArg();
//        CreateColored.REGISTRATE.setupDatagen(dataGenerator.createPack(), helper);
//    }
//}
