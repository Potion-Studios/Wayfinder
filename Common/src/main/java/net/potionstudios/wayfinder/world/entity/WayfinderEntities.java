package net.potionstudios.wayfinder.world.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

import java.util.function.Supplier;

public class WayfinderEntities {

    public static final Supplier<EntityType<WayfinderEntity>> WAYFINDER = createEntity("wayfinder", WayfinderEntity::new, MobCategory.AMBIENT, 0.6F, 1F);

    private static <E extends Entity> Supplier<EntityType<E>> createEntity(String id, EntityType.EntityFactory<E> factory, MobCategory category, float width, float height) {
        return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.ENTITY_TYPE, id, ()-> EntityType.Builder.of(factory, category).sized(width, height).build(id));
    }

    public static void entities() {
        Wayfinder.LOGGER.info("Registering Wayfinder Entity");
    }
}
