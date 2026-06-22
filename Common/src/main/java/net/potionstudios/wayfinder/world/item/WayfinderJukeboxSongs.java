package net.potionstudios.wayfinder.world.item;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.sounds.WayfinderSoundEvents;

import java.util.Map;
import java.util.function.Supplier;

public interface WayfinderJukeboxSongs {
    Map<ResourceKey<JukeboxSong>, JukeBoxSongFactory> JUKEBOX_SONG_FACTORIES = new Reference2ObjectOpenHashMap<>();

    ResourceKey<JukeboxSong> SWEET_DREAMS = register("pixie_club", WayfinderSoundEvents.MUSIC_DISC_SWEET_DREAMS, 162, 4);

    private static ResourceKey<JukeboxSong> register(String id, Supplier<Holder.Reference<SoundEvent>> soundEvent, int lengthInSeconds, int comparatorOutput) {
        ResourceKey<JukeboxSong> key = Wayfinder.key(Registries.JUKEBOX_SONG, id);
        JUKEBOX_SONG_FACTORIES.put(key, context -> new JukeboxSong(soundEvent.get(), Component.translatable(Util.makeDescriptionId("jukebox_song", key.identifier())), lengthInSeconds, comparatorOutput));
        return key;
    }

    @FunctionalInterface
    interface JukeBoxSongFactory{
        JukeboxSong generate(BootstrapContext<JukeboxSong> context);
    }
}
