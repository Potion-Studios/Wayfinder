package net.potionstudios.wayfinder.forge;

import com.google.auto.service.AutoService;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.forge.networking.ForgeNetworking;
import com.geckolib.network.packet.MultiloaderPacket;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@AutoService(PlatformHandler.class)
public final class ForgePlatformHandler implements PlatformHandler {
	@Override
	public Path configPath() {
		return FMLPaths.CONFIGDIR.get().resolve(Wayfinder.MOD_ID);
	}

	private static final boolean luckPerms = ModList.get().isLoaded("luckperms");

	@Override
	public boolean hasPermission(@NonNull CommandSourceStack sourceStack, @NonNull String permission) {
		return PlatformHandler.super.hasPermission(sourceStack, permission) || (luckPerms && LuckPermsProvider.get().getUserManager().getUser(sourceStack.getPlayer().getUUID()).getCachedData().getPermissionData().checkPermission(permission).asBoolean());
	}

	private static final Map<ResourceKey<?>, DeferredRegister> CACHED = new Reference2ObjectOpenHashMap<>();

	@Override
	public <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value) {
		return CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key(), Wayfinder.MOD_ID)).register(name, value);
	}

	@Override
	public <T> Supplier<Holder.Reference<T>> registerForHolder(Registry<T> registry, String name, Supplier<T> value) {
		RegistryObject<T> registryObject = CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key(), Wayfinder.MOD_ID)).register(name, value);
		return () -> (Holder.Reference<T>) registryObject.getHolder().get();
	}

	@Override
	public boolean hasWayfinder(Player player) {
		return player.getPersistentData().contains("wayfinder") && PlatformHandler.super.hasWayfinder(player);
	}

	@Override
	public void setWayfinder(Player player, UUID wayfinder) {
		player.getPersistentData().putIntArray("wayfinder", UUIDUtil.uuidToIntArray(wayfinder));
	}

	@Override
	public UUID getWayfinder(Player player) {
		return UUIDUtil.uuidFromIntArray(player.getPersistentData().getIntArray("wayfinder").get());
	}

	@Override
	public int getWayfinderDeaths(Player player) {
		return player.getPersistentData().getIntOr("wayfinder_deaths", 0);
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
    public void increment3kJourneys(Player player) {
        player.getPersistentData().putInt("3k_journeys", get3kJourneys(player) + 1);
    }

    @Override
    public int get3kJourneys(Player player) {
        return player.getPersistentData().getIntOr("3k_journeys", 0);
    }

    @Override
	public void sendToPlayer(MultiloaderPacket packet, Player player) {
		ForgeNetworking.sendToPlayer(packet, (ServerPlayer) player);
	}

	@Override
	public void sendToServer(MultiloaderPacket packet) {
		ForgeNetworking.sendToServer(packet);
	}

	@Override
	public boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	public static void register(final BusGroup bus) {
		CACHED.values().forEach(deferredRegister -> deferredRegister.register(bus));
	}
}
