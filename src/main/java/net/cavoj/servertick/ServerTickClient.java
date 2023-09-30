package net.cavoj.servertick;

import io.netty.buffer.ByteBuf;
import net.cavoj.servertick.extensions.SerializablePerformanceLog;
import net.cavoj.servertick.mixin.client.DebugHudAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.PerformanceLog;

import java.util.Optional;

public class ServerTickClient implements ClientModInitializer {
    private static ServerTickClient _instance;
    private boolean debugTpsEnabled;

    private boolean serverResponded;

    public ServerTickClient() {
        if (_instance != null) {
            throw new RuntimeException("Cannot have multiple instances");
        }
        _instance = this;
    }

    public static ServerTickClient getInstance() {
        return _instance;
    }

    public boolean getServerResponded() {
        return serverResponded;
    }

    private Optional<PerformanceLog> getPerfLog() {
        if (MinecraftClient.getInstance().inGameHud != null) {
            return Optional.of(((DebugHudAccessor) MinecraftClient.getInstance().getDebugHud()).getTickNanosLog());
        }
        return Optional.empty();
    }

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkS2C.PACKET_FULL_METRICS, (client, handler, buf, responseSender) -> {
            serverResponded = true;
            ByteBuf bufcopy = buf.copy();
            client.execute(() -> getPerfLog().ifPresent(log -> ((SerializablePerformanceLog) log).servertick$deserialize(bufcopy)));
        });
        ClientPlayNetworking.registerGlobalReceiver(NetworkS2C.PACKET_LAST_SAMPLE, (client, handler, buf, responseSender) -> {
            long time = buf.readLong();
            client.execute(() -> getPerfLog().ifPresent(log -> log.push(time)));
        });

    }

    public void setTpsEnabled(boolean enabled) {
        if (this.debugTpsEnabled != enabled) {
            if (enabled) {
                getPerfLog().ifPresent(PerformanceLog::reset);
                serverResponded = false;
            }
            this.debugTpsEnabled = enabled;
            NetworkC2S.sendEnabled(enabled);
        }
    }

    public void joined() {
        getPerfLog().ifPresent(PerformanceLog::reset);
        serverResponded = false;
        setTpsEnabled(false);
    }
}
