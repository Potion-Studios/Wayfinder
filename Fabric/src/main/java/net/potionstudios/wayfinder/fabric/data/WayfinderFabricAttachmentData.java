package net.potionstudios.wayfinder.fabric.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.Wayfinder;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class WayfinderFabricAttachmentData {

    private static final AttachmentType<UUID> WAYFINDER = AttachmentRegistry.create(Wayfinder.id("wayfinder"), uuidBuilder -> uuidBuilder.copyOnDeath().persistent(UUIDUtil.CODEC).initializer(() -> Util.NIL_UUID));
    private static final AttachmentType<Integer> WAYFINDER_DEATHS = AttachmentRegistry.create(Wayfinder.id("wayfinder_deaths"), integerBuilder -> integerBuilder.copyOnDeath().persistent(Codec.INT).initializer(() -> 0));
    private static final AttachmentType<Integer> THREE_K_JOUNEYS = AttachmentRegistry.create(Wayfinder.id("3k_journeys"), integerBuilder -> integerBuilder.copyOnDeath().persistent(Codec.INT).initializer(() -> 0));

    public static void setWayfinder(Player player, UUID wayfinder) {
        player.setAttached(WAYFINDER, wayfinder);
    }

    public static UUID getWayfinder(Player player) {
        return player.getAttachedOrCreate(WAYFINDER);
    }

    public static int getWayfinderDeaths(Player player) {
        return player.getAttachedOrCreate(WAYFINDER_DEATHS);
    }

    public static void resetWayfinderDeaths(Player player) {
        player.setAttached(WAYFINDER_DEATHS, 0);
    }

    public static void incrementWayfinderDeaths(Player player) {
        player.setAttached(WAYFINDER_DEATHS, getWayfinderDeaths(player) + 1);
    }

    public static int get3kJourneys(Player player) {
        return player.getAttachedOrCreate(THREE_K_JOUNEYS);
    }

    public static void increment3kJourneys(Player player) {
        player.setAttached(THREE_K_JOUNEYS, get3kJourneys(player) + 1);
    }

    public static void init() {
        Wayfinder.LOGGER.info("Registering Wayfinder Fabric Attachment Data");
    }
}
