package net.potionstudios.wayfinder.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class WayfinderHeartBlockTrigger extends SimpleCriterionTrigger<WayfinderHeartBlockTrigger.TriggerInstance> {
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

		public static Criterion<TriggerInstance> wayFinderHeartBlock() {
			return WayfinderCriteriaTriggers.WAYFINDER_HEART_BLOCK.get()
					.createCriterion(new TriggerInstance(Optional.empty()));
		}
	}
}
