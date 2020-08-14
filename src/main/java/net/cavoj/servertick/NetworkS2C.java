package net.cavoj.servertick;

import io.netty.buffer.Unpooled;
import net.cavoj.servertick.extensions.SerializableMetricsData;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class NetworkS2C {
    public static final Identifier PACKET_FULL_METRICS = new Identifier("servertick", "metrics/full");
    public static final Identifier PACKET_LAST_SAMPLE = new Identifier("servertick", "metrics/sample");

    public static void sendFullMetrics(SerializableMetricsData metrics, PlayerEntity player) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        metrics.serialize(data);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PACKET_FULL_METRICS, data);
    }

    public static void sendLastSample(long sample, PlayerEntity player) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeLong(sample);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PACKET_LAST_SAMPLE, data);
    }
}
