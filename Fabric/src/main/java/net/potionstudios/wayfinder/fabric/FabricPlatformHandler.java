package net.potionstudios.wayfinder.fabric;

import com.google.auto.service.AutoService;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.fabric.data.WayfinderAttachmentData;
import software.bernie.geckolib.network.packet.MultiloaderPacket;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

@AutoService(PlatformHandler.class)
public final class FabricPlatformHandler implements PlatformHandler {
	@Override
	public Path configPath() {
		return FabricLoader.getInstance().getConfigDir().resolve(Wayfinder.MOD_ID);
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
		WayfinderAttachmentData.setWayfinder(player, wayfinder);
	}

	@Override
	public UUID getWayfinder(Player player) {
		return WayfinderAttachmentData.getWayfinder(player);
	}

	@Override
	public void sendToPlayer(MultiloaderPacket packet, Player player) {
		ServerPlayNetworking.send((ServerPlayer) player, packet);
	}

	@Override
	public void sendToServer(MultiloaderPacket packet) {
		ClientPlayNetworking.send(packet);
	}
}
