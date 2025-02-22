package net.potionstudios.wayfinder.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WayfinderGotToBiomeTrigger extends SimpleCriterionTrigger<WayfinderGotToBiomeTrigger.TriggerInstance> {
    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(@NotNull ServerPlayer player) {
        super.trigger(player, triggerInstance -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
                        )
                        .apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> gotToBiome() {
            return WayfinderCriteriaTriggers.WAYFINDER_GOT_TO_BIOME.get()
                    .createCriterion(new TriggerInstance(Optional.empty()));
        }
    }
}
