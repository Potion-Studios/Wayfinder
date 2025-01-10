package net.potionstudios.wayfinder.neoforge.datagen;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.*;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
        generator.addProvider(event.includeServer(), new LootGenerator(output, lookupProvider));
        generator.addProvider(event.includeServer(), new AdvancementProvider(output, lookupProvider, existingFileHelper, ImmutableList.of(new AdvancementGenerator())));
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
            add("subtitles.entity.wayfinder.shield_hit", "Wayfinder shield hit");
            add("subtitles.entity.wayfinder.shield_break", "Wayfinder shield breaks");


            add(WayfinderItems.WAYFINDER_SPAWN_EGG.get(), "Wayfinder Spawn Egg");
            add(WayfinderBlocks.WAYFINER_HEART.get(), "Wayfinder Heart");

            add("wayfinder.commands.reload.success", "Wayfinder config reloaded");

            add("advancements.wayfinder.so_it_begins.title", "So it begins..");
            add("advancements.wayfinder.so_it_begins.description", "Summon your first Wayfinder");
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
            add(WayfinderSounds.WAYFINDER_SHIELD_HIT.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/shield_hit"))).subtitle(subtitle("entity.wayfinder.shield_hit")));
            add(WayfinderSounds.WAYFINDER_SHIELD_BREAK.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/shield_break"))).subtitle(subtitle("entity.wayfinder.shield_break")));
            add(WayfinderSounds.WAYFINDER_SUMMON.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/summon"))).subtitle(subtitle("entity.wayfinder.summon")));
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

    private static class LootGenerator extends LootTableProvider {
        private LootGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, Collections.emptySet(), ImmutableList.of(
                    new SubProviderEntry(EntityLootGenerator::new, LootContextParamSets.ENTITY)
            ), registries);
        }
    }

    private static class EntityLootGenerator extends EntityLootSubProvider {
        private static final ArrayList<EntityType<?>> knownEntities = new ArrayList<>();
        private EntityLootGenerator(HolderLookup.Provider registries) {
            super(FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        public void generate() {
            add(WayfinderEntities.WAYFINDER.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(Items.BOOK))));
        }

        @Override
        protected void add(@NotNull EntityType<?> entityType, LootTable.@NotNull Builder builder) {
            super.add(entityType, builder);
            knownEntities.add(entityType);
        }

        @Override
        protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
            return knownEntities.stream();
        }
    }

    private static class AdvancementGenerator implements AdvancementProvider.AdvancementGenerator {

        @Override
        public void generate(HolderLookup.@NotNull Provider arg, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
            AdvancementHolder root = Advancement.Builder.advancement()
                    .addCriterion("summon_wayfinder", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(
                            BlockPredicate.Builder.block().of(WayfinderBlocks.WAYFINER_HEART.get())
                    ), ItemPredicate.Builder.item().of(Items.EMERALD)))
                    .display(
                            Items.EMERALD,
                            translateAble("so_it_begins.title"),
                            translateAble("so_it_begins.description"),
                            null, AdvancementType.TASK, true, true, false
                    )
                    .save(consumer, Wayfinder.id(Wayfinder.MOD_ID + "/so_it_begins"), existingFileHelper);
        }

        private static MutableComponent translateAble(String key) {
            return Component.translatable( "advancements." + Wayfinder.MOD_ID +"." + key);
        }
    }

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.PROCESSOR_LIST, pContext -> WayfinderStructureProcessorLists.STRUCTURE_PROCESSOR_LIST_FACTORIES.forEach((structureProcessorListResourceKey, processorListFactory) -> pContext.register(structureProcessorListResourceKey, processorListFactory.generate(pContext.lookup(Registries.PROCESSOR_LIST)))));
}
