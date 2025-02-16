package net.potionstudios.wayfinder.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.network.packet.MultiloaderPacket;

import java.util.function.Consumer;

public record WayfinderBiomePacket(ResourceLocation biome) implements MultiloaderPacket {

	public static final CustomPacketPayload.Type<WayfinderBiomePacket> TYPE = new Type<>(Wayfinder.id("biome"));

	public static final StreamCodec<FriendlyByteBuf, WayfinderBiomePacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.fromCodec(ResourceLocation.CODEC), WayfinderBiomePacket::biome, WayfinderBiomePacket::new
	);

	@Override
	public void receiveMessage(@Nullable Player player, Consumer<Runnable> consumer) {
		consumer.accept(() -> {
			// Use Given Player to get the Server and then the Level of the Player's World then the wayfinder based on the UUID then set the biome to find
			player.getServer().getLevel(player.getCommandSenderWorld().dimension()).getEntity(PlatformHandler.PLATFORM_HANDLER.getWayfinder(player));
		});
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
