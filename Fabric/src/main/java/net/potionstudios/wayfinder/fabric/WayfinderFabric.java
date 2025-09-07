package net.potionstudios.wayfinder.fabric;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.potionstudios.wayfinder.Wayfinder;
import net.fabricmc.api.ModInitializer;
import net.potionstudios.wayfinder.commands.WayfinderCommands;
import net.potionstudios.wayfinder.fabric.data.WayfinderFabricAttachmentData;
import net.potionstudios.wayfinder.network.protocol.WayfinderNetworking;
import net.potionstudios.wayfinder.world.item.WayfinderItems;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;

/**
 * This class is the entrypoint for the mod on the Fabric platform.
 */
public class WayfinderFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Wayfinder.init();
        WayfinderFabricAttachmentData.init();
        Wayfinder.registerEntityAttributes(FabricDefaultAttributeRegistry::register);
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> WayfinderCommands.register(dispatcher::register)));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(group -> group.accept(WayfinderItems.WAYFINDER_SPAWN_EGG.get()));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(group -> group.accept(WayfinderBlocks.WAYFINER_HEART.get()));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(group -> group.accept(WayfinderItems.MUSIC_DISC_SWEET_DREAMS.get()));
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> Wayfinder.onEntityLoad(entity));
        WayfinderNetworking.registerS2CPackets((PayloadTypeRegistry.playS2C()::register));
        WayfinderNetworking.registerC2SPackets((PayloadTypeRegistry.playC2S()::register));
        WayfinderNetworking.registerC2SPackets((type, codec) -> ServerPlayNetworking.registerGlobalReceiver(type, (packet, context) -> packet.receiveMessage(context.player(), context.server()::execute)));
    }
}
