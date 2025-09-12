package net.potionstudios.wayfinder.world.entity.wayfinder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.schedule.Activity;
import net.potionstudios.wayfinder.world.entity.ai.behavior.FollowOwner;

public class WayfinderAi {

    protected static Brain<?> makeBrain(Brain<WayfinderEntity> brain) {
        initCoreActivity(brain);
        initWorkingActivity(brain);
        initIdleActivity(brain);
        initRestActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.CORE);
        return brain;
    }

    private static void initCoreActivity(Brain<WayfinderEntity> brain) {
        brain.addActivity(
                Activity.CORE,
                0,
                ImmutableList.of(
                    new FollowOwner()
                )
        );
    }

    private static void initWorkingActivity(Brain<WayfinderEntity> brain) {
        brain.addActivity(
                Activity.WORK,
                0,
                ImmutableList.of(
                    new MoveToTargetSink()
                )
        );
    }

    private static void initIdleActivity(Brain<WayfinderEntity> brain) {
        brain.addActivity(
                Activity.IDLE,
                0,
                ImmutableList.of()
        );
    }

    private static void initRestActivity(Brain<WayfinderEntity> brain) {
        brain.addActivity(
                Activity.REST,
                0,
                ImmutableList.of()
        );
    }
}
