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

    public void trigger(@NotNull ServerPlayer player, ResourceKey<Biome> biome, ResourceKey<Level> level) {
        super.trigger(player, triggerInstance -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ResourceKey<Biome>> biome, Optional<ResourceKey<Level>> level) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                            ResourceKey.codec(Registries.BIOME).optionalFieldOf("biome").forGetter(TriggerInstance::biome),
                            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("level").forGetter(TriggerInstance::level)
                        )
                        .apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> gotToBiome() {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static Criterion<TriggerInstance> gotToBiome(ResourceKey<Biome> biome, ResourceKey<Level> level) {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(biome), Optional.of(level)));
        }
    }
}
