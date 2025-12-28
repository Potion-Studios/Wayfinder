package net.potionstudios.wayfinder.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RegenerateShield extends Behavior<WayfinderEntity> {
    private int counter = 0;

    public RegenerateShield() {
        super(Map.of(), 200);
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull WayfinderEntity entity) {
        return entity.shield() != WayfinderEntity.SHIELD.FULL;
    }

    @Override
    protected void tick(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        counter++;
        if (counter >= 100 && entity.shield() == WayfinderEntity.SHIELD.NONE)
            entity.setShield(WayfinderEntity.SHIELD.HALF);
        if (counter >= 200)
            doStop(level, entity, gameTime);
    }

    @Override
    protected void stop(@NotNull ServerLevel level, @NotNull WayfinderEntity entity, long gameTime) {
        entity.setShield(WayfinderEntity.SHIELD.FULL);
    }
}
