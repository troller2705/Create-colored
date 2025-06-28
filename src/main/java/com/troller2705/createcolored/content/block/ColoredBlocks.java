package com.troller2705.createcolored.content.block;

import com.simibubi.create.*;
import com.simibubi.create.content.fluids.tank.FluidTankMovementBehavior;
import com.troller2705.createcolored.ColoredHelpers;
import com.troller2705.createcolored.ColoredRegistrate;
import com.troller2705.createcolored.CreateColored;
import com.troller2705.createcolored.content.ColoredTags;
import com.troller2705.createcolored.content.item.ColoredFluidTankItem;
import com.troller2705.createcolored.content.models.ColoredFluidTankModel;
import com.troller2705.createcolored.content.models.ColoredPipeAttachmentModel;
import com.troller2705.createcolored.datagen.ColoredBlockStateGen;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType.mountedFluidStorage;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;


@SuppressWarnings({"deprecation", "removal"})
public class ColoredBlocks {

    public static final DyedBlockList<ColoredFluidTankBlock> DYED_FLUID_TANKS = new DyedBlockList<>(dyecolor -> {
       String colorName = dyecolor.getName();
        return CreateColored.REGISTRATE.block(colorName + "_fluid_tank", (properties) -> ColoredFluidTankBlock.regular(properties, dyecolor))
               .initialProperties(SharedProperties::copperMetal)
               .properties(p -> p.noOcclusion()
                       .isRedstoneConductor((p1, p2, p3) -> true))
               .transform(pickaxeOnly())
               .blockstate(ColoredBlockStateGen.coloredTank(dyecolor))
               .onRegister(ColoredRegistrate.coloredBlockModel(() -> ColoredFluidTankModel::standard, dyecolor))
               .transform(displaySource(AllDisplaySources.BOILER))
               .transform(mountedFluidStorage(AllMountedStorageTypes.FLUID_TANK))
               .onRegister(movementBehaviour(new FluidTankMovementBehavior()))
               .addLayer(() -> RenderType::cutoutMipped)
               .item(ColoredFluidTankItem::new)
               .recipe((c,p) -> {
                   ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, c.get(), 1)
                           .requires(ColoredHelpers.getDyeItem(dyecolor))
                           .requires(AllBlocks.FLUID_TANK.asItem())
                           .unlockedBy("has_tank", RegistrateRecipeProvider.has(AllBlocks.FLUID_TANK))
                           .save(p, CreateColored.asResource(c.getName()));
               })
               .tag(ColoredTags.ColoredItemTags.COLORED_TANKS.tag)
               .build()
               .register();
    });

    public static final DyedBlockList<ColoredFluidPipeBlock> DYED_PIPES = new DyedBlockList<>(dyeColor -> {
        String colorName = dyeColor.getName();
        return CreateColored.REGISTRATE.block(colorName + "_fluid_pipe", settings -> new ColoredFluidPipeBlock(settings, dyeColor))
                .initialProperties(SharedProperties::copperMetal)
                .properties(p -> p.mapColor(dyeColor.getMapColor())
                        .forceSolidOff())
                .transform(pickaxeOnly())
                .onRegister(ColoredRegistrate.coloredBlockModel(() -> ColoredPipeAttachmentModel::withAO, dyeColor))
                .blockstate(ColoredBlockStateGen.coloredPipe(dyeColor))
                .item()
                .recipe((c, p) -> {
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, c.get(), 1)
                            .requires(ColoredHelpers.getDyeItem(dyeColor))
                            .requires(AllBlocks.FLUID_PIPE.asItem())
                            .unlockedBy("has_pipe", RegistrateRecipeProvider.has(AllBlocks.FLUID_PIPE))
                            .save(p, CreateColored.asResource(c.getName()));
                })
                .tag(ColoredTags.ColoredItemTags.COLORED_PIPES.tag)
                .build()
                .register();
    });

    public static final DyedBlockList<ColoredGlassFluidPipeBlock> DYED_GLASS_PIPES = new DyedBlockList<>(dyeColor -> {
        String colorName = dyeColor.getName();
        return CreateColored.REGISTRATE.block(colorName + "_glass_fluid_pipe", settings -> new ColoredGlassFluidPipeBlock(settings, dyeColor))
                .initialProperties(SharedProperties::copperMetal)
                .properties(p -> p.noOcclusion())
                .addLayer(() -> RenderType::cutoutMipped)
                .transform(pickaxeOnly())
                .blockstate((c, p) ->
                        p.getVariantBuilder(c.getEntry())
                                .forAllStatesExcept(state -> {
                                    Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                                    return ConfiguredModel.builder()
                                            .modelFile(p.models().
                                                    withExistingParent("block/colored_fluid_pipe/" + colorName + "_fluid_pipe/window", Create.asResource("block/fluid_pipe/window"))
                                                    .texture("0", "block/glass_fluid_pipe/" + colorName))
                                            .uvLock(false)
                                            .rotationX(axis == Direction.Axis.Y ? 0 : 90)
                                            .rotationY(axis == Direction.Axis.X ? 90 : 0)
                                            .build();
                                }, BlockStateProperties.WATERLOGGED))
                .onRegister(ColoredRegistrate.coloredBlockModel(() -> ColoredPipeAttachmentModel::withAO, dyeColor))
                .loot((p, b) -> p.dropOther(b, DYED_PIPES.get(dyeColor).get()))
                .register();
    });

    public static final DyedBlockList<ColoredEncasedPipeBlock> DYED_ENCASED_PIPES = new DyedBlockList<>(dyeColor -> {
        String colorName = dyeColor.getName();
        return CreateColored.REGISTRATE.block(colorName + "_encased_fluid_pipe", p -> new ColoredEncasedPipeBlock(p, AllBlocks.COPPER_CASING::get, dyeColor))
                .initialProperties(SharedProperties::copperMetal)
                .properties(p -> p.noOcclusion()
                            .mapColor(dyeColor.getMapColor()))
                .transform(axeOrPickaxe())
                .blockstate(ColoredBlockStateGen.encasedPipe(dyeColor))
                .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.COPPER_CASING)))
                .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.COPPER_CASING,
                        (s, f) -> !s.getValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)))))
                .onRegister(ColoredRegistrate.coloredBlockModel(() -> ColoredPipeAttachmentModel::withoutAO, dyeColor))
                .loot((p, b) -> p.dropOther(b, DYED_PIPES.get(dyeColor).get()))
                .transform(EncasingRegistry.addVariantTo(DYED_PIPES.get(dyeColor)))
                .register();
    });

    public static void initialize() {

    }
}

