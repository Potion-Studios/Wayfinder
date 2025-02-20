package net.potionstudios.wayfinder;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import software.bernie.geckolib.network.packet.MultiloaderPacket;

import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * This class handles the registration of all content
 * Also handles making custom objects that are needed for each platforms
 * @author Joseph T. McQuigg
 */
public interface PlatformHandler {

	PlatformHandler PLATFORM_HANDLER = load(PlatformHandler.class);

	/**
	 * Gets the path to the config directory
	 * @return The path to the config directory
	 */
	Path configPath();

	/**
	 * Creates a spawn egg with the specified parameters
	 * @see SpawnEggItem
	 * @param entity The entity to be spawned from the spawn egg
	 * @param backgroundColor The background color of the spawn egg
	 * @param highlightColor The highlight color of the spawn egg
	 * @return Supplier of the SpawnEggItem
	 */
	default Supplier<SpawnEggItem> createSpawnEgg(Supplier<EntityType<? extends Mob>> entity, int backgroundColor, int highlightColor) {
		return () -> new SpawnEggItem(entity.get(), backgroundColor, highlightColor, new Item.Properties());
	}

	<T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value);

	<T> Supplier<Holder.Reference<T>> registerForHolder(Registry<T> registry, String name, Supplier<T> value);

	default boolean hasWayfinder(Player player) {
		return getWayfinder(player) != Util.NIL_UUID;
	};

	void setWayfinder(Player player, UUID wayfinder);

	UUID getWayfinder(Player player);

	int getWayfinderDeaths(Player player);

	void incrementWayfinderDeaths(Player player);

	void sendToPlayer(MultiloaderPacket packet, Player player);

	void sendToServer(MultiloaderPacket packet);

	private static <T> T load(Class<T> clazz) {
		final T loadedService = ServiceLoader.load(clazz)
				.findFirst()
				.orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
		Wayfinder.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
		return loadedService;
	}
}
