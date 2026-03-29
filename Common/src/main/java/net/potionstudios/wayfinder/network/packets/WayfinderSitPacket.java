package net.potionstudios.wayfinder.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.geckolib.network.packet.MultiloaderPacket;

import java.util.function.Consumer;

public record WayfinderSitPacket(boolean sit) implements MultiloaderPacket {

	public static final CustomPacketPayload.Type<WayfinderSitPacket> TYPE = new Type<>(Wayfinder.id("sit"));

	public static final StreamCodec<FriendlyByteBuf, WayfinderSitPacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, WayfinderSitPacket::sit, WayfinderSitPacket::new
	);

	@Override
	public void receiveMessage(@Nullable Player player, Consumer<Runnable> consumer) {
		consumer.accept(() -> {
			Entity entity = player.level().getEntity(PlatformHandler.PLATFORM_HANDLER.getWayfinder(player));
			if (entity instanceof WayfinderEntity wayfinder)
				if (sit) wayfinder.sit();
				else wayfinder.stand();
		});
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
