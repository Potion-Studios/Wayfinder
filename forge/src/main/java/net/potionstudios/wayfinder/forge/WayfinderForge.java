package net.potionstudios.wayfinder.forge;

import net.potionstudios.wayfinder.Wayfinder;
import net.minecraftforge.fml.common.Mod;

/**
 * Main class for the mod on the Forge platform.
 */
@Mod(Wayfinder.MOD_ID)
public class WayfinderForge {
    public WayfinderForge() {
        Wayfinder.init();
    }
}
