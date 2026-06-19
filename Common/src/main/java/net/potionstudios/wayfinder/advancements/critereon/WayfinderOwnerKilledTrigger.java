package net.potionstudios.wayfinder.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.*;
import net.minecraft.server.level.ServerPlayer;
import net.potionstudios.wayfinder.advancements.WayfinderCriteriaTriggers;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class WayfinderOwnerKilledTrigger extends SimpleCriterionTrigger<WayfinderOwnerKilledTrigger.TriggerInstance> {

    @Override
    public @NonNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        super.trigger(player, _ -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
                        )
                        .apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> ownerKilledWayfinder() {
            return WayfinderCriteriaTriggers.WAYFINDER_OWNER_KILLED.get()
                    .createCriterion(new TriggerInstance(Optional.empty()));
        }
    }
}
