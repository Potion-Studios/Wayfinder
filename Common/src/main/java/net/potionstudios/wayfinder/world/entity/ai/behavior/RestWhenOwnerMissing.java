package net.potionstudios.wayfinder.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestWhenOwnerMissing extends Behavior<WayfinderEntity> {
    public RestWhenOwnerMissing() {
        super(ImmutableMap.of());
    }

    @Override
    protected void start(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        entity.sit();
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull WayfinderEntity entity) {
        @Nullable LivingEntity owner = entity.getOwner();
        return (owner == null || owner.isSleeping() || !owner.isAlive()) && !entity.isResting();
    }
}
