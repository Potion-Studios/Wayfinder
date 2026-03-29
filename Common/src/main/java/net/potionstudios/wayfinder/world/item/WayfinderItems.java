package net.potionstudios.wayfinder.world.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.WayfinderEntityType;
import net.potionstudios.wayfinder.world.item.jukebox.WayfinderJukeBoxSongs;

import java.util.function.Supplier;

public class WayfinderItems {

    public static final Supplier<SpawnEggItem> WAYFINDER_SPAWN_EGG = register("wayfinder_spawn_egg", () -> new SpawnEggItem(new Item.Properties().setId(id("wayfinder_spawn_egg")).spawnEgg(WayfinderEntityType.WAYFINDER.get())));

    public static final Supplier<Item> MUSIC_DISC_SWEET_DREAMS = register("music_disc_sweet_dreams", () -> new Item((new Item.Properties()).setId(id("music_disc_sweet_dreams")).stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(WayfinderJukeBoxSongs.SWEET_DREAMS)));

    public static <I extends Item> Supplier<I> register(String id, Supplier<I> item) {
        return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.ITEM, id, item);
    }

    private static ResourceKey<Item> id(String id) {
        return Wayfinder.key(BuiltInRegistries.ITEM.key(), id);
    }

    public static void items() {
        Wayfinder.LOGGER.info("Registering Wayfinder items");
    }
}
