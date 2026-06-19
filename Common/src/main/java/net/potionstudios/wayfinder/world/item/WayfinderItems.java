package net.potionstudios.wayfinder.world.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.WayfinderEntityType;

import java.util.function.Supplier;

public class WayfinderItems {

    public static final Supplier<SpawnEggItem> WAYFINDER_SPAWN_EGG = register("wayfinder_spawn_egg", PlatformHandler.PLATFORM_HANDLER.createSpawnEgg(WayfinderEntityType.WAYFINDER::get, FastColor.ARGB32.color(84, 71, 63), FastColor.ARGB32.color(108, 128, 49)));

    public static final Supplier<Item> MUSIC_DISC_SWEET_DREAMS = register("music_disc_sweet_dreams", () -> new Item((new Item.Properties()).stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(WayfinderJukeboxSongs.SWEET_DREAMS)));

    public static <I extends Item> Supplier<I> register(String id, Supplier<I> item) {
        return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.ITEM, id, item);
    }

    public static void items() {
        Wayfinder.LOGGER.info("Registering Wayfinder items");
    }
}
