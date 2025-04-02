package net.potionstudios.wayfinder.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.tags.WayfinderEntityTypeTags;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

import java.util.List;

public class ScaredWayfinderGoal extends Goal {

    private final WayfinderEntity entity;
    private int ticksToNormal;
    private List<Monster> targets;
    private boolean wasScaredByEntity;

    public ScaredWayfinderGoal(WayfinderEntity entity) {
        this.entity = entity;
        this.targets = List.of();
    }

    @Override
    public void tick() {
        Wayfinder.LOGGER.info("Scared wayfinder goal tick");
        if (entity.isScared()) {
            if (wasScaredByEntity) {
                // If scared by a monster, stop being scared immediately if all threats are gone
                if (targets.stream().allMatch(LivingEntity::isDeadOrDying)) {
                    entity.setScared(false);
                    return;
                }
            }

            // If scared by an unknown cause (e.g., player hit), wait for the timer
            if (ticksToNormal > 0) {
                ticksToNormal--;
                if (ticksToNormal == 0) {
                    entity.setScared(false);
                }
            }
            return;
        }

        // If not currently scared, check for nearby monsters
        refreshTargets();
        if (!targets.isEmpty()) {
            entity.setScared(true);
            wasScaredByEntity = true;
            ticksToNormal = 200;
        }
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void start() {
        ticksToNormal = 200;
        wasScaredByEntity = false;
        refreshTargets();
    }

    /**
     * Refreshes the list of monsters nearby.
     */
    private void refreshTargets() {
        targets = entity.level().getNearbyEntities(Monster.class,
                TargetingConditions.forNonCombat()
                        .selector(livingEntity -> livingEntity.getType().is(WayfinderEntityTypeTags.SCARES_WAYFINDER)),
                entity, entity.getBoundingBox().inflate(Wayfinder.CONFIG.wayfinder.SCARED_PROJECTILE_MOB_DISTANCE_IN_BLOCKS));
    }
}
