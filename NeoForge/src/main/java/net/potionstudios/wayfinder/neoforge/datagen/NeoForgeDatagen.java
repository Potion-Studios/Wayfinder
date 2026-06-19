package net.potionstudios.wayfinder.neoforge.datagen;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.Filterable;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ListOperation;
import net.minecraft.world.level.storage.loot.functions.SetWrittenBookPagesFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.*;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
//import net.potionstudios.biomeswevegone.BiomesWeveGone;
//import net.potionstudios.biomeswevegone.world.item.BWGItems;
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
import net.potionstudios.wayfinder.world.item.jukebox.WayfinderJukeBoxSongs;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;
import net.potionstudios.wayfinder.world.level.block.WayfinderHeartBlock;
import net.potionstudios.wayfinder.data.worldgen.WayfinderProcessorLists;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

@EventBusSubscriber(modid = Wayfinder.MOD_ID)
class NeoForgeDatagen {

    @SubscribeEvent
    private static void onGatherData(final GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        DatapackBuiltinEntriesProvider datapackBuiltinEntriesProvider = new DatapackBuiltinEntriesProvider(output, lookupProvider, BUILDER, Set.of(Wayfinder.MOD_ID));
        generator.addProvider(true, datapackBuiltinEntriesProvider);

        generator.addProvider(true, new LangGenerator(output, "en_us"));
        generator.addProvider(true, new SoundDefinitionsGenerator(output));
        generator.addProvider(true, new ModelGenerator(output));
        generator.addProvider(true, new LootGenerator(output, lookupProvider));
        generator.addProvider(true, new AdvancementGenerator(output, lookupProvider));
        generator.addProvider(true, new EntityTypeTagsGenerator(output, lookupProvider));
        generator.addProvider(true, new BlockTagsGenerator(output, lookupProvider));
        generator.addProvider(true, new ItemTagsGenerator(output, lookupProvider));
        generator.addProvider(true, new BiomeTagsGenerator(output, lookupProvider));
    }


    private static class LangGenerator extends LanguageProvider {
        private LangGenerator(PackOutput output, String locale) {
            super(output, Wayfinder.MOD_ID, locale);
        }

