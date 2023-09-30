package net.cavoj.servertick;

import net.cavoj.servertick.extensions.SerializablePerformanceLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.profiler.PerformanceLog;

import java.nio.file.Path;
import java.util.HashSet;

public class ServerTick implements ModInitializer {
    private static ServerTick _instance;

    private Config config;

    private final HashSet<ServerPlayerEntity> listeners = new HashSet<>();

    private final PerformanceLog performanceLog = new PerformanceLog();

    public ServerTick() {
        if (_instance != null) {
            throw new RuntimeException("Cannot have multiple instances");
        }
        _instance = this;
    }

    public static ServerTick getInstance() {
        return _instance;
    }

    public void pushSample(long t) {
        performanceLog.push(t);
    }

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkC2S.PACKET_ENABLED, this::processTogglePacket);
        ServerTickEvents.END_SERVER_TICK.register((minecraftServer -> {
            long sample = ((SerializablePerformanceLog)this.performanceLog).servertick$getLastSample();
            for (ServerPlayerEntity player : this.listeners) {
                NetworkS2C.sendLastSample(sample, player);
            }
        }));
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            Path configFilePath = FabricLoader.getInstance().getConfigDir().resolve("servertick.toml");
            this.config = new Config(configFilePath);
        }
    }

    private boolean checkPlayerPrivilege(PlayerEntity player) {
        return (player.getServer() != null && !player.getServer().isDedicated()) ||
               (this.config != null && !this.config.requireOP) ||
               player.hasPermissionLevel(4);
    }

    private void processTogglePacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        boolean state = buf.readBoolean();
        server.execute(() -> {
            if (state) {
                if (checkPlayerPrivilege(player)) {
                    this.listeners.add(player);
                    NetworkS2C.sendFull((SerializablePerformanceLog) this.performanceLog, player);
                }
            } else {
                this.listeners.remove(player);
            }
        });
    }

    public void disconnected(ServerPlayerEntity player) {
        this.listeners.remove(player);
    }
}
