package net.cavoj.servertick.mixin;

import net.cavoj.servertick.STServer;
import net.cavoj.servertick.SerializableMetricsData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.MetricsData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MetricsData.class)
public abstract class MetricsDataMixin implements SerializableMetricsData {

    @Shadow @Final private long[] samples;

    @Shadow private int writeIndex;

    @Shadow private int sampleCount;

    @Shadow private int startIndex;

    @Override
    public void deserialize(PacketByteBuf data) {
        this.writeIndex = data.readInt();
        this.sampleCount = data.readInt();
        this.startIndex = data.readInt();
        for (int i = 0; i < this.samples.length; i++)
            this.samples[i] = data.readLong();
    }

    @Override
    public void serialize(PacketByteBuf data) {
        data.writeInt(this.writeIndex);
        data.writeInt(this.sampleCount);
        data.writeInt(this.startIndex);
        for (int i = 0; i < this.samples.length; i++)
            data.writeLong(this.samples[i]);
    }

}
