package net.potionstudios.wayfinder.world.entity.ai.memory;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Memory Module Type for the Wayfinder Entity
 * @author Joseph T. McQuigg
 * @see MemoryModuleType
 */
public class WayfinderMemoryModuleType {

    public static final Supplier<MemoryModuleType<Unit>> IS_RESTING = register("is_resting", Optional.of(Unit.CODEC));

    private static <U> Supplier<MemoryModuleType<U>> register(String name, Optional<Codec<U>> codec) {
        return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.MEMORY_MODULE_TYPE, name, () -> new MemoryModuleType<>(codec));
    }

    public static void memoryModuleTypes() {
        Wayfinder.LOGGER.info("Registering Wayfinder Memory Module Types");
    }
}
