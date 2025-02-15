package net.potionstudios.wayfinder.fabric.data;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.Wayfinder;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class WayfinderAttachmentData {

    public static final AttachmentType<UUID> WAYFINDER = AttachmentRegistry.create(Wayfinder.id("wayfinder"), uuidBuilder -> uuidBuilder.copyOnDeath().persistent(UUIDUtil.CODEC).initializer(() -> Util.NIL_UUID));

    public static void setWayfinder(Player player, UUID wayfinder) {
        player.setAttached(WAYFINDER, wayfinder);
    }

    public static UUID getWayfinder(Player player) {
        return player.getAttachedOrCreate(WAYFINDER);
    }

    public static void init() {
        Wayfinder.LOGGER.info("Registering Wayfinder Fabric Attachment Data");
    }
}
