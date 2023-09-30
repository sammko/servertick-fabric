package net.cavoj.servertick.extensions;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;

public interface SerializablePerformanceLog {
    void servertick$deserialize(ByteBuf data);
    void servertick$serialize(PacketByteBuf data);

    long servertick$getLastSample();
}
