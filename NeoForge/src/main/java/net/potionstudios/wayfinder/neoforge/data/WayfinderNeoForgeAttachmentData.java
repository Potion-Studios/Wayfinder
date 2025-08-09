package net.potionstudios.wayfinder.neoforge.data;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.potionstudios.wayfinder.Wayfinder;

import java.util.UUID;
import java.util.function.Supplier;

public class WayfinderNeoForgeAttachmentData {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Wayfinder.MOD_ID);

    private static final Supplier<AttachmentType<UUID>> WAYFINDER = ATTACHMENT_TYPES.register("wayfinder", () -> AttachmentType.builder(() -> Util.NIL_UUID).serialize(UUIDUtil.CODEC).copyOnDeath().build());
    private static final Supplier<AttachmentType<Integer>> WAYFINDER_DEATHS = ATTACHMENT_TYPES.register("wayfinder_deaths", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build());
    private static final Supplier<AttachmentType<Integer>> THREE_K_JOUNEYS = ATTACHMENT_TYPES.register("3k_journeys", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build());

    public static void setWayfinder(Player player, UUID wayfinder) {
        player.setData(WAYFINDER, wayfinder);
    }

    public static UUID getWayfinder(Player player) {
        return player.getData(WAYFINDER);
    }

    public static int getWayfinderDeaths(Player player) {
        return player.getData(WAYFINDER_DEATHS);
    }

    public static void resetWayfinderDeaths(Player player) {
        player.setData(WAYFINDER_DEATHS, 0);
    }

    public static void incrementWayfinderDeaths(Player player) {
        player.setData(WAYFINDER_DEATHS, getWayfinderDeaths(player) + 1);
    }

    public static int get3kJourneys(Player player) {
        return player.getData(THREE_K_JOUNEYS);
    }

    public static void increment3kJourneys(Player player) {
        player.setData(THREE_K_JOUNEYS, get3kJourneys(player) + 1);
    }

    public static void init(IEventBus eventBus) {
        Wayfinder.LOGGER.info("Registering Wayfinder NeoForge Attachment Data");
        ATTACHMENT_TYPES.register(eventBus);
    }
}
