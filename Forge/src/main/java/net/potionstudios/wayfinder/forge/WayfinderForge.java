package net.potionstudios.wayfinder.forge;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.potionstudios.wayfinder.Wayfinder;
import net.minecraftforge.fml.common.Mod;
import net.potionstudios.wayfinder.commands.WayfinderReloadCommand;
import net.potionstudios.wayfinder.forge.client.WayfinderClientForge;
import net.potionstudios.wayfinder.world.item.WayfinderItems;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;

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
        MOD_BUS.addListener((EntityAttributeCreationEvent event) -> Wayfinder.registerEntityAttributes(event::put));
        EVENT_BUS.addListener((RegisterCommandsEvent event) -> WayfinderReloadCommand.register(event.getDispatcher()::register));
        MOD_BUS.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
                event.accept(WayfinderItems.WAYFINDER_SPAWN_EGG.get());
            else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
                event.accept(WayfinderBlocks.WAYFINER_HEART.get());
        });
    }
}
