package net.potionstudios.wayfinder.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.ai.memory.WayfinderMemoryModuleType;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.network.packet.MultiloaderPacket;

import java.util.function.Consumer;

public record WayfinderBiomePacket(Identifier biome) implements MultiloaderPacket {

	public static final CustomPacketPayload.Type<WayfinderBiomePacket> TYPE = new Type<>(Wayfinder.id("biome"));

	public static final StreamCodec<FriendlyByteBuf, WayfinderBiomePacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.fromCodec(Identifier.CODEC), WayfinderBiomePacket::biome, WayfinderBiomePacket::new
	);

	@Override
	public void receiveMessage(@Nullable Player player, Consumer<Runnable> consumer) {
		consumer.accept(() -> {
			Entity entity = player.level().getEntity(PlatformHandler.PLATFORM_HANDLER.getWayfinder(player));
			if (entity instanceof WayfinderEntity wayfinder)
				if (biome.equals(Wayfinder.id("clear_packet"))) {
					wayfinder.getBrain().eraseMemory(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get());
					wayfinder.playSound(SoundEvents.BOOK_PUT);
				}
				else wayfinder.startBiomeSearch(biome);
		});
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
