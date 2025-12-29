package net.potionstudios.wayfinder.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LookAtOwner extends Behavior<WayfinderEntity> {
    private int cooldown;
    @Nullable
    private ServerPlayer owner;
    public LookAtOwner() {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT
        ));
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull WayfinderEntity entity) {
        if (!(entity.getOwner() instanceof ServerPlayer player) || player.isDeadOrDying()) return false;

        if (entity.distanceToSqr(player) > 64) return false;

        this.owner = player;
        return true;
    }

    @Override
    protected void start(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        cooldown = 0;
    }

    @Override
    protected void tick(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        if (cooldown-- > 0 || owner == null) return;
        cooldown = 5;
        entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(owner, true));
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        return owner != null && owner.isAlive() && entity.distanceToSqr(owner) < 64;
    }

    @Override
    protected void stop(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        owner = null;
        entity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
    }
}
