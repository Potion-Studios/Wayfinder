package net.potionstudios.wayfinder.world.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

import java.util.function.Supplier;

public class WayfinderEntities {

    public static final Supplier<EntityType<WayfinderEntity>> WAYFINDER = PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.ENTITY_TYPE, "wayfinder", ()-> EntityType.Builder.<WayfinderEntity>of(WayfinderEntity::new, MobCategory.AMBIENT).sized(0.6F, 1.09F).eyeHeight(0.36F).build("wayfinder"));

    public static void entities() {
        Wayfinder.LOGGER.info("Registering Wayfinder Entity");
    }
}
