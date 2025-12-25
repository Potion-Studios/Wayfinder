package net.potionstudios.wayfinder.world.entity.wayfinder;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.potionstudios.wayfinder.world.entity.ai.behavior.FollowOwner;
import net.potionstudios.wayfinder.world.entity.ai.behavior.RegenerateShield;
import net.potionstudios.wayfinder.world.entity.ai.behavior.ResetScared;

import java.util.Set;

public class WayfinderAi {

    protected static Brain<?> makeBrain(Brain<WayfinderEntity> brain) {
        initWorkingActivity(brain);
        initIdleActivity(brain);
        initRestActivity(brain);
		initPanicActivity(brain);
        brain.setDefaultActivity(Activity.IDLE);
        return brain;
    }

    private static void initWorkingActivity(Brain<WayfinderEntity> brain) {
        brain.addActivityWithConditions(
                Activity.WORK,
                ImmutableList.of(Pair.of(0, new MoveToTargetSink())),
                Set.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT))
        );
    }

    private static void initIdleActivity(Brain<WayfinderEntity> brain) {
        brain.addActivity(
                Activity.IDLE,
                0,
                ImmutableList.of(
	                new FollowOwner()
                )
        );
    }

    private static void initRestActivity(Brain<WayfinderEntity> brain) {
        brain.addActivity(
                Activity.REST,
                0,
                ImmutableList.of(new RegenerateShield())
        );
    }

	private static void initPanicActivity(Brain<WayfinderEntity> brain) {
		brain.addActivityWithConditions(
				Activity.PANIC,
				ImmutableList.of(
						Pair.of(0, new DoNothing(10, 100)),
						Pair.of(10, new ResetScared())
				),
				Set.of(
						Pair.of(MemoryModuleType.DANGER_DETECTED_RECENTLY, MemoryStatus.VALUE_PRESENT),
						Pair.of(MemoryModuleType.IS_PANICKING, MemoryStatus.REGISTERED),
						Pair.of(MemoryModuleType.HURT_BY, MemoryStatus.REGISTERED)
				)
		);
	}

	protected static void updateActivity(WayfinderEntity wayfinder) {
		if (wayfinder.isSitting())
			wayfinder.getBrain().setActiveActivityToFirstValid(ImmutableList.of(
					Activity.PANIC,
					Activity.REST
			));
		else wayfinder.getBrain().setActiveActivityToFirstValid(ImmutableList.of(
				Activity.PANIC,
				Activity.WORK,
				Activity.IDLE
		));
	}
}
