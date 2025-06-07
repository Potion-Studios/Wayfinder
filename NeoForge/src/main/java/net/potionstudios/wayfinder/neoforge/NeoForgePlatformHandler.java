package net.potionstudios.wayfinder.neoforge;

import com.google.auto.service.AutoService;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.network.packet.MultiloaderPacket;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@AutoService(PlatformHandler.class)
public final class NeoForgePlatformHandler implements PlatformHandler {
	@Override
	public Path configPath() {
		return FMLPaths.CONFIGDIR.get().resolve(Wayfinder.MOD_ID);
	}

	private static final boolean luckPerms = ModList.get().isLoaded("luckperms");

	@Override
	public boolean hasPermission(@NotNull CommandSourceStack sourceStack, @NotNull String permission) {
		return PlatformHandler.super.hasPermission(sourceStack, permission) || (luckPerms && LuckPermsProvider.get().getUserManager().getUser(sourceStack.getPlayer().getUUID()).getCachedData().getPermissionData().checkPermission(permission).asBoolean());
	}

	private static final Map<ResourceKey<?>, DeferredRegister> CACHED = new Reference2ObjectOpenHashMap<>();

	@Override
	public <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value) {
		return CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key().location(), Wayfinder.MOD_ID)).register(name, value);
	}

	@Override
	public <T> Supplier<Holder.Reference<T>> registerForHolder(Registry<T> registry, String name, Supplier<T> value) {
		DeferredHolder<?, ?> registryObject = CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key().location(), Wayfinder.MOD_ID)).register(name, value);
		return () -> (Holder.Reference<T>) registryObject.getDelegate();
	}

	@Override
	public boolean hasWayfinder(Player player) {
        return player.getPersistentData().hasUUID("wayfinder") && PlatformHandler.super.hasWayfinder(player);
	}

	@Override
	public void setWayfinder(Player player, UUID wayfinder) {
		player.getPersistentData().putUUID("wayfinder", wayfinder);
	}

	@Override
	public UUID getWayfinder(Player player) {
		return player.getPersistentData().getUUID("wayfinder");
	}

	@Override
	public int getWayfinderDeaths(Player player) {
		return player.getPersistentData().getInt("wayfinder_deaths");
	}

	@Override
	public void incrementWayfinderDeaths(Player player) {
		player.getPersistentData().putInt("wayfinder_deaths", getWayfinderDeaths(player) + 1);
	}

	@Override
	public void resetWayfinderDeaths(Player player) {
		player.getPersistentData().putInt("wayfinder_deaths", 0);
	}

	@Override
	public void sendToPlayer(MultiloaderPacket packet, Player player) {
		PacketDistributor.sendToPlayer((ServerPlayer) player, packet);
	}

	@Override
	public void sendToServer(MultiloaderPacket packet) {
		PacketDistributor.sendToServer(packet);
	}

	public static void register(final IEventBus bus) {
		CACHED.values().forEach(deferredRegister -> deferredRegister.register(bus));
	}
}
