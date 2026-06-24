package net.potionstudios.wayfinder.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jspecify.annotations.NonNull;

public class WayfinderPanicTrigger extends Behavior<WayfinderEntity> {
	public WayfinderPanicTrigger() {
		super(ImmutableMap.of());
	}

	@Override
	protected boolean canStillUse(@NonNull ServerLevel level, @NonNull WayfinderEntity entity, long gameTime) {
		return dangerNear(entity) || isHurt(entity);
	}

	@Override
	protected void start(@NonNull ServerLevel level, @NonNull WayfinderEntity entity, long gameTime) {
		if (isHurt(entity) || dangerNear(entity)) {
			Brain<WayfinderEntity> brain = entity.getBrain();
			if (!brain.isActive(Activity.PANIC)) {
				brain.eraseMemory(MemoryModuleType.WALK_TARGET);
			}
			brain.setMemoryWithExpiry(MemoryModuleType.IS_PANICKING, true, 200L);
		}
	}

	public static boolean dangerNear(LivingEntity entity) {
		return entity.getBrain().hasMemoryValue(MemoryModuleType.DANGER_DETECTED_RECENTLY);
	}

	private static boolean isHurt(LivingEntity entity) {
		return entity.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
	}
}
