package net.cavoj.servertick;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class NetworkC2S {
    public static final Identifier PACKET_ENABLED = new Identifier("servertick", "enabled");

    public static void sendEnabled(boolean state) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeBoolean(state);
        if (ClientPlayNetworking.canSend(PACKET_ENABLED))
            ClientPlayNetworking.send(PACKET_ENABLED, data);
    }
}
