package net.potionstudios.wayfinder.world.entity.ai.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {
    private final WayfinderEntity mob;
    private Entity target;
    private final double speed;
    private final float minDistance;
    private final float maxDistance;

    public FollowOwnerGoal(WayfinderEntity mob, Entity target, double speed, float minDistance, float maxDistance) {
        this.mob = mob;
        this.target = target;
        this.speed = speed;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (target == null) {
            return true;
        } else {
            return target.isAlive() && mob.distanceToSqr(target) > minDistance * minDistance;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null) {
            return true;
        } else {
            return target.isAlive() && mob.distanceToSqr(target) > minDistance * minDistance && mob.distanceToSqr(target) < maxDistance * maxDistance;
        }
    }

    @Override
    public void tick() {
        if (target != null) {
            double distance = mob.distanceToSqr(target);
            if (distance > minDistance * minDistance) {
                Vec3 direction = new Vec3(target.getX() - mob.getX(), target.getY() - mob.getY(), target.getZ() - mob.getZ()).normalize();
                Vec3 newPos = new Vec3(target.getX() - direction.x * minDistance, target.getY() - direction.y * minDistance, target.getZ() - direction.z * minDistance);
                mob.getMoveControl().setWantedPosition(newPos.x, newPos.y + 1.5F, newPos.z, speed);
            }
        } else {
            target = mob.getOwner();
        }
    }
}

