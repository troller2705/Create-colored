package com.azasad.createcolored;

import com.azasad.createcolored.content.ColoredCreativeTabs;
import com.azasad.createcolored.content.models.ColoredPartials;
import com.azasad.createcolored.content.models.ColoredSpriteShifts;
import net.fabricmc.api.ClientModInitializer;

//Client only registrations
public class CreateColoredClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColoredCreativeTabs.initialize();
        ColoredSpriteShifts.initialize();
        ColoredPartials.initialize();
    }
}
