package net.potionstudios.wayfinder.forge.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.potionstudios.wayfinder.client.WayfinderClient;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class WayfinderClientForge {

    /**
     * Initializes the client side of the Forge mod.
     * @param eventBus The event bus to register the client side of the mod to.
     */
    public static void init(IEventBus eventBus) {
        eventBus.addListener((Consumer<EntityRenderersEvent.RegisterRenderers>) event -> WayfinderClient.registerEntityRenderers(event::registerEntityRenderer));
    }
}
