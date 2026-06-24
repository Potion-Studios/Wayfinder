package net.potionstudios.wayfinder.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.network.packets.WayfinderCloseScreenPacket;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jspecify.annotations.NonNull;

/**
 * Behavior that closes a wayfinder screen if open when the entity is panicking
 * @author Joseph T. McQuigg
 * @see Behavior
 */
public class CloseScreenOnPanic extends Behavior<WayfinderEntity> {
    public CloseScreenOnPanic() {
        super(ImmutableMap.of(MemoryModuleType.DANGER_DETECTED_RECENTLY, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected void start(@NonNull ServerLevel level, @NonNull WayfinderEntity entity, long gameTime) {
        if (entity.getOwner() != null)
            PlatformHandler.PLATFORM_HANDLER.sendToPlayer(new WayfinderCloseScreenPacket(), (Player) entity.getOwner());
    }
}
