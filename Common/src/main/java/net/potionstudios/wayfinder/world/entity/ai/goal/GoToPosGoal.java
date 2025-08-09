package net.potionstudios.wayfinder.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
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
    private int teleportWaitTime;
    private int distance;

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
    public void start() {
        this.timeToRecalcPath = 0;
        this.teleportWaitTime = Wayfinder.CONFIG.wayfinder.TELEPORT_TO_OWNER.value() * 20;
        this.distance = wayfinder.blockPosition().distManhattan(target.get());
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        wayfinder.setTargetBlockPos(Optional.empty());
    }

    @Override
    public void tick() {
        if (owner.blockPosition().distManhattan(wayfinder.blockPosition()) >= 200) {
            navigation.stop();
            timeToRecalcPath = 0;
            teleportWaitTime--;
            if (teleportWaitTime <= 0) wayfinder.tryToTeleportToOwner();
        } else if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.navigation.isStuck() || this.navigation.isDone())
                this.navigation.moveTo(target.get().getX(), target.get().getY(), target.get().getZ(), speed);
            teleportWaitTime = Wayfinder.CONFIG.wayfinder.TELEPORT_TO_OWNER.value() * 20;
        }

        Level level = wayfinder.level();
        Holder<Biome> biome = level.getBiome(wayfinder.blockPosition());

        if (biome.is(level.getBiome(target.get()))) {
            ServerPlayer owner = (ServerPlayer) this.owner;
            if (distance >= 3000) PlatformHandler.PLATFORM_HANDLER.increment3kJourneys(owner);
            WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get().trigger(owner, biome.unwrapKey().orElseThrow(), level.dimension(), distance);
            wayfinder.playSound(SoundEvents.AMETHYST_BLOCK_RESONATE);
            level.broadcastEntityEvent(wayfinder, (byte) 12);
            wayfinder.incrementCompletedJourneys();
            stop();
        }
    }
}
