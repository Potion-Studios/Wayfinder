package net.potionstudios.wayfinder.world.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.tags.WayfinderEntityTypeTags;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

public class ScaredWayfinderGoal extends Goal {

    private final WayfinderEntity entity;

    public ScaredWayfinderGoal(WayfinderEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick() {
        entity.setScared(!entity.level().getNearbyEntities(Monster.class,
                TargetingConditions.forNonCombat()
                        .selector(livingEntity -> livingEntity.getType().is(WayfinderEntityTypeTags.SCARES_WAYFINDER)),
                            entity, entity.getBoundingBox().inflate(Wayfinder.CONFIG.wayfinder.SCARED_PROJECTILE_MOB_DISTANCE_IN_BLOCKS)).isEmpty());
    }

    @Override
    public boolean canUse() {
        return !entity.isScared() ;
    }
}
