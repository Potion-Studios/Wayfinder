package net.potionstudios.wayfinder.world.entity.wayfinder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.potionstudios.wayfinder.world.entity.ai.behavior.*;
import net.potionstudios.wayfinder.world.entity.ai.memory.WayfinderMemoryModuleType;

import java.util.Set;

public class WayfinderAi {

    protected static Brain<?> makeBrain(Brain<WayfinderEntity> brain) {
		initCoreActivity(brain);
        initWorkingActivity(brain);
        initIdleActivity(brain);
        initRestActivity(brain);
		initPanicActivity(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        return brain;
    }

	private static void initCoreActivity(Brain<WayfinderEntity> brain) {
		brain.addActivity(
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

    private static void initWorkingActivity(Brain<WayfinderEntity> brain) {
        brain.addActivityWithConditions(
                Activity.WORK,
                ImmutableList.of(Pair.of(0, new TravelToJourneyTarget())),
                Set.of(Pair.of(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get(), MemoryStatus.VALUE_PRESENT))
        );
    }

    private static void initIdleActivity(Brain<WayfinderEntity> brain) {
        brain.addActivity(
                Activity.IDLE,
                0,
                ImmutableList.of(
						new FollowOwner(),
						new LookAtOwner()
				)
        );
    }

    private static void initRestActivity(Brain<WayfinderEntity> brain) {
        brain.addActivityWithConditions(
                Activity.REST,
                ImmutableList.of(Pair.of(0, new RegenerateShield())),
				Set.of(Pair.of(WayfinderMemoryModuleType.IS_RESTING.get(), MemoryStatus.VALUE_PRESENT))
        );
    }

	private static void initPanicActivity(Brain<WayfinderEntity> brain) {
		brain.addActivityWithConditions(
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
