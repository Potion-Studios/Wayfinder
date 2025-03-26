package net.potionstudios.wayfinder.world.entity.block;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;

import java.util.function.Supplier;

public class WayfinderBlockEntities {

	public static final Supplier<BlockEntityType<WayfinderHeartBlockEntity>> WAYFINDER_HEART = register("wayfinder_heart", () -> BlockEntityType.Builder.of(
			WayfinderHeartBlockEntity::new, WayfinderBlocks.WAYFINER_HEART.get()
	));

	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String key, Supplier<BlockEntityType.Builder<T>> builder) {
		return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, () -> builder.get().build(Util.fetchChoiceType(References.BLOCK_ENTITY, key)));
	}

	public static void blockEntities() {
		Wayfinder.LOGGER.info("Registering Wayfinder Block Entities");
	}
}
