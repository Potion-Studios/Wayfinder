package net.potionstudios.wayfinder.neoforge;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.potionstudios.wayfinder.Wayfinder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import java.util.function.Consumer;

/**
 * Main class for the mod on the NeoForge platform.
 */
@Mod(Wayfinder.MOD_ID)
public class WayfinderNeoForge {
    public WayfinderNeoForge(IEventBus eventBus) {
        Wayfinder.init();
        eventBus.addListener((Consumer<EntityAttributeCreationEvent>) event -> Wayfinder.registerEntityAttributes(event::put));
    }
}
