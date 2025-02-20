package net.potionstudios.wayfinder.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

import java.util.EnumSet;

public class GoToPosGoal extends Goal {
    private final WayfinderEntity wayfinder;

    public GoToPosGoal(WayfinderEntity wayfinder) {
        this.wayfinder = wayfinder;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public void start() {
        BlockPos pos = wayfinder.targetBlockPos().get();
        wayfinder.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
    }

    @Override
    public void stop() {
        wayfinder.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return !wayfinder.getNavigation().isDone();
    }

    @Override
    public boolean canUse() {
        return !wayfinder.isSitting() && wayfinder.hasTarget();
    }
}
