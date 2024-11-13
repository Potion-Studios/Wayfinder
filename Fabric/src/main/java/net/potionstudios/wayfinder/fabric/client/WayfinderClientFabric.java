package net.potionstudios.wayfinder.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.potionstudios.wayfinder.client.WayfinderClient;

@Environment(EnvType.CLIENT)
public class WayfinderClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WayfinderClient.registerEntityRenderers(EntityRendererRegistry::register);
    }
}
