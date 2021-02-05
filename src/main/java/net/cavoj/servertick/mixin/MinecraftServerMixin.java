package net.cavoj.servertick.mixin;

import net.cavoj.servertick.NetworkS2C;
import net.cavoj.servertick.extensions.LastSampleMetricsData;
import net.cavoj.servertick.extensions.MinecraftServerWithST;
import net.cavoj.servertick.extensions.SerializableMetricsData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.MetricsData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerWithST {
    @Shadow @Final private MetricsData metricsData;
    private final HashSet<ServerPlayerEntity> listeners = new HashSet<>();

    @Override
    public void registerSTListener(ServerPlayerEntity player) {
        this.listeners.add(player);
        NetworkS2C.sendFullMetrics((SerializableMetricsData) this.metricsData, player);
    }

    @Override
    public void removeSTListener(ServerPlayerEntity player) {
        this.listeners.remove(player);
    }

    @Override
    public void tickST() {
        long sample = ((LastSampleMetricsData)this.metricsData).getLastSample();
        for (ServerPlayerEntity player : this.listeners) {
            NetworkS2C.sendLastSample(sample, player);
        }
    }
}
