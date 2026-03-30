package net.potionstudios.wayfinder.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.potionstudios.wayfinder.client.WayfinderClient;
import net.potionstudios.wayfinder.network.protocol.WayfinderNetworking;

@Environment(EnvType.CLIENT)
public class WayfinderClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WayfinderClient.registerEntityRenderers(EntityRenderers::register);
        WayfinderNetworking.registerS2CPackets((type, codec) -> ClientPlayNetworking.registerGlobalReceiver(type, (packet, context) -> packet.receiveMessage(context.player(), context.client()::execute)));
    }
}
