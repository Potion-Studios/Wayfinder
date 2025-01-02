package net.potionstudios.wayfinder.neoforge;

import com.google.auto.service.AutoService;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

@AutoService(PlatformHandler.class)
public class NeoForgePlatformHandler implements PlatformHandler {
	@Override
	public Path configPath() {
		return FMLPaths.CONFIGDIR.get().resolve(Wayfinder.MOD_ID);
	}

	public static final Map<ResourceKey<?>, DeferredRegister> CACHED = new Reference2ObjectOpenHashMap<>();

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
		return player.getPersistentData().getBoolean("hasWayfinder");
	}

	@Override
	public void setWayfinder(Player player, boolean hasWayfinder) {
		player.getPersistentData().putBoolean("hasWayfinder", hasWayfinder);
	}

	public static void register(IEventBus bus) {
		CACHED.values().forEach(deferredRegister -> deferredRegister.register(bus));
	}
}
