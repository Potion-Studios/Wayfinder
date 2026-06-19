package net.potionstudios.wayfinder.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.client.gui.screens.WayfinderScreen;
import com.geckolib.network.packet.MultiloaderPacket;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record WayfinderCloseScreenPacket() implements MultiloaderPacket {

    public static final CustomPacketPayload.Type<WayfinderCloseScreenPacket> TYPE = new Type<>(Wayfinder.id("close_screen"));

    public static final StreamCodec<FriendlyByteBuf, WayfinderCloseScreenPacket> CODEC = StreamCodec.unit(new WayfinderCloseScreenPacket());

    @Override
    public void receiveMessage(@Nullable Player player, Consumer<Runnable> consumer) {
        if (Minecraft.getInstance().gui.screen() instanceof WayfinderScreen screen)
            consumer.accept(screen::onClose);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
