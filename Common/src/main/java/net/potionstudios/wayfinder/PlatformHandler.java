package net.potionstudios.wayfinder;

import net.minecraft.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import com.geckolib.network.packet.MultiloaderPacket;
import org.jspecify.annotations.NonNull;

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
	 * Checks if the player has the specified permission
	 * @param sourceStack The command source stack to check the permission for
	 * @param permission The permission to check
	 * @return True if the player has the permission, false otherwise
	 */
	default boolean hasPermission(@NonNull CommandSourceStack sourceStack, @NonNull String permission) {
		return sourceStack.permissions().hasPermission(Permissions.COMMANDS_OWNER);
	}

	<T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value);

	<T> Supplier<Holder.Reference<T>> registerForHolder(Registry<T> registry, String name, Supplier<T> value);

	default boolean hasWayfinder(Player player) {
		return !getWayfinder(player).equals(Util.NIL_UUID);
	}

	void setWayfinder(Player player, UUID wayfinder);

	UUID getWayfinder(Player player);

	int getWayfinderDeaths(Player player);

	void incrementWayfinderDeaths(Player player);

	void resetWayfinderDeaths(Player player);

    void increment3kJourneys(Player player);

    int get3kJourneys(Player player);

	void sendToPlayer(MultiloaderPacket packet, Player player);

	void sendToServer(MultiloaderPacket packet);

	private static <T> T load(Class<T> clazz) {
		final T loadedService = ServiceLoader.load(clazz)
				.findFirst()
				.orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
		Wayfinder.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
		return loadedService;
	}

	boolean isModLoaded(String modid);
}
