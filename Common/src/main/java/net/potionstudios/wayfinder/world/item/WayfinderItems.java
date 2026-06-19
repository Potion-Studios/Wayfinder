package net.potionstudios.wayfinder.world.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.references.WayfinderItemIds;
import net.potionstudios.wayfinder.world.entity.WayfinderEntityTypes;
import net.potionstudios.wayfinder.world.item.WayfinderJukeboxSongs;

import java.util.function.Supplier;

public class WayfinderItems {

    public static final Supplier<SpawnEggItem> WAYFINDER_SPAWN_EGG = register("wayfinder_spawn_egg", () -> new SpawnEggItem(new Item.Properties().setId(WayfinderItemIds.WAYFINDER_SPAWN_EGG).spawnEgg(WayfinderEntityTypes.WAYFINDER.get())));

    public static final Supplier<Item> MUSIC_DISC_SWEET_DREAMS = register("music_disc_sweet_dreams", () -> new Item((new Item.Properties()).setId(WayfinderItemIds.MUSIC_DISC_SWEET_DREAMS).stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(WayfinderJukeboxSongs.SWEET_DREAMS)));

    public static <I extends Item> Supplier<I> register(String id, Supplier<I> item) {
        return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.ITEM, id, item);
    }

    public static void items() {
        Wayfinder.LOGGER.info("Registering Wayfinder items");
    }
}
