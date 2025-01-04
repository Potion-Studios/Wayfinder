package net.potionstudios.wayfinder.neoforge.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.potionstudios.wayfinder.Wayfinder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.potionstudios.wayfinder.sounds.WayfinderSounds;
import net.potionstudios.wayfinder.world.entity.WayfinderEntities;
import net.potionstudios.wayfinder.world.item.WayfinderItems;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;
import net.potionstudios.wayfinder.world.level.block.WayfinderHeartBlock;
import net.potionstudios.wayfinder.world.level.levelgen.structure.processor.WayfinderStructureProcessorLists;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Wayfinder.MOD_ID)
class NeoForgeDatagen {

    @SubscribeEvent
    private static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        DatapackBuiltinEntriesProvider datapackBuiltinEntriesProvider = new DatapackBuiltinEntriesProvider(output, lookupProvider, BUILDER, Set.of(Wayfinder.MOD_ID));
        generator.addProvider(event.includeServer(), datapackBuiltinEntriesProvider);

        generator.addProvider(event.includeClient(), new LangGenerator(output, "en_us"));
        generator.addProvider(event.includeClient(), new SoundDefinitionsGenerator(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new BlockModelGenerator(output, existingFileHelper));
    }


    private static class LangGenerator extends LanguageProvider {

        private LangGenerator(PackOutput output, String locale) {
            super(output, Wayfinder.MOD_ID, locale);
        }

        @Override
        protected void addTranslations() {
            add(WayfinderEntities.WAYFINDER.get(), "Wayfinder");
            add("subtitles.entity.wayfinder.death", "Wayfinder dies");
            add("subtitles.entity.wayfinder.hurt0", "Wayfinder hurts");
            add("subtitles.entity.wayfinder.hurt1", "Wayfinder hurts");

            add(WayfinderItems.WAYFINDER_SPAWN_EGG.get(), "Wayfinder Spawn Egg");
            add(WayfinderBlocks.WAYFINER_HEART.get(), "Wayfinder Heart");

            add("wayfinder.commands.reload.success", "Wayfinder config reloaded");
        }
    }

    private static class SoundDefinitionsGenerator extends SoundDefinitionsProvider {

        private SoundDefinitionsGenerator(PackOutput output, ExistingFileHelper helper) {
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
            add(WayfinderSounds.WAYFINDER_HURT0.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/hurt0"))).subtitle(subtitle("entity.wayfinder.hurt0")));
            add(WayfinderSounds.WAYFINDER_HURT1.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/hurt1"))).subtitle(subtitle("entity.wayfinder.hurt1")));
        }

        private String subtitle(String subtitle) {
            return "subtitles." + subtitle;
        }
    }

    private static class ItemModelGenerator extends ItemModelProvider {

        private ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, Wayfinder.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            spawnEggItem(WayfinderItems.WAYFINDER_SPAWN_EGG.get());
        }
    }

    private static class BlockModelGenerator extends BlockStateProvider {

        private BlockModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, Wayfinder.MOD_ID, existingFileHelper);
        }


        @Override
        protected void registerStatesAndModels() {
            BlockModelBuilder activated = models().orientable("wayfinder_heart_activated", Wayfinder.id("block/wayfinder_heart_side_activated"), Wayfinder.id("block/wayfinder_heart_front_activated"), Wayfinder.id("block/wayfinder_heart_top_activated"));
            BlockModelBuilder normal = models().orientable("wayfinder_heart", mcLoc("block/chiseled_tuff"), Wayfinder.id("block/wayfinder_heart_front"), mcLoc("block/chiseled_tuff_top"));

            getVariantBuilder(WayfinderBlocks.WAYFINER_HEART.get()).forAllStates(blockState -> {
                BlockModelBuilder model = blockState.getValue(WayfinderHeartBlock.ACTIVATED) ? activated : normal;
                return switch (blockState.getValue(WayfinderHeartBlock.FACING)) {
                    case EAST -> ConfiguredModel.builder().rotationY(90).modelFile(model).build();
                    case WEST -> ConfiguredModel.builder().rotationY(270).modelFile(model).build();
                    case SOUTH -> ConfiguredModel.builder().rotationY(180).modelFile(model).build();
                    default -> ConfiguredModel.builder().modelFile(model).build();
                };
            });
            simpleBlockItem(WayfinderBlocks.WAYFINER_HEART.get(), normal);
        }
    }

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.PROCESSOR_LIST, pContext -> WayfinderStructureProcessorLists.STRUCTURE_PROCESSOR_LIST_FACTORIES.forEach((structureProcessorListResourceKey, processorListFactory) -> pContext.register(structureProcessorListResourceKey, processorListFactory.generate(pContext.lookup(Registries.PROCESSOR_LIST)))));
}
