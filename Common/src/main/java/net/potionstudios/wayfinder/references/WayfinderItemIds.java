package net.potionstudios.wayfinder.references;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.potionstudios.wayfinder.Wayfinder;

public class WayfinderItemIds {
    public static final ResourceKey<Item> WAYFINDER_SPAWN_EGG = create("wayfinder_spawn_egg");
    public static final ResourceKey<Item> MUSIC_DISC_SWEET_DREAMS = create("music_disc_sweet_dreams");

    private static ResourceKey<Item> create(String name) {
        return Wayfinder.key(Registries.ITEM, name);
    }
}
