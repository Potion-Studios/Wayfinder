package net.potionstudios.wayfinder.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.potionstudios.wayfinder.advancements.critereon.WayfinderCriteriaTriggers;
import net.potionstudios.wayfinder.world.entity.ai.memory.WayfinderMemoryModuleType;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TravelToJourneyTarget extends Behavior<WayfinderEntity> {
    private int timeToRecalc;
    private int teleportTimer;

    public TravelToJourneyTarget() {
        super(ImmutableMap.of(
                WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get(), MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED
        ));
    }

    @Override
    protected void start(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        timeToRecalc = 0;
        teleportTimer = 0;
        entity.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    protected void tick(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        if (this.timeToRecalc-- > 0) return;
        this.timeToRecalc = 10;

        Brain<WayfinderEntity> brain = entity.getBrain();

        BlockPos target = brain
                .getMemory(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get())
                .orElse(null);
        if (target == null) return;

        Vec3 to = Vec3.atCenterOf(target).subtract(entity.position());
        double dist = to.length();

        if (dist <= 4.0) {
            int completedDistance = entity.getStartBlockPos().isPresent() ? (int) entity.getStartBlockPos().get().distSqr(target) : 0;
            WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get().trigger((ServerPlayer) entity.getOwner(), level.getBiome(target).unwrapKey().get(), level.dimension(), completedDistance);
            entity.incrementCompletedJourneys((ServerPlayer) entity.getOwner(), completedDistance);
            entity.setStartBlockPos(Optional.empty());
            brain.eraseMemory(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get());
            stop(level, entity, gameTime);
        } else if (entity.getOwner() == null || entity.getOwner().isDeadOrDying()) {
            entity.setStartBlockPos(Optional.empty());
            brain.eraseMemory(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get());
            stop(level, entity, gameTime);
        } else if (entity.distanceToSqr(entity.getOwner()) > 200) {
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            if (++teleportTimer > 4) {
                entity.tryToTeleportToOwner();
                teleportTimer = 0;
            }
        } else {
            Vec3 step = entity.position().add(to.normalize().scale(Math.min(24, dist)));
            BlockPos stepPos = BlockPos.containing(step);
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(stepPos, 1.2F, 3));
        }

        brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    protected void stop(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        entity.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get());
    }
}
