package net.potionstudios.wayfinder.sounds;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;

import java.util.function.Supplier;

public class WayfinderSounds {

    public static final Supplier<SoundEvent> WAYFINDER_IDLE0 = createVariableRangeEvent("wayfinder.idle0");
    public static final Supplier<SoundEvent> WAYFINDER_IDLE1 = createVariableRangeEvent("wayfinder.idle1");
    public static final Supplier<SoundEvent> WAYFINDER_IDLE2 = createVariableRangeEvent("wayfinder.idle2");
    public static final Supplier<SoundEvent> WAYFINDER_IDLE3 = createVariableRangeEvent("wayfinder.idle3");
    public static final Supplier<SoundEvent> WAYFINDER_IDLE4 = createVariableRangeEvent("wayfinder.idle4");
    public static final Supplier<SoundEvent> WAYFINDER_IDLE5 = createVariableRangeEvent("wayfinder.idle5");
    public static final Supplier<SoundEvent> WAYFINDER_DEATH = createVariableRangeEvent("wayfinder.death");
    public static final Supplier<SoundEvent> WAYFINDER_HURT0 = createVariableRangeEvent("wayfinder.hurt0");
    public static final Supplier<SoundEvent> WAYFINDER_HURT1 = createVariableRangeEvent("wayfinder.hurt1");
    public static final Supplier<SoundEvent> WAYFINDER_SHIELD_HIT = createVariableRangeEvent("wayfinder.shield_hit");
    public static final Supplier<SoundEvent> WAYFINDER_SHIELD_BREAK = createVariableRangeEvent("wayfinder.shield_break");
    public static final Supplier<SoundEvent> WAYFINDER_SUMMON = createVariableRangeEvent("wayfinder.summon");


    private static Supplier<SoundEvent> createVariableRangeEvent(String id) {
        return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.SOUND_EVENT, id, () -> SoundEvent.createVariableRangeEvent(Wayfinder.id(id)));
    }

    public static void sounds() {
        Wayfinder.LOGGER.info("Registering Wayfinder Sounds");
    }
}
