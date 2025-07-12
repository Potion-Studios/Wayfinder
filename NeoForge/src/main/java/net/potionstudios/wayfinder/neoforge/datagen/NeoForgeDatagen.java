package net.potionstudios.wayfinder.neoforge.datagen;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.Filterable;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ListOperation;
import net.minecraft.world.level.storage.loot.functions.SetWrittenBookPagesFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.*;
import net.potionstudios.wayfinder.Wayfinder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.potionstudios.wayfinder.advancements.critereon.WayfinderGotToBiomeTrigger;
import net.potionstudios.wayfinder.advancements.critereon.WayfinderHeartBlockTrigger;
import net.potionstudios.wayfinder.advancements.critereon.WayfinderOwnerKilledTrigger;
import net.potionstudios.wayfinder.sounds.WayfinderSounds;
import net.potionstudios.wayfinder.tags.WayfinderBiomeTags;
import net.potionstudios.wayfinder.tags.WayfinderEntityTypeTags;
import net.potionstudios.wayfinder.world.entity.WayfinderEntityType;
import net.potionstudios.wayfinder.world.item.WayfinderItems;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;
import net.potionstudios.wayfinder.world.level.block.WayfinderHeartBlock;
import net.potionstudios.wayfinder.world.level.levelgen.structure.WayfinderTemplatePools;
import net.potionstudios.wayfinder.world.level.levelgen.structure.processor.WayfinderStructureProcessorLists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

@EventBusSubscriber(modid = Wayfinder.MOD_ID)
class NeoForgeDatagen {

