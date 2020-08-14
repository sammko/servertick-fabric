package net.cavoj.servertick.extensions;

import net.minecraft.network.PacketByteBuf;

public interface SerializableMetricsData {
    void deserialize(PacketByteBuf data);
    void serialize(PacketByteBuf data);
}
