package com.troller2705.createcolored;

import com.troller2705.createcolored.content.ColoredCreativeTabs;
import com.troller2705.createcolored.content.models.ColoredPartials;
import com.troller2705.createcolored.content.models.ColoredSpriteShifts;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = CreateColored.MODID, dist = Dist.CLIENT)
public class CreateColoredClient {

    public CreateColoredClient(ModContainer modContainer){
        ColoredCreativeTabs.initialize();
        ColoredSpriteShifts.initialize();
        ColoredPartials.initialize();
    }

}
