package com.troller2705.createcolored.datagen;

import com.simibubi.create.foundation.data.AssetLookup;
import com.troller2705.createcolored.content.block.ColoredFluidPipeBlock;
import com.troller2705.createcolored.content.block.ColoredFluidTankBlock;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.Pointing;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColoredBlockStateGen {
    public static <P extends ColoredFluidTankBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> coloredTank(DyeColor color) {
        return (ctx, provider) -> {
            String colorName = color.getName();
            String coloredPath = "block/colored_fluid_tank/" + ctx.getName() + "/";
            String path = "block/fluid_tank/";

            provider.getVariantBuilder(ctx.getEntry())
                    .forAllStates(state -> ConfiguredModel.builder()
                            .modelFile(getModel(ctx, provider, state, color))
                            .build())
                    ;

            /*
            * .forAllStates(state -> {
                        Boolean top = state.getValue(ColoredFluidTankBlock.TOP);
                        Boolean bottom = state.getValue(ColoredFluidTankBlock.BOTTOM);
                        FluidTankBlock.Shape shape = state.getValue(ColoredFluidTankBlock.SHAPE);

                        String shapeName = "middle";
                        if (top && bottom)
                            shapeName = "single";
                        else if (top)
                            shapeName = "top";
                        else if (bottom)
                            shapeName = "bottom";

                        //Create model
                        String modelName = "block_" + shapeName + (shape == FluidTankBlock.Shape.PLAIN ? "" : "_" + shape.name());
                        ModelFile model = provider.models()
                                .withExistingParent(coloredPath + modelName, Create.asResource(path + modelName))
                                .texture("0", provider.modLoc("block/fluid_tank_top/" + colorName))
                                .texture("1", provider.modLoc("block/fluid_tank/" + colorName))
                                .texture("3", provider.modLoc("block/fluid_tank_window/" + colorName))
                                .texture("4", provider.modLoc("block/fluid_tank_inner/" + colorName))
                                .texture("5", provider.modLoc("block/fluid_tank_window_single/" + colorName))
                                .texture("particle", provider.modLoc("block/fluid_tank/" + colorName));

                        return ConfiguredModel.builder()
                                .modelFile(model)
                                .build();
                    })
            * */

        };
    }

    private static <P extends ColoredFluidTankBlock> ModelFile getModel(DataGenContext<Block, P> ctx, RegistrateBlockstateProvider provider, BlockState state, DyeColor color){
        Boolean top = state.getValue(ColoredFluidTankBlock.TOP);
        Boolean bottom = state.getValue(ColoredFluidTankBlock.BOTTOM);
        FluidTankBlock.Shape shape = state.getValue(ColoredFluidTankBlock.SHAPE);
        String colorName = color.getName();

        String shapeName = "middle";
        if (top && bottom)
            shapeName = "single";
        else if (top)
            shapeName = "top";
        else if (bottom)
            shapeName = "bottom";

        String modelName = shapeName + (shape == FluidTankBlock.Shape.PLAIN ? "" : "_" + shape.getSerializedName());

        return provider.models()
                .getExistingFile(provider.modLoc("block/" + ctx.getName() + "/" + colorName + "_fluid_tank" + "/block_" + modelName));

//        return provider.models()
//                .withExistingParent(modelName, provider.modLoc("block/fluid_tank/block_" + modelName))
//                .texture("0", provider.modLoc("block/casing/" + colorName))
//                .texture("1", provider.modLoc("block/fluid_tank/" + colorName))
//                .texture("3", provider.modLoc("block/fluid_tank_window/" + colorName))
//                .texture("4", provider.modLoc("block/casing/" + colorName))
//                .texture("5", provider.modLoc("block/fluid_tank_window_single/" + colorName))
//                .texture("particle", provider.modLoc("block/fluid_tank/" + colorName));
    }

    public static <P extends ColoredFluidPipeBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> coloredPipe(DyeColor color) {
        return (c, p) -> {
            String colorName = color.getName();
            String coloredPath = "block/colored_fluid_pipe/" + c.getName();
            String path = "block/fluid_pipe";

            String LU = "lu";
            String RU = "ru";
            String LD = "ld";
            String RD = "rd";
            String LR = "lr";
            String UD = "ud";
            String U = "u";
            String D = "d";
            String L = "l";
            String R = "r";

            List<String> orientations = ImmutableList.of(LU, RU, LD, RD, LR, UD, U, D, L, R);

            Map<Pair<String, Direction.Axis>, ModelFile> coreModels = new HashMap<>();
            for (Direction.Axis axis : Iterate.axes) {
                for (String orientation : orientations) {
                    Pair<String, Direction.Axis> key = Pair.of(orientation, axis);
                    String sourceModel = path + "/" + orientation + "_" + axis.getName(); //Single model for all pipes
                    String coloredModel = coloredPath + "/" + orientation + "_" + axis.getName(); //each pipe has its own model

                    coreModels.put(key, p.models()
                            .withExistingParent(coloredModel, Create.asResource(sourceModel))
                            .texture("0", p.modLoc("block/pipes_connected/" + colorName))
                            .texture("particle", p.modLoc("block/pipes_connected/" + colorName)));
                }
            }

            MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());
            for (Direction.Axis axis : Iterate.axes) {
                putPart(coreModels, builder, axis, LU, true, false, true, false);
                putPart(coreModels, builder, axis, RU, true, false, false, true);
                putPart(coreModels, builder, axis, LD, false, true, true, false);
                putPart(coreModels, builder, axis, RD, false, true, false, true);
                putPart(coreModels, builder, axis, UD, true, true, false, false);
                putPart(coreModels, builder, axis, U, true, false, false, false);
                putPart(coreModels, builder, axis, D, false, true, false, false);
                putPart(coreModels, builder, axis, LR, false, false, true, true);
                putPart(coreModels, builder, axis, L, false, false, true, false);
                putPart(coreModels, builder, axis, R, false, false, false, true);
            }

            //Partials
            //Casing
            String casingSource = path + "/casing";
            String casingOutput = coloredPath + "/casing";
            p.models().withExistingParent(casingOutput, Create.asResource(casingSource))
                    .texture("0", "block/pipes/" + colorName);

            //Connection
            for (Direction d : Iterate.directions) {
                String sourceModel = path + "/connection/" + d.getName();
                String outputModel = coloredPath + "/connection/" + d.getName();

                p.models().withExistingParent(outputModel, Create.asResource(sourceModel))
                        .texture("0", "block/pipes/" + colorName);
            }

            //Drain
            for (Direction d : Iterate.directions) {
                String sourceModel = path + "/drain/" + d.getName();
                String outputModel = coloredPath + "/drain/" + d.getName();

                p.models().withExistingParent(outputModel, Create.asResource(sourceModel))
                        .texture("0", "block/pipes/" + colorName);
            }

            //Rim
            for (Direction d : Iterate.directions) {
                String sourceModel = path + "/rim/" + d.getName();
                String outputModel = coloredPath + "/rim/" + d.getName();

                p.models().withExistingParent(outputModel, Create.asResource(sourceModel))
                        .texture("0", "block/pipes/" + colorName);
            }

            //Rim_connector
            for (Direction d : Iterate.directions) {
                String sourceModel = path + "/rim_connector/" + d.getName();
                String outputModel = coloredPath + "/rim_connector/" + d.getName();

                p.models().withExistingParent(outputModel, Create.asResource(sourceModel))
                        .texture("0", "block/pipes/" + colorName);
            }
        };
    }

    private static void putPart(Map<Pair<String, Direction.Axis>, ModelFile> coreModels, MultiPartBlockStateBuilder builder,
                                Direction.Axis axis, String s, boolean up, boolean down, boolean left, boolean right) {
        Direction positiveAxis = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        Map<Direction, BooleanProperty> propertyMap = FluidPipeBlock.PROPERTY_BY_DIRECTION;

        Direction upD = Pointing.UP.getCombinedDirection(positiveAxis);
        Direction leftD = Pointing.LEFT.getCombinedDirection(positiveAxis);
        Direction rightD = Pointing.RIGHT.getCombinedDirection(positiveAxis);
        Direction downD = Pointing.DOWN.getCombinedDirection(positiveAxis);

        if (axis == Direction.Axis.Y || axis == Direction.Axis.X) {
            leftD = leftD.getOpposite();
            rightD = rightD.getOpposite();
        }

        builder.part()
                .modelFile(coreModels.get(Pair.of(s, axis)))
                .addModel()
                .condition(propertyMap.get(upD), up)
                .condition(propertyMap.get(leftD), left)
                .condition(propertyMap.get(rightD), right)
                .condition(propertyMap.get(downD), down)
                .end();
    }

    public static <P extends EncasedPipeBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> encasedPipe(DyeColor color) {
        return (c, p) -> {
            String colorName = color.getName();
            ModelFile open = p.models().withExistingParent("block/encased_fluid_pipe/" + colorName + "_encased_fluid_pipe/block_open", Create.asResource("block/encased_fluid_pipe/block_open"))
                    .texture("0", "block/encased_pipe/" + colorName)
                    .texture("particle", "block/encased_pipe/" + colorName);
            ModelFile flat = p.models().getExistingFile(Create.asResource("block/encased_fluid_pipe/block_flat"));
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());
            for (boolean flatPass : Iterate.trueAndFalse)
                for (Direction d : Iterate.directions) {
                    int verticalAngle = d == Direction.UP ? 90 : d == Direction.DOWN ? -90 : 0;
                    builder.part()
                            .modelFile(flatPass ? flat : open)
                            .rotationX(verticalAngle)
                            .rotationY((int) (d.toYRot() + (d.getAxis()
                                    .isVertical() ? 90 : 0)) % 360)
                            .addModel()
                            .condition(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(d), !flatPass)
                            .end();
                }
        };
    }
}