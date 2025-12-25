package net.potionstudios.wayfinder.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;

public class RegenerateShield implements BehaviorControl<WayfinderEntity> {
    private int counter = 0;
    @Override
    public Behavior.@NotNull Status getStatus() {
        return Behavior.Status.RUNNING;
    }

    @Override
    public boolean tryStart(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        return true;
    }

    @Override
    public void tickOrStop(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        counter++;
        if (counter >= 100 && entity.shield() == WayfinderEntity.SHIELD.NONE)
            entity.setShield(WayfinderEntity.SHIELD.HALF);
        if (counter >= 200)
            doStop(level, entity, gameTime);
    }

    @Override
    public void doStop(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        entity.setShield(WayfinderEntity.SHIELD.FULL);
    }

    @Override
    public @NotNull String debugString() {
        return this.getClass().getSimpleName();
    }
}
