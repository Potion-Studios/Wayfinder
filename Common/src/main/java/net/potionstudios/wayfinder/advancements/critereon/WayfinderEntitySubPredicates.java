package net.potionstudios.wayfinder.advancements.critereon;

import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

import java.util.Optional;

/**
 * @see EntitySubPredicates
 * @author Joseph T. McQuigg
 */
public class WayfinderEntitySubPredicates {

	public static final EntitySubPredicates.EntityVariantPredicateType<WayfinderEntity.Variant> WAYFINDER = register(
			"wayfinder",
			EntitySubPredicates.EntityVariantPredicateType.create(
					WayfinderEntity.Variant.CODEC, entity -> entity instanceof WayfinderEntity wayfinder ? Optional.of(wayfinder.getVariant()) : Optional.empty()
			)
	);

	private static <V> EntitySubPredicates.EntityVariantPredicateType<V> register(String name, EntitySubPredicates.EntityVariantPredicateType<V> predicateType) {
		PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, name, () -> predicateType.codec);
		return predicateType;
	}

	public static void entitySubPredicates() {
		Wayfinder.LOGGER.info("Registering Entity SubPredicate");
	}
}
