package net.potionstudios.wayfinder.world.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.WayfinderEntities;

import java.awt.*;
import java.util.function.Supplier;

public class WayfinderItems {

    public static final Supplier<SpawnEggItem> WAYFINDER_SPAWN_EGG = register("wayfinder_spawn_egg", PlatformHandler.PLATFORM_HANDLER.createSpawnEgg(WayfinderEntities.WAYFINDER::get, new Color(84, 71, 63).getRGB(), new Color(108, 128, 49).getRGB()));

    public static <I extends Item> Supplier<I> register(String id, Supplier<I> item) {
        return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.ITEM, id, item);
    }

    public static void items() {
        Wayfinder.LOGGER.info("Registering Wayfinder items");
    }
}
