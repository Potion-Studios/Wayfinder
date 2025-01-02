package net.potionstudios.wayfinder.fabric;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.potionstudios.wayfinder.Wayfinder;
import net.fabricmc.api.ModInitializer;
import net.potionstudios.wayfinder.commands.WayfinderReloadCommand;

/**
 * This class is the entrypoint for the mod on the Fabric platform.
 */
public class WayfinderFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Wayfinder.init();
        Wayfinder.registerEntityAttributes(FabricDefaultAttributeRegistry::register);
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            WayfinderReloadCommand.register(dispatcher::register);
        }));
    }
}
