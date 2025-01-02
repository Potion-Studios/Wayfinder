package net.potionstudios.wayfinder.fabric;

import com.google.auto.service.AutoService;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.fabric.data.WayfinderAttachmentData;

import java.nio.file.Path;
import java.util.function.Supplier;

@AutoService(PlatformHandler.class)
public class FabricPlatformHandler implements PlatformHandler {
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
	public boolean hasWayfinder(Player player) {
		return WayfinderAttachmentData.hasWayfinder(player);
	}

	@Override
	public void setWayfinder(Player player, boolean hasWayfinder) {
		WayfinderAttachmentData.setWayfinder(player, hasWayfinder);
	}
}
