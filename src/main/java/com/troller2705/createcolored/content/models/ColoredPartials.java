package com.troller2705.createcolored.content.models;

import com.troller2705.createcolored.CreateColored;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ColoredPartials {

    //Create casings map
    public static final Map<DyeColor, PartialModel> COLORED_FLUID_PIPE_CASINGS = new EnumMap<>(DyeColor.class);
    //Create attachment map
    public static final Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<DyeColor, Map<String, PartialModel>>> COLORED_PIPE_ATTACHMENTS = new EnumMap<>(
            FluidTransportBehaviour.AttachmentTypes.ComponentPartials.class);

    public static final Map<DyeColor, PartialModel> COLORED_BOILER_GAUGE = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, PartialModel> COLORED_BOILER_GAUGE_DIAL = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            COLORED_FLUID_PIPE_CASINGS.put(color, block("colored_fluid_pipe/" + color.getName() + "_fluid_pipe/casing"));

            COLORED_BOILER_GAUGE.put(color, block("colored_gauges/" + color.getName() + "_gauge"));
            COLORED_BOILER_GAUGE_DIAL.put(color, block("colored_gauges/" + color.getName() + "_gauge_dial"));
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
                    map.put(d.getName(), block("colored_fluid_pipe/" + color.getName() + "_fluid_pipe/" + asId + "/" + Lang.asId(d.getName())));
                }
                colorMap.put(color, map);
            }
            COLORED_PIPE_ATTACHMENTS.put(type, colorMap);
        }
    }

    private static PartialModel block(String path) {
        return PartialModel.of(CreateColored.asResource("block/" + path));
    }

    public static void initialize() {
    }
}
