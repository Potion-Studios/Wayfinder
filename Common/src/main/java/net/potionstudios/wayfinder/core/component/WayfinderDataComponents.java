package net.potionstudios.wayfinder.core.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class WayfinderDataComponents {

	public static final Supplier<DataComponentType<ScrollSkin>> SCROLL_DATA = register(
			"scroll_data",
			builder -> builder.persistent(ScrollSkin.CODEC)
					.networkSynchronized(ScrollSkin.STREAM_CODEC)
					.cacheEncoding()
	);

	private static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
		return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.DATA_COMPONENT_TYPE, name, () -> builder.apply(DataComponentType.builder()).build());
	}

	public static void dataComponents() {
		Wayfinder.LOGGER.info("Registering Wayfinder Data Components");
	}
}
