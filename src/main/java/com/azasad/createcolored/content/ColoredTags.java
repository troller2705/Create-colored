package com.azasad.createcolored.content;

import com.azasad.createcolored.CreateColored;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ColoredTags {
    public static <T> TagKey<T> optionalTag(Registry<T> registry,
                                            Identifier id) {
        return TagKey.of(registry.getKey(), id);
    }

    public static <T> TagKey<T> forgeTag(Registry<T> registry, String path) {
        return optionalTag(registry, new Identifier("c", path));
    }

    public enum NameSpace {
        MOD(CreateColored.MOD_ID, false, true),
        CREATE("create-colored"),
        FORGE("forge"),
        TIC("tic"),
        QUARK("quark");

        public final String id;
        public final boolean optionalDefault;
        public final boolean alwaysDatagenDefault;

        NameSpace(String id) {
            this(id, true, false);
        }

        NameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
            this.id = id;
            this.optionalDefault = optionalDefault;
            this.alwaysDatagenDefault = alwaysDatagenDefault;
        }
    }

    public enum ColoredItemTags {
        COLORED_PIPES;

        public final TagKey<Item> tag;
        public final boolean alwaysDatagen;

        ColoredItemTags() {
            this(NameSpace.MOD);
        }

        ColoredItemTags(ColoredTags.NameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        ColoredItemTags(ColoredTags.NameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        ColoredItemTags(ColoredTags.NameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        ColoredItemTags(ColoredTags.NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            Identifier id = new Identifier(namespace.id, path == null ? Lang.asId(name()) : path);
            tag = optionalTag(Registries.ITEM, id);

            this.alwaysDatagen = alwaysDatagen;
        }

        private static void initialize() {}

    }

    public static void initialize() {
        ColoredItemTags.initialize();
    }
}
