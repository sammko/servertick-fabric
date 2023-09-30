package net.cavoj.servertick.mixin;

import io.netty.buffer.ByteBuf;
import net.cavoj.servertick.extensions.SerializablePerformanceLog;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.profiler.PerformanceLog;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PerformanceLog.class)
public abstract class PerformanceLogMixin implements SerializablePerformanceLog {

    @Unique
    private long lastSample;
    @Shadow @Final private long[] data;
    @Shadow private int currentIndex;
    @Shadow private int maxIndex;


    @Override
    public void servertick$deserialize(ByteBuf data) {
        data.readInt(); // for backwards compatibility
        this.maxIndex = data.readInt();
        this.currentIndex = data.readInt();
        for (int i = 0; i < this.data.length; i++)
            this.data[i] = data.readLong();
    }

    @Override
    public void servertick$serialize(PacketByteBuf data) {
        data.writeInt(0); // for backwards compatibility
        data.writeInt(this.maxIndex);
        data.writeInt(this.currentIndex);
        for (int i = 0; i < this.data.length; i++)
            data.writeLong(this.data[i]);
    }

    @Inject(method = "push", at = @At("HEAD"))
    public void push(long value, CallbackInfo ci) {
        this.lastSample = value;
    }

    @Override
    public long servertick$getLastSample() {
        return this.lastSample;
    }
}
