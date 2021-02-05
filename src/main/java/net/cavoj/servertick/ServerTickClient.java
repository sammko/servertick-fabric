package net.cavoj.servertick;

import io.netty.buffer.ByteBuf;
import net.cavoj.servertick.extensions.SerializableMetricsData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
        ClientPlayNetworking.registerGlobalReceiver(NetworkS2C.PACKET_FULL_METRICS, (client, handler, buf, responseSender) -> {
            ByteBuf bufcopy = buf.copy();
            client.execute(() -> {
                if (this.metrics == null)
                    this.metrics = new MetricsData();
                ((SerializableMetricsData)this.metrics).deserialize(bufcopy);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(NetworkS2C.PACKET_LAST_SAMPLE, (client, handler, buf, responseSender) -> {
            long time = buf.readLong();
            client.execute(() -> {
                if (this.metrics != null)
                    this.metrics.pushSample(time);
            });
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
