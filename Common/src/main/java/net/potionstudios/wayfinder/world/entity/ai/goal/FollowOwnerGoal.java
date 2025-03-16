package net.potionstudios.wayfinder.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {
    private final WayfinderEntity wayfinder;
    @Nullable private LivingEntity owner;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float startDistance;
    private final float stopDistance;

    public FollowOwnerGoal(WayfinderEntity wayfinder, double speedModifier, float startDistance, float stopDistance) {
        this.wayfinder = wayfinder;
        this.speedModifier = speedModifier;
        this.navigation = wayfinder.getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = wayfinder.getOwner();
        if (livingEntity == null) {
            return false;
        } else if (this.wayfinder.unableToMoveToOwner()) {
            return false;
        } else if (this.wayfinder.distanceToSqr(livingEntity) < (this.startDistance * this.startDistance)){
            return false;
        } else {
            this.owner = livingEntity;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else {
            return !this.wayfinder.unableToMoveToOwner() && !(this.wayfinder.distanceToSqr(this.owner) <= (double) (this.stopDistance * this.stopDistance));
        }
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
    }

    @Override
    public void tick() {
        boolean bl = this.wayfinder.shouldTryTeleportToOwner();
        if (!bl) {
            this.wayfinder.getLookControl().setLookAt(this.owner, 10.0F, (float)this.wayfinder.getMaxHeadXRot());
        }

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (bl) {
                this.wayfinder.tryToTeleportToOwner();
            } else {
                this.navigation.moveTo(this.owner, this.speedModifier);
            }
        }
    }
}

