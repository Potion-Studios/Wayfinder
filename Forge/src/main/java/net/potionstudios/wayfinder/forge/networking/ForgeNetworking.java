package net.potionstudios.wayfinder.forge.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.payload.PayloadProtocol;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.network.protocol.WayfinderNetworking;
import software.bernie.geckolib.network.packet.MultiloaderPacket;

public class ForgeNetworking {

	private static final int PROTOCOL_VERSION = 1;

	public static final PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> INSTANCE = ChannelBuilder.named(Wayfinder.id("main")).networkProtocolVersion(PROTOCOL_VERSION).optional().payloadChannel().play();

	public static Channel<CustomPacketPayload> CHANNEL;

	public static <P extends MultiloaderPacket> void init() {
		CHANNEL = INSTANCE.bidirectional().build();

		WayfinderNetworking.registerS2CPackets(ForgeNetworking::registerS2CPackets);
		WayfinderNetworking.registerC2SPackets(ForgeNetworking::registerC2SPackets);
	}

	private static <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerS2CPackets(CustomPacketPayload.Type<P> type, StreamCodec<B, P> codec) {
		INSTANCE.clientbound().add(type, (StreamCodec<RegistryFriendlyByteBuf, P>) codec, (packet, context) -> {
			packet.receiveMessage(context.getSender(), context::enqueueWork);
			context.setPacketHandled(true);
		});
	}

	private static <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerC2SPackets(CustomPacketPayload.Type<P> type, StreamCodec<B, P> codec) {
		INSTANCE.serverbound().add(type, (StreamCodec<RegistryFriendlyByteBuf, P>) codec, (packet, context) -> {
			packet.receiveMessage(context.getSender(), context::enqueueWork);
			context.setPacketHandled(true);
		});
	}

	public static void sendToPlayer(MultiloaderPacket packet, ServerPlayer player) {
		CHANNEL.send(packet, PacketDistributor.PLAYER.with(player));
	}

	public static void sendToServer(MultiloaderPacket packet) {
		CHANNEL.send(packet, PacketDistributor.SERVER.noArg());
	}
}
