package net.potionstudios.wayfinder.world.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.potionstudios.wayfinder.Wayfinder;

public class WayfinderEntityTypeIds {
    public static final ResourceKey<EntityType<?>> WAYFINDER = create("wayfinder");

    private static ResourceKey<EntityType<?>> create(String name) {
        return Wayfinder.key(Registries.ENTITY_TYPE, name);
    }
}
