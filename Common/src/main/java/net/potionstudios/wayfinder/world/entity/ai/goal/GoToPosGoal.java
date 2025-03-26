package net.potionstudios.wayfinder.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.potionstudios.wayfinder.advancements.critereon.WayfinderCriteriaTriggers;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class GoToPosGoal extends Goal {
    private final WayfinderEntity wayfinder;
    private Optional<BlockPos> target;
    private @Nullable LivingEntity owner;
    private final double speed;
    private final PathNavigation navigation;
    private int timeToRecalcPath;

    public GoToPosGoal(WayfinderEntity wayfinder, @Nullable LivingEntity owner, Optional<BlockPos> target, double speed) {
        this.wayfinder = wayfinder;
        this.target = target;
        this.owner = owner;
        this.speed = speed;
        this.navigation = wayfinder.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = wayfinder.getOwner();
        Optional<BlockPos> target = wayfinder.getTargetBiomeBlockPos();
        if (livingEntity == null || target.isEmpty()) {
            return false;
        } else {
            this.owner = livingEntity;
            this.target = target;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && wayfinder.distanceToSqr(target.get().getX(), target.get().getY(), target.get().getZ()) > 10;
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
        if (owner.distanceToSqr(wayfinder) >= 200) {
            navigation.stop();
            timeToRecalcPath = 0;
        } else if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.navigation.moveTo(target.get().getX(), target.get().getY(), target.get().getZ(), speed);
        }

        if (wayfinder.distanceToSqr(target.get().getX(), target.get().getY(), target.get().getZ()) < 3) {
            WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get().trigger((ServerPlayer) owner);
            stop();
        }
    }
}
