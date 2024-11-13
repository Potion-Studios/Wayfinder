package net.potionstudios.wayfinder.neoforge;

import net.potionstudios.wayfinder.Wayfinder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Main class for the mod on the NeoForge platform.
 */
@Mod(Wayfinder.MOD_ID)
public class WayfinderNeoForge {
    public WayfinderNeoForge(IEventBus eventBus) {
        Wayfinder.init();
    }
}
