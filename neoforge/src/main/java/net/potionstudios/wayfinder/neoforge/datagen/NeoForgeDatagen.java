package net.potionstudios.wayfinder.neoforge.datagen;

import net.potionstudios.wayfinder.Wayfinder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Wayfinder.MOD_ID)
class NeoForgeDatagen {

    @SubscribeEvent
    private static void onGatherData(GatherDataEvent event) {

    }

}
