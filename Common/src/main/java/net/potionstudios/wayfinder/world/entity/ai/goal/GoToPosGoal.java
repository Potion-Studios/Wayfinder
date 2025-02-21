package net.potionstudios.wayfinder.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class GoToPosGoal extends Goal {
    private final WayfinderEntity wayfinder;
    private Optional<BlockPos> target;
    private @Nullable LivingEntity owner;
    private final double speed;
    private final float minDistance;

    public GoToPosGoal(WayfinderEntity wayfinder, @Nullable LivingEntity owner, Optional<BlockPos> target, double speed, float minDistance) {
        this.wayfinder = wayfinder;
        this.target = target;
        this.owner = owner;
        this.speed = speed;
        this.minDistance = minDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return !target.isPresent() || wayfinder.distanceToSqr(target.get().getX(), target.get().getY(), target.get().getZ()) > 10;
    }

    @Override
    public void tick() {
        if (target.isPresent() && !wayfinder.unableToMoveToOwner()) {
            double distanceFromOwner = wayfinder.distanceToSqr(owner);
            if (distanceFromOwner <= 200) {
                BlockPos target = this.target.get();
                Vec3 direction = new Vec3(target.getX() - wayfinder.getX(), target.getY() - wayfinder.getY(), target.getZ() - wayfinder.getZ()).normalize();
                Vec3 newPos = new Vec3(target.getX() - direction.x * minDistance, target.getY() - direction.y * minDistance, target.getZ() - direction.z * minDistance);
                wayfinder.getMoveControl().setWantedPosition(newPos.x, newPos.y + .25F, newPos.z, speed);
            }
        } else {
            target = wayfinder.gettargetBiomeBlockPos();
            owner = wayfinder.getOwner();
        }
    }
}
