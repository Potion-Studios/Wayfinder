package net.potionstudios.wayfinder.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.client.gui.screens.WayfinderScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.geckolib.network.packet.MultiloaderPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record WayfinderOpenScreenPacket(List<Identifier> locations, Identifier current, boolean isSitting) implements MultiloaderPacket {

    public static final CustomPacketPayload.Type<WayfinderOpenScreenPacket> TYPE = new Type<>(Wayfinder.id("open_screen"));

    public static final StreamCodec<FriendlyByteBuf, WayfinderOpenScreenPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, Identifier.STREAM_CODEC), WayfinderOpenScreenPacket::locations,
            ByteBufCodecs.fromCodec(Identifier.CODEC), WayfinderOpenScreenPacket::current,
            ByteBufCodecs.BOOL, WayfinderOpenScreenPacket::isSitting,
            WayfinderOpenScreenPacket::new);

    @Override
    public void receiveMessage(@Nullable Player player, Consumer<Runnable> consumer) {
        consumer.accept(() -> WayfinderScreen.openScreen(locations, current, isSitting));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
