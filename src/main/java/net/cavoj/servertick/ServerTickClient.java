package net.cavoj.servertick;

import net.cavoj.servertick.extensions.SerializableMetricsData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.MetricsData;

public class ServerTickClient implements ClientModInitializer {
    private static ServerTickClient _instance;

    public ServerTickClient() {
        if (_instance != null) {
            throw new RuntimeException("Cannot have multiple instances");
        }
        _instance = this;
    }

    public static ServerTickClient getInstance() {
        return _instance;
    }

    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(NetworkS2C.PACKET_FULL_METRICS, this::processMetricsFullPacket);
        ClientSidePacketRegistry.INSTANCE.register(NetworkS2C.PACKET_LAST_SAMPLE, this::processMetricsSamplePacket);
    }

    private void processMetricsFullPacket(PacketContext ctx, PacketByteBuf data) {
        // not sure if I can do this on the network thread
        if (this.metrics == null)
            this.metrics = new MetricsData();
        ((SerializableMetricsData)this.metrics).deserialize(data);
    }

    private void processMetricsSamplePacket(PacketContext ctx, PacketByteBuf data) {
        long time = data.readLong();
        ctx.getTaskQueue().execute(() -> {
            if (this.metrics != null)
                this.metrics.pushSample(time);
        });
    }

    private boolean debugTpsEnabled;
    private MetricsData metrics;

    public void setTpsEnabled(boolean enabled) {
        if (this.debugTpsEnabled != enabled) {
            if (enabled) {
                // To prevent displaying stale data
                setMetricsData(null);
            }
            this.debugTpsEnabled = enabled;
            NetworkC2S.sendToggle(enabled);
        }
    }

    public void setMetricsData(MetricsData data) {
        this.metrics = data;
    }

    public MetricsData getMetricsData() {
        return this.metrics;
    }

    public void joined() {
        setMetricsData(null);
        setTpsEnabled(false);
    }
}
