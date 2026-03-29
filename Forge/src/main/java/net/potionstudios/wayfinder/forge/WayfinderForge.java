package net.potionstudios.wayfinder.forge;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.potionstudios.wayfinder.Wayfinder;
import net.minecraftforge.fml.common.Mod;
import net.potionstudios.wayfinder.commands.WayfinderCommands;
import net.potionstudios.wayfinder.forge.client.WayfinderClientForge;
import net.potionstudios.wayfinder.forge.networking.ForgeNetworking;
import net.potionstudios.wayfinder.world.item.WayfinderItems;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;

/**
 * Main class for the mod on the Forge platform.
 */
@Mod(Wayfinder.MOD_ID)
public class WayfinderForge {
    public WayfinderForge(final FMLJavaModLoadingContext context) {
        BusGroup modBusGroup = context.getModBusGroup();
        Wayfinder.init();
        if (FMLEnvironment.dist.isClient()) WayfinderClientForge.init(modBusGroup);
        ForgePlatformHandler.register(modBusGroup);
        EntityAttributeCreationEvent.BUS.addListener((EntityAttributeCreationEvent event) -> Wayfinder.registerEntityAttributes(event::put));
        RegisterCommandsEvent.BUS.addListener((RegisterCommandsEvent event) -> WayfinderCommands.register(event.getDispatcher()::register));
        BuildCreativeModeTabContentsEvent.BUS.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
                event.accept(WayfinderItems.WAYFINDER_SPAWN_EGG.get());
            else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
                event.accept(WayfinderBlocks.WAYFINER_HEART.get());
            else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
                event.accept(WayfinderItems.MUSIC_DISC_SWEET_DREAMS.get());
        });
        EntityJoinLevelEvent.BUS.addListener((EntityJoinLevelEvent event) -> Wayfinder.onEntityLoad(event.getEntity()));
        ForgeNetworking.init();
        ServerAboutToStartEvent.BUS.addListener((ServerAboutToStartEvent event) -> Wayfinder.onServerStart(event.getServer()));
    }
}
