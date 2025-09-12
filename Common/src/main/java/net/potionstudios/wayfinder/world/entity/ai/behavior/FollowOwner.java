package net.potionstudios.wayfinder.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;

public class FollowOwner extends Behavior<WayfinderEntity> {
    private ServerPlayer owner;
    private int timeToRecalcPath;

    public FollowOwner() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull WayfinderEntity owner) {
        ServerPlayer player = (ServerPlayer) owner.getOwner();
        if (player == null || owner.unableToMoveToOwner() || owner.distanceToSqr(player) < 20)
            return false;

        this.owner = player;
        return true;
    }

    @Override
    protected void start(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        this.timeToRecalcPath = 0;
    }

    @Override
    protected void stop(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        this.owner = null;
        entity.getNavigation().stop();
    }

    @Override
    protected void tick(@NotNull ServerLevel level, @NotNull WayfinderEntity owner, long gameTime) {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (owner.shouldTryTeleportToOwner())
                owner.tryToTeleportToOwner();
            else owner.getNavigation().moveTo(this.owner, 1.5);
        }
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        return true;
    }
}
