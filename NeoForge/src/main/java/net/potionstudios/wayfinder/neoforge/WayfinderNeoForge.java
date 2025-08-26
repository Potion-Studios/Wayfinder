package net.potionstudios.wayfinder.neoforge;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.potionstudios.wayfinder.Wayfinder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.potionstudios.wayfinder.commands.WayfinderCommands;
import net.potionstudios.wayfinder.neoforge.data.WayfinderNeoForgeAttachmentData;
import net.potionstudios.wayfinder.network.protocol.WayfinderNetworking;
import net.potionstudios.wayfinder.world.item.WayfinderItems;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;

/**
 * Main class for the mod on the NeoForge platform.
 */
@Mod(Wayfinder.MOD_ID)
public class WayfinderNeoForge {
    public WayfinderNeoForge(IEventBus eventBus) {
        IEventBus EVENT_BUS = NeoForge.EVENT_BUS;
        Wayfinder.init();
        NeoForgePlatformHandler.register(eventBus);
        eventBus.addListener((EntityAttributeCreationEvent event) -> Wayfinder.registerEntityAttributes(event::put));
        EVENT_BUS.addListener((RegisterCommandsEvent event) -> WayfinderCommands.register(event.getDispatcher()::register));
        eventBus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
                event.accept(WayfinderItems.WAYFINDER_SPAWN_EGG.get());
            else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
                event.accept(WayfinderBlocks.WAYFINER_HEART.get());
        });
        EVENT_BUS.addListener((EntityJoinLevelEvent event) -> Wayfinder.onEntityLoad(event.getEntity()));
        EVENT_BUS.addListener((ServerAboutToStartEvent event) -> Wayfinder.onServerStart(event.getServer()));
        eventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            final PayloadRegistrar registrar = event.registrar(Wayfinder.MOD_ID).executesOn(HandlerThread.NETWORK);
            WayfinderNetworking.registerS2CPackets((type, codec) -> registrar.playToClient(type, codec, (packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork)));
            WayfinderNetworking.registerC2SPackets((type, codec) -> registrar.playToServer(type, codec, (packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork)));
        });
        WayfinderNeoForgeAttachmentData.init(eventBus);
    }
}
