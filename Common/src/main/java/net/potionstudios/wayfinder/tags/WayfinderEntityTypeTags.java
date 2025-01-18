package net.potionstudios.wayfinder.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.potionstudios.wayfinder.Wayfinder;

public class WayfinderEntityTypeTags {

    public static final TagKey<EntityType<?>> SCARES_WAYFINDER = create("scares_wayfinder");

    private static TagKey<EntityType<?>> create(String id) {
        return TagKey.create(Registries.ENTITY_TYPE, Wayfinder.id(id));
    }
}
