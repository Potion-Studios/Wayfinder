package net.potionstudios.wayfinder.fabric;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.potionstudios.wayfinder.Wayfinder;
import net.fabricmc.api.ModInitializer;
import net.potionstudios.wayfinder.commands.WayfinderReloadCommand;
import net.potionstudios.wayfinder.fabric.data.WayfinderAttachmentData;
import net.potionstudios.wayfinder.world.item.WayfinderItems;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;

/**
 * This class is the entrypoint for the mod on the Fabric platform.
 */
public class WayfinderFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Wayfinder.init();
        WayfinderAttachmentData.init();
        Wayfinder.registerEntityAttributes(FabricDefaultAttributeRegistry::register);
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> WayfinderReloadCommand.register(dispatcher::register)));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(group -> group.accept(WayfinderItems.WAYFINDER_SPAWN_EGG.get()));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(group -> group.accept(WayfinderBlocks.WAYFINER_HEART.get()));
        ServerLifecycleEvents.SERVER_STARTING.register(Wayfinder::serverStart);
    }
}
