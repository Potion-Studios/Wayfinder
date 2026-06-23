package net.potionstudios.wayfinder.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.potionstudios.wayfinder.world.entity.ai.memory.WayfinderMemoryModuleType;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jspecify.annotations.NonNull;

public class FollowOwner extends Behavior<WayfinderEntity> {
    private ServerPlayer owner;
    private int timeToRecalcPath;

    public FollowOwner() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get(), MemoryStatus.VALUE_ABSENT
        ));
    }

    @Override
    protected boolean checkExtraStartConditions(@NonNull ServerLevel level, @NonNull WayfinderEntity entity) {
        if (entity.unableToMoveToOwner()) return false;

        if (!(entity.getOwner() instanceof ServerPlayer player)) return false;

        if (entity.distanceToSqr(player) < 30) return false;

        this.owner = player;
        return true;
    }

    @Override
    protected void start(@NonNull ServerLevel level, @NonNull WayfinderEntity entity, long gameTime) {
        this.timeToRecalcPath = 0;
    }

    @Override
    protected void stop(@NonNull ServerLevel level, @NonNull WayfinderEntity entity, long gameTime) {
        this.owner = null;
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        entity.getNavigation().stop();
    }

    @Override
    protected void tick(@NonNull ServerLevel level, @NonNull WayfinderEntity entity, long gameTime) {
        if (this.owner == null) return;

        if (this.timeToRecalcPath-- > 0) return;
        this.timeToRecalcPath = 10;

        if (entity.shouldTryTeleportToOwner()) {
            entity.tryToTeleportToOwner();
            entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            return;
        }

        entity.getBrain().setMemory(
                MemoryModuleType.WALK_TARGET,
                new WalkTarget(new EntityTracker(this.owner, false), 1.5F, 2)
        );
    }

    @Override
    protected boolean canStillUse(@NonNull ServerLevel level, @NonNull WayfinderEntity entity, long gameTime) {
        if (entity.unableToMoveToOwner()) return false;

        return entity.distanceToSqr(this.owner) > 16;
    }
}
