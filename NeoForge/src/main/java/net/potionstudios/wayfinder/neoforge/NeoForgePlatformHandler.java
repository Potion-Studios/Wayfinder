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
import net.potionstudios.wayfinder.neoforge.data.WayfinderNeoForgeAttachmentData;
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

	private static final Map<ResourceKey<?>, DeferredRegister<?>> CACHED = new Reference2ObjectOpenHashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value) {
		return ((DeferredRegister<T>) CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(key.location(), Wayfinder.MOD_ID))).register(name, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Supplier<Holder.Reference<T>> registerForHolder(Registry<T> registry, String name, Supplier<T> value) {
		DeferredHolder<?, ?> registryObject = ((DeferredRegister<T>) CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(key.location(), Wayfinder.MOD_ID))).register(name, value);
		return () -> (Holder.Reference<T>) registryObject.getDelegate();
	}

	@Override
	public void setWayfinder(Player player, UUID wayfinder) {
		WayfinderNeoForgeAttachmentData.setWayfinder(player, wayfinder);
	}

	@Override
	public UUID getWayfinder(Player player) {
		return WayfinderNeoForgeAttachmentData.getWayfinder(player);
	}

	@Override
	public int getWayfinderDeaths(Player player) {
		return WayfinderNeoForgeAttachmentData.getWayfinderDeaths(player);
	}

	@Override
	public void incrementWayfinderDeaths(Player player) {
		WayfinderNeoForgeAttachmentData.incrementWayfinderDeaths(player);
	}

	@Override
	public void resetWayfinderDeaths(Player player) {
		WayfinderNeoForgeAttachmentData.resetWayfinderDeaths(player);
	}

    @Override
    public void increment3kJourneys(Player player) {
        WayfinderNeoForgeAttachmentData.increment3kJourneys(player);
    }

    @Override
    public int get3kJourneys(Player player) {
        return WayfinderNeoForgeAttachmentData.get3kJourneys(player);
    }

    @Override
	public void sendToPlayer(MultiloaderPacket packet, Player player) {
		PacketDistributor.sendToPlayer((ServerPlayer) player, packet);
	}

	@Override
	public void sendToServer(MultiloaderPacket packet) {
		PacketDistributor.sendToServer(packet);
	}

	@Override
	public boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	public static void register(final IEventBus bus) {
		CACHED.values().forEach(deferredRegister -> deferredRegister.register(bus));
	}
}
