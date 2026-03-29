package net.potionstudios.wayfinder.world.entity.wayfinder;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.potionstudios.wayfinder.world.entity.ai.behavior.*;
import net.potionstudios.wayfinder.world.entity.ai.memory.WayfinderMemoryModuleType;

import java.util.List;
import java.util.Set;

public class WayfinderAi {

	protected static List<ActivityData<WayfinderEntity>> getActivities() {
		return ImmutableList.of(
				initCoreActivity(),
				initWorkingActivity(),
				initIdleActivity(),
				initRestActivity(),
				initPanicActivity()
		);
	}

	private static ActivityData<WayfinderEntity> initCoreActivity() {
		return ActivityData.create(
				Activity.CORE,
				0,
				ImmutableList.of(
						new WayfinderPanicTrigger(),
						new MoveToTargetSink(),
						new RestWhenOwnerMissing(),
						new LookAtTargetSink(10, 50)
				)
		);
	}

    private static ActivityData<WayfinderEntity> initWorkingActivity() {
        return ActivityData.create(
                Activity.WORK,
                ImmutableList.of(Pair.of(0, new TravelToJourneyTarget())),
                Set.of(Pair.of(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get(), MemoryStatus.VALUE_PRESENT))
        );
    }

    private static ActivityData<WayfinderEntity> initIdleActivity() {
        return ActivityData.create(
                Activity.IDLE,
                0,
                ImmutableList.of(
						new FollowOwner(),
						new LookAtOwner()
				)
        );
    }

    private static ActivityData<WayfinderEntity> initRestActivity() {
        return ActivityData.create(
                Activity.REST,
                ImmutableList.of(Pair.of(0, new RegenerateShield())),
				Set.of(Pair.of(WayfinderMemoryModuleType.IS_RESTING.get(), MemoryStatus.VALUE_PRESENT))
        );
    }

	private static ActivityData<WayfinderEntity> initPanicActivity() {
		return ActivityData.create(
				Activity.PANIC,
				ImmutableList.of(
						Pair.of(0, new CloseScreenOnPanic()),
						Pair.of(1, new DoNothing(10, 100))
				),
				Set.of(
						Pair.of(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_PRESENT),
						Pair.of(MemoryModuleType.HURT_BY, MemoryStatus.REGISTERED),
						Pair.of(MemoryModuleType.DANGER_DETECTED_RECENTLY, MemoryStatus.REGISTERED)
				)
		);
	}

	protected static void updateActivity(WayfinderEntity wayfinder) {
		wayfinder.getBrain().setActiveActivityToFirstValid(ImmutableList.of(
				Activity.PANIC,
				Activity.REST,
				Activity.WORK,
				Activity.IDLE
		));
	}
}