    @SubscribeEvent
    private static void onGatherData(final GatherDataEvent event) {
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
        generator.addProvider(event.includeServer(), new EntityTypeTagsGenerator(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new BiomeTagsGenerator(output, lookupProvider, existingFileHelper));
    }


    private static class LangGenerator extends LanguageProvider {
        private LangGenerator(PackOutput output, String locale) {
            super(output, Wayfinder.MOD_ID, locale);
        }

        @Override
        protected void addTranslations() {
            addEntityType(WayfinderEntityType.WAYFINDER, "Wayfinder");
            add("subtitles.entity.wayfinder.death", "Wayfinder dies");
            add("subtitles.entity.wayfinder.hurt0", "Wayfinder hurts");
            add("subtitles.entity.wayfinder.hurt1", "Wayfinder hurts");
            add("subtitles.entity.wayfinder.shield_hit", "Wayfinder shield hit");
            add("subtitles.entity.wayfinder.shield_break", "Wayfinder shield breaks");

            addItem(WayfinderItems.WAYFINDER_SPAWN_EGG, "Wayfinder Spawn Egg");
            addBlock(WayfinderBlocks.WAYFINER_HEART, "Wayfinder Heart");

            add("wayfinder.commands.reload.success", "Wayfinder config reloaded");
            add("wayfinder.commands.deaths", "%s has %s Wayfinder deaths");
            add("wayfinder.commands.deaths.reset.other", "Wayfinder deaths reset for %s");
            add("wayfinder.commands.deaths.reset.self", "Reset your Wayfinder deaths");
            add("wayfinder.commands.deaths.list.start", "Wayfinder deaths for all players:");
            add("wayfinder.commands.deaths.list.player", "%s : %s");
            add("wayfinder.commands.deaths.show.other", "%s has %s Wayfinder deaths");
            add("wayfinder.commands.deaths.show.self", "You have %s Wayfinder deaths");
            add("wayfinder.commands.remove.success", "%s's Wayfinder has been removed");
            add("wayfinder.commands.remove.fail", "%s does not have a Wayfinder");
            add("wayfinder.commands.remove.kill.success", "%s's Wayfinder has been removed and killed");
            add("wayfinder.commands.locate.self.success", "Your Wayfinder is at %s");
            add("wayfinder.commands.locate.self.lost", "Your Wayfinder's location could not be found");
            add("wayfinder.commands.locate.self.nowayfinder", "You do not have a Wayfinder");
            add("wayfinder.commands.locate.other.success", "%s's Wayfinder is at %s");
            add("wayfinder.commands.locate.other.lost", "%s's Wayfinder's location could not be currently found");
            add("wayfinder.commands.locate.other.nowayfinder", "%s does not have a Wayfinder");

            add("advancements.wayfinder.a_tale_as_old_as_time.title", "A Tale As Old As Time");
            add("advancements.wayfinder.a_tale_as_old_as_time.description", "Approach a Wayfinder Shrine");
            add("advancements.wayfinder.so_it_begins.title", "So it begins..");
            add("advancements.wayfinder.so_it_begins.description", "Summon your first Wayfinder");
            add("advancements.wayfinder.first_of_many.title", "First of Many");
            add("advancements.wayfinder.first_of_many.description", "Complete your first full journey with a Wayfinder!");
            add("advancements.wayfinder.ultimate_betrayal.title", "Ultimate Betrayal");
            add("advancements.wayfinder.ultimate_betrayal.description", "You should be ashamed of yourself..");

            add("gui.wayfinder.button.sit", "Sit");
            add("gui.wayfinder.button.follow", "Follow");
            add("gui.wayfinder.search", "Search Biomes");
            add("gui.wayfinder.button.search", "Search");
            add("gui.wayfinder.button.stop", "Stop");
            add("gui.wayfinder.description", "Congrats on summoning a Wayfinder! This mod aims in providing the player with a unique lore friendly way to explore! Simply select the biome you want to find and follow the Wayfinder! Be careful though, projectile mobs will try to attack him so you will have to defend him! If your Wayfinder dies, you'll need to go find another shrine and summon him with more emeralds!");

            add("wayfinder.book.story.page1", "Entry 2307,\n" + "I've never seen this before.. I stumbled upon an ancient shrine hidden in the back of this small village. It was covered in vines and moss like no one had touched it for a long time. I was able to make out a faded engraving on the statue..");
            add("wayfinder.book.story.page2", "'When a soul is lost, look to a wayfinder to guide it home'" + "\n" + "I asked the locals about it and one of the librarians directed me to a small corner of the library that looked untouched for decades. It took ages to clean off the spider webs and dust but then");
            add("wayfinder.book.story.page3", "I finally saw it! This shrine belonged to a deity that was worshipped over a thousand years ago. It says here in the text that if you offered it Emeralds then the deity would appear. I asked the Librarian about this and she told me that\n");
            add("wayfinder.book.story.page4", "long ago, the locals used to be nomads and that’s when they found it, the 'Wayfinder'. It would guide their people across countless continents and oceans providing a clear path to safety. I'm running out of ink so I will finish up my thoughts,");
            add("wayfinder.book.story.page5", "I plan on trying to summon this deity with some Emeralds tomorrow morning and I hope that this isn't just some folklore..");
        }
    }

    private static class SoundDefinitionsGenerator extends SoundDefinitionsProvider {
        private SoundDefinitionsGenerator(PackOutput output, ExistingFileHelper helper) {
            super(output, Wayfinder.MOD_ID, helper);
        }

        @Override
        public void registerSounds() {
            add(WayfinderSounds.WAYFINDER_IDLE0, definition().with(sound(Wayfinder.id("entity/wayfinder/idle0"))).subtitle(subtitle("entity.wayfinder.idle0")));
            add(WayfinderSounds.WAYFINDER_IDLE1, definition().with(sound(Wayfinder.id("entity/wayfinder/idle1"))).subtitle(subtitle("entity.wayfinder.idle1")));
            add(WayfinderSounds.WAYFINDER_IDLE2, definition().with(sound(Wayfinder.id("entity/wayfinder/idle2"))).subtitle(subtitle("entity.wayfinder.idle2")));
            add(WayfinderSounds.WAYFINDER_IDLE3, definition().with(sound(Wayfinder.id("entity/wayfinder/idle3"))).subtitle(subtitle("entity.wayfinder.idle3")));
            add(WayfinderSounds.WAYFINDER_IDLE4, definition().with(sound(Wayfinder.id("entity/wayfinder/idle4"))).subtitle(subtitle("entity.wayfinder.idle4")));
            add(WayfinderSounds.WAYFINDER_IDLE5, definition().with(sound(Wayfinder.id("entity/wayfinder/idle5"))).subtitle(subtitle("entity.wayfinder.idle5")));
            add(WayfinderSounds.WAYFINDER_DEATH, definition().with(sound(Wayfinder.id("entity/wayfinder/death"))).subtitle(subtitle("entity.wayfinder.death")));
            add(WayfinderSounds.WAYFINDER_HURT0, definition().with(sound(Wayfinder.id("entity/wayfinder/hurt0"))).subtitle(subtitle("entity.wayfinder.hurt0")));
            add(WayfinderSounds.WAYFINDER_HURT1, definition().with(sound(Wayfinder.id("entity/wayfinder/hurt1"))).subtitle(subtitle("entity.wayfinder.hurt1")));
            add(WayfinderSounds.WAYFINDER_SHIELD_HIT, definition().with(sound(Wayfinder.id("entity/wayfinder/shield_hit"))).subtitle(subtitle("entity.wayfinder.shield_hit")));
            add(WayfinderSounds.WAYFINDER_SHIELD_BREAK, definition().with(sound(Wayfinder.id("entity/wayfinder/shield_break"))).subtitle(subtitle("entity.wayfinder.shield_break")));
            add(WayfinderSounds.WAYFINDER_SUMMON, definition().with(sound(Wayfinder.id("entity/wayfinder/summon"))).subtitle(subtitle("entity.wayfinder.summon")));
            add(WayfinderSounds.WAYFINDER_NO, definition().with(sound(Wayfinder.id("entity/wayfinder/no"))).subtitle(subtitle("entity.wayfinder.no")));
            add(WayfinderSounds.WAYFINDER_SCARED, definition().with(sound(Wayfinder.id("entity/wayfinder/scared"))).subtitle(subtitle("entity.wayfinder.scared")));
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
                    new SubProviderEntry(EntityLootGenerator::new, LootContextParamSets.ENTITY),
                    new SubProviderEntry(AdvancementLootGenerator::new, LootContextParamSets.ADVANCEMENT_REWARD)
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
            add(WayfinderEntityType.WAYFINDER.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(Items.BOOK))));
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

    private static final ResourceKey<LootTable> bookTable = Wayfinder.key(Registries.LOOT_TABLE, "book");

    private static class AdvancementLootGenerator implements LootTableSubProvider {

        private AdvancementLootGenerator(HolderLookup.Provider registries) {}

        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
            output.accept(bookTable, LootTable.lootTable().withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.WRITTEN_BOOK)
                            .apply(SetWrittenBookPagesFunction.simpleBuilder(list ->
                                    new SetWrittenBookPagesFunction(List.of(),
                                            List.of(
                                                    Filterable.passThrough(Component.translatable("wayfinder.book.story.page1")),
                                                    Filterable.passThrough(Component.translatable("wayfinder.book.story.page2")),
                                                    Filterable.passThrough(Component.translatable("wayfinder.book.story.page3")),
                                                    Filterable.passThrough(Component.translatable("wayfinder.book.story.page4"))),
                                            ListOperation.Append.INSTANCE))))));
        }
    }

    private static class AdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.@NotNull Provider arg, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
            AdvancementHolder root = Advancement.Builder.advancement()
                    .addCriterion("near_heartblock", WayfinderHeartBlockTrigger.TriggerInstance.wayFinderHeartBlock())
                    .display(
                            Items.WRITTEN_BOOK,
                            translateAble("a_tale_as_old_as_time.title"),
                            translateAble("a_tale_as_old_as_time.description"),
                            ResourceLocation.withDefaultNamespace("textures/block/moss_block.png"), AdvancementType.TASK, true, false, true
                    )
                    .rewards(AdvancementRewards.Builder.loot(bookTable))
                    .save(consumer, Wayfinder.id(Wayfinder.MOD_ID + "/a_tale_as_old_as_time"), existingFileHelper);

            AdvancementHolder soItBegins = Advancement.Builder.advancement()
                    .addCriterion("summon_wayfinder", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(
                            BlockPredicate.Builder.block().of(WayfinderBlocks.WAYFINER_HEART.get())
                    ), ItemPredicate.Builder.item().of(Tags.Items.GEMS_EMERALD)))
                    .display(
                            Items.EMERALD,
                            translateAble("so_it_begins.title"),
                            translateAble("so_it_begins.description"),
                            null, AdvancementType.TASK, true, true, true
                    )
                    .parent(root)
                    .save(consumer, Wayfinder.id(Wayfinder.MOD_ID + "/so_it_begins"), existingFileHelper);

            Advancement.Builder.advancement()
                        .addCriterion("get_to_biome", WayfinderGotToBiomeTrigger.TriggerInstance.gotToBiome())
                        .display(
                                Items.MAP,
                                translateAble("first_of_many.title"),
                                translateAble("first_of_many.description"),
                                null, AdvancementType.TASK, true, true, true
                        )
                        .parent(soItBegins)
                        .save(consumer, Wayfinder.id(Wayfinder.MOD_ID + "/first_of_many"), existingFileHelper);

            Advancement.Builder.advancement()
                    .addCriterion("kill_wayfinder", WayfinderOwnerKilledTrigger.TriggerInstance.ownerKilledWayfinder())
                    .display(
                            Items.BOOK,
                            translateAble("ultimate_betrayal.title"),
                            translateAble("ultimate_betrayal.description"),
                            null, AdvancementType.CHALLENGE, true, true, true
                    )
                    .parent(soItBegins)
                    .save(consumer, Wayfinder.id(Wayfinder.MOD_ID + "/ultimate_betrayal"), existingFileHelper);
        }

        private static MutableComponent translateAble(String key) {
            return Component.translatable( "advancements." + Wayfinder.MOD_ID +"." + key);
        }
    }

    private static class EntityTypeTagsGenerator extends EntityTypeTagsProvider {
        private EntityTypeTagsGenerator(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
            super(arg, completableFuture, Wayfinder.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(WayfinderEntityTypeTags.SCARES_WAYFINDER).add(EntityType.WITCH, EntityType.GHAST, EntityType.BLAZE, EntityType.WITHER, EntityType.PILLAGER).addTag(EntityTypeTags.SKELETONS);
            tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(WayfinderEntityType.WAYFINDER.get());
            tag(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH).add(WayfinderEntityType.WAYFINDER.get());
        }
    }

    private static class BiomeTagsGenerator extends BiomeTagsProvider {
        private BiomeTagsGenerator(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
            super(arg, completableFuture, Wayfinder.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(WayfinderBiomeTags.WAYFINDER_EXCLUDED);
        }
    }

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.TEMPLATE_POOL, context -> WayfinderTemplatePools.TEMPLATE_POOL_FACTORIES.forEach((templatePoolResourceKey, templatePoolFactory) -> context.register(templatePoolResourceKey, templatePoolFactory.generate(context))))
            .add(Registries.PROCESSOR_LIST, pContext -> WayfinderStructureProcessorLists.STRUCTURE_PROCESSOR_LIST_FACTORIES.forEach((structureProcessorListResourceKey, processorListFactory) -> pContext.register(structureProcessorListResourceKey, processorListFactory.generate(pContext.lookup(Registries.PROCESSOR_LIST)))));
}
