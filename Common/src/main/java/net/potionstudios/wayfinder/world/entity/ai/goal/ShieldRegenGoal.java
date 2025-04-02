package net.potionstudios.wayfinder.world.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

public class ShieldRegenGoal extends Goal {
    private final WayfinderEntity wayfinder;

    public ShieldRegenGoal(WayfinderEntity wayfinder) {
        this.wayfinder = wayfinder;
    }

    @Override
    public void tick() {
        if (wayfinder.level().getRandom().nextInt(10) == 0)
            wayfinder.setShield(WayfinderEntity.SHIELD.byHits(wayfinder.shield().hits() + 1));
    }

    @Override
    public boolean canUse() {
        return wayfinder.shield() != WayfinderEntity.SHIELD.FULL && !wayfinder.isScared();
    }
}
