package net.cavoj.servertick.extensions;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;

public interface SerializableMetricsData {
    void deserialize(ByteBuf data);
    void serialize(PacketByteBuf data);
}
