package net.potionstudios.wayfinder.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jspecify.annotations.NonNull;

public class RestWhenOwnerMissing extends Behavior<WayfinderEntity> {
    public RestWhenOwnerMissing() {
        super(ImmutableMap.of());
    }

    @Override
    protected void start(@NonNull ServerLevel level, @NonNull WayfinderEntity entity, long gameTime) {
        entity.sit();
    }

    @Override
    protected boolean checkExtraStartConditions(@NonNull ServerLevel level, @NonNull WayfinderEntity entity) {
        LivingEntity owner = entity.getOwner();
        return (owner == null || owner.isSleeping() || !owner.isAlive()) && !entity.isResting();
    }
}
