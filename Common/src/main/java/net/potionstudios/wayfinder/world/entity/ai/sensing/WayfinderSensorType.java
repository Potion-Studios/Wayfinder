package net.potionstudios.wayfinder.world.entity.ai.sensing;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.MobSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

import java.util.function.Supplier;

/**
 * Sensor Type for the Wayfinder Entity
 * @author Joseph T. McQuigg
 * @see SensorType
 */
public class WayfinderSensorType {

    public static final Supplier<SensorType<MobSensor<WayfinderEntity>>> WAYFINDER_SCARE_DETECTED = register("wayfinder_scare_detected", () -> new MobSensor<>(5, WayfinderEntity::isScaredBy, WayfinderEntity::canScare, MemoryModuleType.DANGER_DETECTED_RECENTLY, 10000));

    public static void sensorType() {
        Wayfinder.LOGGER.info("Registering Wayfinder Sensor Types");
    }

    private static <U extends Sensor<?>> Supplier<SensorType<U>> register(String name, Supplier<U> sensor) {
        return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.SENSOR_TYPE, name, () -> new SensorType<>(sensor));
    }
}
