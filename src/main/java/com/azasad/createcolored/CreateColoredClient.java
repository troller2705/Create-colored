package com.azasad.createcolored;

import com.azasad.createcolored.content.models.ColoredPartials;
import net.fabricmc.api.ClientModInitializer;

//Client only registrations
public class CreateColoredClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColoredPartials.initialize();
    }
}