        @Override
        protected void addTranslations() {
            addEntityType(WayfinderEntityType.WAYFINDER, "Wayfinder");
            add("subtitles.entity.wayfinder.idle", "Wayfinder Giggles");
            add("subtitles.entity.wayfinder.death", "Wayfinder dies");
            add("subtitles.entity.wayfinder.hurt0", "Wayfinder hurts");
            add("subtitles.entity.wayfinder.hurt1", "Wayfinder hurts");
            add("subtitles.entity.wayfinder.shield_hit", "Wayfinder shield hit");
            add("subtitles.entity.wayfinder.shield_break", "Wayfinder shield breaks");
            add("subtitles.entity.wayfinder.no", "Wayfinder declines");
            add("subtitles.entity.wayfinder.scared", "");
            add("subtitles.entity.wayfinder.summon", "");

            addItem(WayfinderItems.WAYFINDER_SPAWN_EGG, "Wayfinder Spawn Egg");
            addItem(WayfinderItems.MUSIC_DISC_SWEET_DREAMS, "Music Disc");
            addBlock(WayfinderBlocks.WAYFINER_HEART, "Wayfinder Heart");

            add("item." + Wayfinder.MOD_ID + ".music_disc_sweet_dreams.desc", "AOCAWOL - Sweet Dreams");
            add("jukebox_song." + WayfinderJukeBoxSongs.SWEET_DREAMS.identifier().toLanguageKey(), "AOCAWOL - Sweet Dreams");

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
            add("advancements.wayfinder.boiling_journeys.title", "Boiling Journeys");
            add("advancements.wayfinder.boiling_journeys.description", "Complete a full journey to a Nether Biome with a Wayfinder!");
            add("advancements.wayfinder.familiar_lands.title", "Familiar Lands");
            add("advancements.wayfinder.familiar_lands.description", "Complete a full journey to an End Biome with a Wayfinder!");
            add("advancements.wayfinder.ultimate_betrayal.title", "Ultimate Betrayal");
            add("advancements.wayfinder.ultimate_betrayal.description", "You should be ashamed of yourself..");

            add("advancements.wayfinder.beginner.title", "Beginner Adventurer");
            add("advancements.wayfinder.beginner.description", "Complete 3 full journeys with a Wayfinder that are over 3K blocks");
            add("advancements.wayfinder.novice.title", "Novice Adventurer");
            add("advancements.wayfinder.novice.description", "Complete 5 full journeys with a Wayfinder that are over 3K blocks");
            add("advancements.wayfinder.intermediate.title", "Intermediate Adventurer");
            add("advancements.wayfinder.intermediate.description", "Complete 8 full journeys with a Wayfinder that are over 3K blocks");
            add("advancements.wayfinder.ultimate.title", "Ultimate Adventurer");
            add("advancements.wayfinder.ultimate.description", "Complete 12 full journeys with a Wayfinder that are over 3K blocks");
            add("advancements.wayfinder.boundless_exploration.title", "Boundless Exploration");
            add("advancements.wayfinder.boundless_exploration.description", "Your Wayfinder has their work cut out for them...");

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
        private SoundDefinitionsGenerator(PackOutput output) {
            super(output, Wayfinder.MOD_ID);
        }

        @Override
        public void registerSounds() {
            add(WayfinderSounds.WAYFINDER_IDLE0.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle0"))).subtitle(subtitle("entity.wayfinder.idle")));
            add(WayfinderSounds.WAYFINDER_IDLE1.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle1"))).subtitle(subtitle("entity.wayfinder.idle")));
            add(WayfinderSounds.WAYFINDER_IDLE2.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle2"))).subtitle(subtitle("entity.wayfinder.idle")));
            add(WayfinderSounds.WAYFINDER_IDLE3.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle3"))).subtitle(subtitle("entity.wayfinder.idle")));
            add(WayfinderSounds.WAYFINDER_IDLE4.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle4"))).subtitle(subtitle("entity.wayfinder.idle")));
            add(WayfinderSounds.WAYFINDER_IDLE5.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/idle5"))).subtitle(subtitle("entity.wayfinder.idle")));
            add(WayfinderSounds.WAYFINDER_DEATH.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/death"))).subtitle(subtitle("entity.wayfinder.death")));
            add(WayfinderSounds.WAYFINDER_HURT0.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/hurt0"))).subtitle(subtitle("entity.wayfinder.hurt0")));
            add(WayfinderSounds.WAYFINDER_HURT1.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/hurt1"))).subtitle(subtitle("entity.wayfinder.hurt1")));
            add(WayfinderSounds.WAYFINDER_SHIELD_HIT.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/shield_hit"))).subtitle(subtitle("entity.wayfinder.shield_hit")));
            add(WayfinderSounds.WAYFINDER_SHIELD_BREAK.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/shield_break"))).subtitle(subtitle("entity.wayfinder.shield_break")));
            add(WayfinderSounds.WAYFINDER_SUMMON.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/summon"))).subtitle(subtitle("entity.wayfinder.summon")));
            add(WayfinderSounds.WAYFINDER_NO.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/no"))).subtitle(subtitle("entity.wayfinder.no")));
            add(WayfinderSounds.WAYFINDER_SCARED.get(), definition().with(sound(Wayfinder.id("entity/wayfinder/scared"))).subtitle(subtitle("entity.wayfinder.scared")));
            add(WayfinderSounds.MUSIC_DISC_SWEET_DREAMS.get().value(), definition().with(sound(Wayfinder.id("music/disc/sweet_dreams"))));
        }

        private String subtitle(String subtitle) {
            return "subtitles." + subtitle;
        }
    }

    private static class ModelGenerator extends ModelProvider {
        private ModelGenerator(PackOutput arg) {
            super(arg, Wayfinder.MOD_ID);
        }

        @Override
        protected void registerModels(@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
            itemModels.generateFlatItem(WayfinderItems.MUSIC_DISC_SWEET_DREAMS.get(), ModelTemplates.MUSIC_DISC);
            itemModels.generateFlatItem(WayfinderItems.WAYFINDER_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);

            MultiVariant activated = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(WayfinderBlocks.WAYFINER_HEART.get(), "_activated", TextureMapping.column(Blocks.CHISELED_TUFF)
                    .copyAndUpdate(TextureSlot.FRONT, new Material(Wayfinder.id("block/wayfinder_heart_front_activated")))
                    .copyAndUpdate(TextureSlot.SIDE, new Material(Wayfinder.id("block/wayfinder_heart_side_activated")))
                    .copyAndUpdate(TextureSlot.TOP, new Material(Wayfinder.id("block/wayfinder_heart_top_activated"))), blockModels.modelOutput));

            MultiVariant nonActive = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ORIENTABLE.create(WayfinderBlocks.WAYFINER_HEART.get(), TextureMapping.column(Blocks.CHISELED_TUFF)
                    .copyAndUpdate(TextureSlot.SIDE, new Material(mcLocation("block/chiseled_tuff")))
                    .copyAndUpdate(TextureSlot.FRONT, new Material(Wayfinder.id("block/wayfinder_heart_front"))), blockModels.modelOutput));

            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(WayfinderBlocks.WAYFINER_HEART.get())
                    .with(PropertyDispatch.initial(WayfinderHeartBlock.ACTIVATED)
                            .select(false, nonActive)
                            .select(true, activated))
                    .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
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
        protected void add(@NonNull EntityType<?> entityType, LootTable.@NonNull Builder builder) {
            super.add(entityType, builder);
            knownEntities.add(entityType);
        }

        @Override
        protected @NonNull Stream<EntityType<?>> getKnownEntityTypes() {
            return knownEntities.stream();
        }
    }

    private static final ResourceKey<LootTable> bookTable = Wayfinder.key(Registries.LOOT_TABLE, "book");
    private static final ResourceKey<LootTable> sweetDreamsTable = Wayfinder.key(Registries.LOOT_TABLE, "music_disc/sweet_dreams");

    private static class AdvancementLootGenerator implements LootTableSubProvider {

        private AdvancementLootGenerator(HolderLookup.Provider registries) {}

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
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

            output.accept(sweetDreamsTable, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(WayfinderItems.MUSIC_DISC_SWEET_DREAMS.get()))));
        }
    }

    private static class AdvancementGenerator extends AdvancementProvider {
        private AdvancementGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, ImmutableList.of(new Generator()));
        }

        private static class Generator implements AdvancementSubProvider {
            @Override
            public void generate(HolderLookup.@NonNull Provider registries, @NonNull Consumer<AdvancementHolder> writer) {
                AdvancementHolder root = Advancement.Builder.advancement()
                        .addCriterion("near_heartblock", WayfinderHeartBlockTrigger.TriggerInstance.wayFinderHeartBlock())
                        .display(
                                Items.WRITTEN_BOOK,
                                translateAble("a_tale_as_old_as_time.title"),
                                translateAble("a_tale_as_old_as_time.description"),
                                Identifier.withDefaultNamespace("textures/block/moss_block.png"), AdvancementType.TASK, true, false, true
                        )
                        .rewards(AdvancementRewards.Builder.loot(bookTable))
                        .save(writer, Wayfinder.id(Wayfinder.MOD_ID + "/a_tale_as_old_as_time"));

                AdvancementHolder soItBegins = Advancement.Builder.advancement()
                        .addCriterion("summon_wayfinder", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(registries.lookupOrThrow(Registries.ENTITY_TYPE), WayfinderEntityType.WAYFINDER.get())))
                        .display(
                                Items.EMERALD,
                                translateAble("so_it_begins.title"),
                                translateAble("so_it_begins.description"),
                                null, AdvancementType.TASK, true, true, true
                        )
                        .parent(root)
                        .save(writer, Wayfinder.id(Wayfinder.MOD_ID + "/so_it_begins"));

                AdvancementHolder firstOfMany = Advancement.Builder.advancement()
                        .addCriterion("get_to_biome", WayfinderGotToBiomeTrigger.TriggerInstance.gotToBiome())
                        .display(
                                Items.MAP,
                                translateAble("first_of_many.title"),
                                translateAble("first_of_many.description"),
                                null, AdvancementType.TASK, true, true, false
                        )
                        .parent(soItBegins)
                        .save(writer, Wayfinder.id(Wayfinder.MOD_ID + "/first_of_many"));

                Advancement.Builder.advancement()
                        .addCriterion("get_to_biome_in_nether", WayfinderGotToBiomeTrigger.TriggerInstance.gotToBiome(Level.NETHER))
                        .display(
                                Items.NETHERRACK,
                                translateAble("boiling_journeys.title"),
                                translateAble("boiling_journeys.description"),
                                null, AdvancementType.TASK, true, true, false
                        )
                        .parent(firstOfMany)
                        .save(writer, Wayfinder.id(Wayfinder.MOD_ID + "/boiling_journeys"));

                Advancement.Builder.advancement()
                        .addCriterion("get_to_biome_in_end", WayfinderGotToBiomeTrigger.TriggerInstance.gotToBiome(Level.END))
                        .display(
                                Items.END_STONE,
                                translateAble("familiar_lands.title"),
                                translateAble("familiar_lands.description"),
                                null, AdvancementType.TASK, true, true, false
                        )
                        .parent(firstOfMany)
                        .save(writer, Wayfinder.id(Wayfinder.MOD_ID + "/familiar_lands"));

                AdvancementHolder Beginner = Advancement.Builder.advancement()
                        .addCriterion("3_3k_journeys", WayfinderGotToBiomeTrigger.TriggerInstance.gotToBiome(3000, 3))
                        .display(
                                Items.LEATHER_BOOTS,
                                translateAble("beginner.title"),
                                translateAble("beginner.description"),
                                null, AdvancementType.TASK, true, true, false
                        )
                        .parent(firstOfMany)
                        .save(writer, Wayfinder.id(Wayfinder.MOD_ID + "/beginner"));

                AdvancementHolder Novice = Advancement.Builder.advancement()
                        .addCriterion("5_3k_journeys", WayfinderGotToBiomeTrigger.TriggerInstance.gotToBiome(3000, 5))
                        .display(
                                Items.GOLDEN_BOOTS,
                                translateAble("novice.title"),
                                translateAble("novice.description"),
                                null, AdvancementType.TASK, true, true, false
                        )
                        .parent(Beginner)
                        .save(writer, Wayfinder.id(Wayfinder.MOD_ID + "/novice"));

                AdvancementHolder Intermediate = Advancement.Builder.advancement()
                        .addCriterion("8_3k_journeys", WayfinderGotToBiomeTrigger.TriggerInstance.gotToBiome(3000, 8))
                        .display(
                                Items.DIAMOND_BOOTS,
                                translateAble("intermediate.title"),
                                translateAble("intermediate.description"),
                                null, AdvancementType.TASK, true, true, false
                        )
                        .parent(Novice)
                        .save(writer, Wayfinder.id(Wayfinder.MOD_ID + "/intermediate"));

                Advancement.Builder.advancement()
                        .addCriterion("12_3k_journeys", WayfinderGotToBiomeTrigger.TriggerInstance.gotToBiome(3000, 12))
                        .display(
                                Items.NETHERITE_BOOTS,
                                translateAble("ultimate.title"),
                                translateAble("ultimate.description"),
                                null, AdvancementType.TASK, true, true, false
                        )
                        .rewards(new AdvancementRewards.Builder().addLootTable(sweetDreamsTable).build())
                        .parent(Intermediate)
                        .save(writer, Wayfinder.id(Wayfinder.MOD_ID + "/ultimate"));

                Advancement.Builder.advancement()
                        .addCriterion("kill_wayfinder", WayfinderOwnerKilledTrigger.TriggerInstance.ownerKilledWayfinder())
                        .display(
                                Items.BOOK,
                                translateAble("ultimate_betrayal.title"),
                                translateAble("ultimate_betrayal.description"),
                                null, AdvancementType.CHALLENGE, true, true, true
                        )
                        .parent(soItBegins)
                        .save(writer, Wayfinder.id(Wayfinder.MOD_ID + "/ultimate_betrayal"));

//                Advancement.Builder.advancement()
//                        .addCriterion("bwg_biome", WayfinderGotToBiomeTrigger.TriggerInstance.gotToBiome(BiomesWeveGone.MOD_ID))
//                        .display(
//                                BWGItems.BWG_LOGO.get(),
//                                translateAble("boundless_exploration.title"),
//                                translateAble("boundless_exploration.description"),
//                                null, AdvancementType.CHALLENGE, true, false, true
//                        )
//                        .parent(firstOfMany)
//                        .save(writer, Wayfinder.id(BiomesWeveGone.MOD_ID + "/boundless_exploration"));
            }

            private static MutableComponent translateAble(String key) {
                return Component.translatable( "advancements." + Wayfinder.MOD_ID +"." + key);
            }
        }
    }

    private static class BlockTagsGenerator extends BlockTagsProvider {
        private BlockTagsGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider, Wayfinder.MOD_ID);
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider provider) {

        }
    }

    private static class ItemTagsGenerator extends ItemTagsProvider {
        private ItemTagsGenerator(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(arg, completableFuture, Wayfinder.MOD_ID);
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider provider) {
            tag(Tags.Items.MUSIC_DISCS).add(WayfinderItems.MUSIC_DISC_SWEET_DREAMS.get());
        }
    }

    private static class EntityTypeTagsGenerator extends EntityTypeTagsProvider {
        private EntityTypeTagsGenerator(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(arg, completableFuture, Wayfinder.MOD_ID);
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider provider) {
            tag(WayfinderEntityTypeTags.SCARES_WAYFINDER).add(EntityType.WITCH, EntityType.GHAST, EntityType.BLAZE, EntityType.WITHER, EntityType.PILLAGER).addTag(EntityTypeTags.SKELETONS);
            tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(WayfinderEntityType.WAYFINDER.get());
            tag(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH).add(WayfinderEntityType.WAYFINDER.get());
        }
    }

    private static class BiomeTagsGenerator extends BiomeTagsProvider {
        private BiomeTagsGenerator(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(arg, completableFuture, Wayfinder.MOD_ID);
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider provider) {
            tag(WayfinderBiomeTags.WAYFINDER_EXCLUDED).addTag(Tags.Biomes.HIDDEN_FROM_LOCATOR_SELECTION);
        }
    }

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.PROCESSOR_LIST, pContext -> WayfinderProcessorLists.PROCESSOR_LIST_FACTORIES.forEach((structureProcessorListResourceKey, processorListFactory) -> pContext.register(structureProcessorListResourceKey, processorListFactory.generate(pContext.lookup(Registries.PROCESSOR_LIST)))))
            .add(Registries.JUKEBOX_SONG, pContext -> WayfinderJukeBoxSongs.JUKEBOX_SONG_FACTORIES.forEach((songResourceKey, songFactory) -> pContext.register(songResourceKey, songFactory.generate(pContext))));
}
