package net.potionstudios.wayfinder.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WayfinderGotToBiomeTrigger extends SimpleCriterionTrigger<WayfinderGotToBiomeTrigger.TriggerInstance> {
    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(@NotNull ServerPlayer player, ResourceKey<Biome> biome, ResourceKey<Level> level, int distance) {
        super.trigger(player, triggerInstance -> triggerInstance.matches(biome, level, distance));
    }

    public void triggerOver3000(@NotNull ServerPlayer player, ResourceKey<Biome> biome, ResourceKey<Level> level) {
        super.trigger(player, triggerInstance -> triggerInstance.matches(biome, level, 3000));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ResourceKey<Biome>> biome, Optional<ResourceKey<Level>> level, Optional<Integer> distance) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                            ResourceKey.codec(Registries.BIOME).optionalFieldOf("biome").forGetter(TriggerInstance::biome),
                            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("level").forGetter(TriggerInstance::level),
                            Codec.INT.optionalFieldOf("distance").forGetter(TriggerInstance::distance)
                        )
                        .apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> gotToBiome() {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static Criterion<TriggerInstance> gotToBiome(ResourceKey<Biome> biome, ResourceKey<Level> level, int distance) {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(biome), Optional.of(level), Optional.of(distance)));
        }

        public static Criterion<TriggerInstance> gotToBiome(ResourceKey<Level> level, int distance) {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(level), Optional.of(distance)));
        }

        public boolean matches(ResourceKey<Biome> biome, ResourceKey<Level> level, int distance) {
            return this.biome.map(b -> b.equals(biome)).orElse(true) &&
                   this.level.map(l -> l.equals(level)).orElse(true) &&
                   this.distance.map(d -> d <= distance).orElse(true);
        }

        public static Criterion<TriggerInstance> gotToBiome(ResourceKey<Level> level) {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(level), Optional.empty()));
        }
    }
}
