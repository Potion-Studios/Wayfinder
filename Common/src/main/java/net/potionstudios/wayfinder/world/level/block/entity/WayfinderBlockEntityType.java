package net.potionstudios.wayfinder.world.level.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;

import java.util.Set;
import java.util.function.Supplier;

public class WayfinderBlockEntityType {

	public static final Supplier<BlockEntityType<WayfinderHeartBlockEntity>> WAYFINDER_HEART = register("wayfinder_heart", () -> new BlockEntityType<>(
			WayfinderHeartBlockEntity::new, Set.of(WayfinderBlocks.WAYFINER_HEART.get())
	));

	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String key, Supplier<BlockEntityType<T>> blockEntity) {
		return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, blockEntity);
	}

	public static void blockEntities() {
		Wayfinder.LOGGER.info("Registering Wayfinder Block Entities");
	}
}
