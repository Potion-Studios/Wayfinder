package net.potionstudios.wayfinder.fabric.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.Wayfinder;

@SuppressWarnings("UnstableApiUsage")
public class WayfinderAttachmentData {

    public static final AttachmentType<Boolean> HAS_WAYFINDER = AttachmentRegistry.<Boolean>builder().copyOnDeath().persistent(Codec.BOOL).initializer(() -> false).buildAndRegister(Wayfinder.id("has_wayfinder"));

    public static void setWayfinder(Player player, boolean hasWayfinder) {
        player.setAttached(HAS_WAYFINDER, hasWayfinder);
    }

    public static boolean hasWayfinder(Player player) {
        return player.getAttachedOrCreate(HAS_WAYFINDER);
    }

    public static void init() {
        Wayfinder.LOGGER.info("Registering Wayfinder Fabric Attachment Data");
    }
}
