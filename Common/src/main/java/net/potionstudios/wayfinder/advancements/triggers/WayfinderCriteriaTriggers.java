package net.potionstudios.wayfinder.advancements.triggers;

import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;

import java.util.function.Supplier;

public class WayfinderCriteriaTriggers {

    public static final Supplier<WayfinderOwnerKilledTrigger> WAYFINDER_OWNER_KILLED = register("wayfinder_owner_killed", WayfinderOwnerKilledTrigger::new);
    public static final Supplier<WayfinderGotToBiomeTrigger> WAYFINDER_GOT_TO_BIOME = register("wayfinder_got_to_biome", WayfinderGotToBiomeTrigger::new);
    public static final Supplier<WayfinderHeartBlockTrigger> WAYFINDER_HEART_BLOCK = register("wayfinder_heart_block", WayfinderHeartBlockTrigger::new);

    private static <T extends CriterionTrigger<?>> Supplier<T> register(String id, Supplier<T> supplier) {
        return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.TRIGGER_TYPES, id, supplier);
    }

    public static void criteriaTriggers() {
        Wayfinder.LOGGER.info("Registering Wayfinder Criteria Triggers");
    }
}
