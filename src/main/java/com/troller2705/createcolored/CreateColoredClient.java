package com.troller2705.createcolored;

import com.troller2705.createcolored.content.ColoredCreativeTabs;
import com.troller2705.createcolored.content.models.ColoredPartials;
import com.troller2705.createcolored.content.models.ColoredSpriteShifts;

//Client only registrations
public class CreateColoredClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColoredCreativeTabs.initialize();
        ColoredSpriteShifts.initialize();
        ColoredPartials.initialize();
    }
}
