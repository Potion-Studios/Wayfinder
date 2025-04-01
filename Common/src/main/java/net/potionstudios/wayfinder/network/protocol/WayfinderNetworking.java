package net.potionstudios.wayfinder.network.protocol;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.potionstudios.wayfinder.network.packets.WayfinderBiomePacket;
import net.potionstudios.wayfinder.network.packets.WayfinderCloseScreenPacket;
import net.potionstudios.wayfinder.network.packets.WayfinderOpenScreenPacket;
import net.potionstudios.wayfinder.network.packets.WayfinderSitPacket;
import software.bernie.geckolib.network.packet.MultiloaderPacket;

import java.util.function.BiConsumer;

public class WayfinderNetworking {

	public static <T extends MultiloaderPacket> void registerS2CPackets(BiConsumer<CustomPacketPayload.Type<T>, StreamCodec<FriendlyByteBuf, T>> consumer) {
		consumer.accept((CustomPacketPayload.Type<T>) WayfinderOpenScreenPacket.TYPE, (StreamCodec<FriendlyByteBuf, T>) WayfinderOpenScreenPacket.CODEC);
		consumer.accept((CustomPacketPayload.Type<T>) WayfinderCloseScreenPacket.TYPE, (StreamCodec<FriendlyByteBuf, T>) WayfinderCloseScreenPacket.CODEC);
	}

	public static <T extends MultiloaderPacket> void registerC2SPackets(BiConsumer<CustomPacketPayload.Type<T>, StreamCodec<FriendlyByteBuf, T>> consumer) {
		consumer.accept((CustomPacketPayload.Type<T>) WayfinderBiomePacket.TYPE, (StreamCodec<FriendlyByteBuf, T>) WayfinderBiomePacket.CODEC);
		consumer.accept((CustomPacketPayload.Type<T>) WayfinderSitPacket.TYPE, (StreamCodec<FriendlyByteBuf, T>) WayfinderSitPacket.CODEC);
	}
}
