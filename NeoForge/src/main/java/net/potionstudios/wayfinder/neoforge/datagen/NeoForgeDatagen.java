package net.potionstudios.wayfinder.neoforge.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.potionstudios.wayfinder.Wayfinder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.potionstudios.wayfinder.world.entity.WayfinderEntities;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Wayfinder.MOD_ID)
class NeoForgeDatagen {

    @SubscribeEvent
    private static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new LangGenerator(output, "en_us"));
    }


    private static class LangGenerator extends LanguageProvider {

        public LangGenerator(PackOutput output, String locale) {
            super(output, Wayfinder.MOD_ID, locale);
        }

        @Override
        protected void addTranslations() {
            add(WayfinderEntities.WAYFINDER.get(), "Wayfinder");
        }
    }
}

