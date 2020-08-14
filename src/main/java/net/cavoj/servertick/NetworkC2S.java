package net.cavoj.servertick;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class NetworkC2S {
    public static final Identifier PACKET_ENABLED = new Identifier("servertick", "enabled");

    public static void sendToggle(boolean state) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeBoolean(state);
        ClientSidePacketRegistry.INSTANCE.sendToServer(PACKET_ENABLED, data);
    }
}
