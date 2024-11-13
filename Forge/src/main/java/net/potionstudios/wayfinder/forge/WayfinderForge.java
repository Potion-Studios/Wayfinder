package net.potionstudios.wayfinder.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.potionstudios.wayfinder.Wayfinder;
import net.minecraftforge.fml.common.Mod;

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
        ForgePlatformHandler.register(MOD_BUS);
        MOD_BUS.addListener((Consumer<EntityAttributeCreationEvent>) event -> Wayfinder.registerEntityAttributes(event::put));
    }
}
