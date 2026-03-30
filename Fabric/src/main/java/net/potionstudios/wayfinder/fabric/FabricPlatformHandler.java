package net.potionstudios.wayfinder.fabric;

import com.google.auto.service.AutoService;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.fabric.data.WayfinderFabricAttachmentData;
import com.geckolib.network.packet.MultiloaderPacket;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

@AutoService(PlatformHandler.class)
public final class FabricPlatformHandler implements PlatformHandler {
	@Override
	public Path configPath() {
		return FabricLoader.getInstance().getConfigDir().resolve(Wayfinder.MOD_ID);
	}

	private static final boolean fabricPermissionsApi = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");

	@Override
	public boolean hasPermission(@NonNull CommandSourceStack sourceStack, @NonNull String permission) {
		return PlatformHandler.super.hasPermission(sourceStack, permission) || (fabricPermissionsApi && Permissions.check(sourceStack, permission));
	}

	@Override
	public <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value) {
		T value1 = Registry.register(registry, Wayfinder.id(name), value.get());
		return () -> value1;
	}

	@Override
	public <T> Supplier<Holder.Reference<T>> registerForHolder(Registry<T> registry, String name, Supplier<T> value) {
		Holder.Reference<T> reference = Registry.registerForHolder(registry, Wayfinder.id(name), value.get());
		return () -> reference;
	}

	@Override
	public void setWayfinder(Player player, UUID wayfinder) {
		WayfinderFabricAttachmentData.setWayfinder(player, wayfinder);
	}

	@Override
	public UUID getWayfinder(Player player) {
		return WayfinderFabricAttachmentData.getWayfinder(player);
	}

	@Override
	public int getWayfinderDeaths(Player player) {
		return WayfinderFabricAttachmentData.getWayfinderDeaths(player);
	}

	@Override
	public void incrementWayfinderDeaths(Player player) {
		WayfinderFabricAttachmentData.incrementWayfinderDeaths(player);
	}

	@Override
	public void resetWayfinderDeaths(Player player) {
		WayfinderFabricAttachmentData.resetWayfinderDeaths(player);
	}

    @Override
    public void increment3kJourneys(Player player) {
        WayfinderFabricAttachmentData.increment3kJourneys(player);
    }

    @Override
    public int get3kJourneys(Player player) {
        return WayfinderFabricAttachmentData.get3kJourneys(player);
    }

    @Override
	public void sendToPlayer(MultiloaderPacket packet, Player player) {
		ServerPlayNetworking.send((ServerPlayer) player, packet);
	}

	@Override
	public void sendToServer(MultiloaderPacket packet) {
		ClientPlayNetworking.send(packet);
	}

	@Override
	public boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}
}
