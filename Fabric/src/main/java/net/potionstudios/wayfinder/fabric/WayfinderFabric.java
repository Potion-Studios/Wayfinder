package net.potionstudios.wayfinder.fabric;

import net.potionstudios.wayfinder.Wayfinder;
import net.fabricmc.api.ModInitializer;

/**
 * This class is the entrypoint for the mod on the Fabric platform.
 */
public class WayfinderFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Wayfinder.init();
    }
}
