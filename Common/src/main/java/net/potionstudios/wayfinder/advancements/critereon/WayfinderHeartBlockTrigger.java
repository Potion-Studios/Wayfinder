package net.potionstudios.wayfinder.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.potionstudios.wayfinder.advancements.WayfinderCriteriaTriggers;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WayfinderHeartBlockTrigger extends SimpleCriterionTrigger<WayfinderHeartBlockTrigger.TriggerInstance> {
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

		public static Criterion<TriggerInstance> wayFinderHeartBlock() {
			return WayfinderCriteriaTriggers.WAYFINDER_HEART_BLOCK.get()
					.createCriterion(new TriggerInstance(Optional.empty()));
		}
	}
}
