package com.troller2705.createcolored.content.models;

import com.troller2705.createcolored.CreateColored;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ColoredPartials {

    //Create casings map
    public static final Map<DyeColor, PartialModel> COLORED_FLUID_PIPE_CASINGS = new EnumMap<>(DyeColor.class);
    //Create attachment map
    public static final Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<DyeColor, Map<String, PartialModel>>> COLORED_PIPE_ATTACHMENTS = new EnumMap<>(
            FluidTransportBehaviour.AttachmentTypes.ComponentPartials.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            COLORED_FLUID_PIPE_CASINGS.put(color, block("colored_fluid_pipe/" + color.getName() + "_fluid_pipe/casing"));
        }
    }

    // populate models
    static {
        for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials type : FluidTransportBehaviour.AttachmentTypes.ComponentPartials
                .values()) {
            Map<DyeColor, Map<String, PartialModel>> colorMap = new EnumMap<>(DyeColor.class);
            for (DyeColor color : DyeColor.values()) {
                Map<String, PartialModel> map = new HashMap<>();
                for (Direction d : Iterate.directions) {
                    String asId = Lang.asId(type.name());
                    map.put(d.asString(), block("colored_fluid_pipe/" + color.getName() + "_fluid_pipe/" + asId + "/" + Lang.asId(d.asString())));
                }
                colorMap.put(color, map);
            }
            COLORED_PIPE_ATTACHMENTS.put(type, colorMap);
        }
    }

    private static PartialModel block(String path) {
        return new PartialModel(new Identifier(CreateColored.MOD_ID, "block/" + path));
    }

    public static void initialize() {
    }
}
