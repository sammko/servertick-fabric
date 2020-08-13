package net.cavoj.servertick;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.MetricsData;

public class STClient implements ClientModInitializer {
    private static STClient _instance;

    public STClient() {
        if (_instance != null) {
            throw new RuntimeException("Cannot have multiple instances");
        }
        _instance = this;
    }

    public static STClient getInstance() {
        return _instance;
    }

    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(Packets.PACKET_FULL_METRICS, this::processMetricsFullPacket);
        ClientSidePacketRegistry.INSTANCE.register(Packets.PACKET_SAMPLE_METRICS, this::processMetricsSamplePacket);
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
            this.debugTpsEnabled = enabled;
            updateTpsEnabled();
        }
    }

    private void updateTpsEnabled() {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeBoolean(this.debugTpsEnabled);
        ClientSidePacketRegistry.INSTANCE.sendToServer(Packets.PACKET_TOGGLE_DEBUG_SCREEN, data);
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
