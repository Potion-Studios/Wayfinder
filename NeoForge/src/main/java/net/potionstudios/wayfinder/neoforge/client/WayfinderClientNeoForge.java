package net.potionstudios.wayfinder.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.client.WayfinderClient;

/**
 * This class is used to initialize the NeoForge client side of the mod.
 * @see FMLClientSetupEvent
 * @see WayfinderClient
 * @author Joseph T. McQuigg
 */
@Mod(value = Wayfinder.MOD_ID, dist = Dist.CLIENT)
public class WayfinderClientNeoForge {

    public WayfinderClientNeoForge(final IEventBus eventBus) {
        eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> WayfinderClient.registerEntityRenderers(event::registerEntityRenderer));
    }
}
