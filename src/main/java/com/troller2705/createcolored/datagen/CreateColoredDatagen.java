package com.troller2705.createcolored.datagen;

import com.troller2705.createcolored.CreateColored;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CreateColoredDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        ExistingFileHelper helper = ExistingFileHelper.withResourcesFromArg();
        CreateColored.REGISTRATE.setupDatagen(dataGenerator.createPack(), helper);
    }
} 
