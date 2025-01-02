package net.potionstudios.wayfinder.neoforge;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.potionstudios.wayfinder.Wayfinder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.potionstudios.wayfinder.commands.WayfinderReloadCommand;

/**
 * Main class for the mod on the NeoForge platform.
 */
@Mod(Wayfinder.MOD_ID)
public class WayfinderNeoForge {
    public WayfinderNeoForge(IEventBus eventBus) {
        Wayfinder.init();
        NeoForgePlatformHandler.register(eventBus);
        eventBus.addListener((EntityAttributeCreationEvent event) -> Wayfinder.registerEntityAttributes(event::put));
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    /**
     * Registers Commands
     * @see RegisterCommandsEvent
     */
    private void registerCommands(final RegisterCommandsEvent event) {
        WayfinderReloadCommand.register(event.getDispatcher()::register);
    }
}
