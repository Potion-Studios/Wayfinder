package net.potionstudios.wayfinder.world.entity.ai.goal;

import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.biome.Biome;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

import java.util.EnumSet;

public class GoToBiomeGoal extends Goal {
    private final WayfinderEntity wayfinder;
    private final Predicate<Holder<Biome>> biome;

    public GoToBiomeGoal(WayfinderEntity wayfinder, Predicate<Holder<Biome>> biome) {
        this.wayfinder = wayfinder;
        this.biome = biome;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public void start() {
        Wayfinder.LOGGER.info("Starting GoToBiomeGoal");
        if (wayfinder.level().isClientSide() || wayfinder.getNavigation().isInProgress()) return;
        Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
        Pair<BlockPos, Holder<Biome>> pair = ((ServerLevel) wayfinder.level()).findClosestBiome3d(biome, wayfinder.blockPosition(), Wayfinder.CONFIG.MAX_SEARCH_DISTANCE_IN_CHUNKS, 32, 64);
        stopwatch.stop();
        if (pair == null) {
            throw new IllegalStateException("Could not find biome " + biome);
        }
        boolean b = wayfinder.getNavigation().moveTo(pair.getFirst().getX(), pair.getFirst().getY(), pair.getFirst().getZ(), 3.0);
        Wayfinder.LOGGER.info(Boolean.toString(b) + "Navigation started?");
        Wayfinder.LOGGER.info("Found biome " + pair.getSecond().getRegisteredName() + " in " + stopwatch.elapsed());
        Wayfinder.LOGGER.info(Boolean.toString(wayfinder.getNavigation().isInProgress()));
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
        return true;
    }
}
