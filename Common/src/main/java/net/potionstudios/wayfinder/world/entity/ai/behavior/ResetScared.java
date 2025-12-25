package net.potionstudios.wayfinder.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;

public class ResetScared implements BehaviorControl<WayfinderEntity> {
    private int counter = 0;

    @Override
    public Behavior.@NotNull Status getStatus() {
        return Behavior.Status.RUNNING;
    }

    @Override
    public boolean tryStart(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        return entity.getBrain().checkMemory(MemoryModuleType.DANGER_DETECTED_RECENTLY, MemoryStatus.VALUE_ABSENT);
    }

    @Override
    public void tickOrStop(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        if (entity.getBrain().checkMemory(MemoryModuleType.DANGER_DETECTED_RECENTLY, MemoryStatus.VALUE_PRESENT)) {
            counter = 0;
            return;
        }
        counter++;
        if (counter >= 100)
            doStop(level, entity, gameTime);
    }

    @Override
    public void doStop(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        entity.setScared(false);
        Brain<WayfinderEntity> brain = entity.getBrain();
        brain.eraseMemory(MemoryModuleType.DANGER_DETECTED_RECENTLY);
        brain.eraseMemory(MemoryModuleType.IS_PANICKING);
        brain.eraseMemory(MemoryModuleType.HURT_BY);
    }

    @Override
    public @NotNull String debugString() {
        return this.getClass().getSimpleName();
    }
}
