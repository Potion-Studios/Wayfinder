package net.potionstudios.wayfinder.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.potionstudios.wayfinder.Wayfinder;
import net.minecraftforge.fml.common.Mod;
import net.potionstudios.wayfinder.forge.client.WayfinderClientForge;

import java.util.function.Consumer;

/**
 * Main class for the mod on the Forge platform.
 */
@Mod(Wayfinder.MOD_ID)
public class WayfinderForge {
    public WayfinderForge(final FMLJavaModLoadingContext context) {
        IEventBus MOD_BUS = context.getModEventBus();
        IEventBus EVENT_BUS = MinecraftForge.EVENT_BUS;
        Wayfinder.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> WayfinderClientForge.init(MOD_BUS));
        ForgePlatformHandler.register(MOD_BUS);
        MOD_BUS.addListener((Consumer<EntityAttributeCreationEvent>) event -> Wayfinder.registerEntityAttributes(event::put));
    }
}
