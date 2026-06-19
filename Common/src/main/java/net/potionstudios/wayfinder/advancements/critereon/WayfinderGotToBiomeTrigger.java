package net.potionstudios.wayfinder.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.advancements.WayfinderCriteriaTriggers;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WayfinderGotToBiomeTrigger extends SimpleCriterionTrigger<WayfinderGotToBiomeTrigger.TriggerInstance> {
    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(@NotNull ServerPlayer player, ResourceKey<Biome> biome, ResourceKey<Level> level, int distance) {
        super.trigger(player, triggerInstance -> triggerInstance.matches(player, biome, level, distance));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ResourceKey<Biome>> biome, Optional<ResourceKey<Level>> level, Optional<Integer> distance, Optional<Integer> threeKJourneys, Optional<String> modid) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                            ResourceKey.codec(Registries.BIOME).optionalFieldOf("biome").forGetter(TriggerInstance::biome),
                            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("level").forGetter(TriggerInstance::level),
                            Codec.INT.optionalFieldOf("distance").forGetter(TriggerInstance::distance),
                            Codec.INT.optionalFieldOf("three_k_journeys").forGetter(TriggerInstance::threeKJourneys),
                            Codec.STRING.optionalFieldOf("modid").forGetter(TriggerInstance::modid)
                        )
                        .apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> gotToBiome() {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static Criterion<TriggerInstance> gotToBiome(ResourceKey<Level> level) {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(level), Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static Criterion<TriggerInstance> gotToBiome(ResourceKey<Biome> biome, ResourceKey<Level> level, int distance) {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(biome), Optional.of(level), Optional.of(distance), Optional.empty(), Optional.empty()));
        }

        public static Criterion<TriggerInstance> gotToBiome(ResourceKey<Level> level, int distance) {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(level), Optional.of(distance), Optional.empty(), Optional.empty()));
        }

        public static Criterion<TriggerInstance> gotToBiome(int distance, int threeKJourneys) {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(distance), Optional.of(threeKJourneys), Optional.empty()));
        }

        public static Criterion<TriggerInstance> gotToBiome(String modid) {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(modid)));
        }

        public boolean matches(ServerPlayer player, ResourceKey<Biome> biome, ResourceKey<Level> level, int distance) {
            return this.biome.map(b -> b.equals(biome)).orElse(true) &&
                   this.level.map(l -> l.equals(level)).orElse(true) &&
                   this.distance.map(d -> d <= distance).orElse(true) &&
                   this.threeKJourneys.map(t -> t == PlatformHandler.PLATFORM_HANDLER.get3kJourneys(player)).orElse(true) &&
                    this.modid.map(m -> biome.identifier().getNamespace().equals(m)).orElse(true);
        }
    }
}
