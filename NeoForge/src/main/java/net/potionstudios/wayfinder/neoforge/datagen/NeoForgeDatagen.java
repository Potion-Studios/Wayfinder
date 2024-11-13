package net.potionstudios.wayfinder.neoforge.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.potionstudios.wayfinder.Wayfinder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.potionstudios.wayfinder.sounds.WayfinderSounds;
import net.potionstudios.wayfinder.world.entity.WayfinderEntities;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Wayfinder.MOD_ID)
class NeoForgeDatagen {

    @SubscribeEvent
    private static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new LangGenerator(output, "en_us"));
        generator.addProvider(event.includeClient(), new SoundDefinitionsGenerator(output, existingFileHelper));
    }


    private static class LangGenerator extends LanguageProvider {

        public LangGenerator(PackOutput output, String locale) {
            super(output, Wayfinder.MOD_ID, locale);
        }

        @Override
        protected void addTranslations() {
            add(WayfinderEntities.WAYFINDER.get(), "Wayfinder");
            add("subtitles.entity.wayfinder.death", "Wayfinder dies");
        }
    }

    private static class SoundDefinitionsGenerator extends SoundDefinitionsProvider {

        protected SoundDefinitionsGenerator(PackOutput output, ExistingFileHelper helper) {
            super(output, Wayfinder.MOD_ID, helper);
        }

        @Override
        public void registerSounds() {
            add(WayfinderSounds.WAYFINDER_IDLE0.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle0"))).subtitle(subtitle("entity.wayfinder.idle0")));
            add(WayfinderSounds.WAYFINDER_IDLE1.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle1"))).subtitle(subtitle("entity.wayfinder.idle1")));
            add(WayfinderSounds.WAYFINDER_IDLE2.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle2"))).subtitle(subtitle("entity.wayfinder.idle2")));
            add(WayfinderSounds.WAYFINDER_IDLE3.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle3"))).subtitle(subtitle("entity.wayfinder.idle3")));
            add(WayfinderSounds.WAYFINDER_IDLE4.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle4"))).subtitle(subtitle("entity.wayfinder.idle4")));
            add(WayfinderSounds.WAYFINDER_IDLE5.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle5"))).subtitle(subtitle("entity.wayfinder.idle5")));
            add(WayfinderSounds.WAYFINDER_DEATH.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/death"))).subtitle(subtitle("entity.wayfinder.death")));
        }

        private String subtitle(String subtitle) {
            return "subtitles." + subtitle;
        }
    }
}

