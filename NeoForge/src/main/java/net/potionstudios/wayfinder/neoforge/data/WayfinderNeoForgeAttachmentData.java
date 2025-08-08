package net.potionstudios.wayfinder.neoforge.data;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.potionstudios.wayfinder.Wayfinder;

import java.util.UUID;

public class WayfinderNeoForgeAttachmentData {

    private static final AttachmentType<UUID> WAYFINDER = AttachmentType.builder(() -> Util.NIL_UUID).serialize(UUIDUtil.CODEC).copyOnDeath().build();
    private static final AttachmentType<Integer> WAYFINDER_DEATHS = AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build();

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

    public static void init() {
        Wayfinder.LOGGER.info("Registering Wayfinder NeoForge Attachment Data");
    }
}
