package net.cavoj.servertick;

import io.netty.buffer.Unpooled;
import net.cavoj.servertick.mixin.server.MinecraftServerAccessor;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.MetricsData;

import java.util.HashSet;
import java.util.Set;

public class STServer implements DedicatedServerModInitializer {
    private final Set<PlayerEntity> listeners = new HashSet<>();
    private static STServer _instance;

    public STServer() {
        if (_instance != null) {
            throw new RuntimeException("Cannot have multiple instances");
        }
        _instance = this;
    }

    @Override
    public void onInitializeServer() {
        ServerSidePacketRegistry.INSTANCE.register(Packets.PACKET_TOGGLE_DEBUG_SCREEN, this::processTogglePacket);
//        ServerTickEvents.END_SERVER_TICK.register(this::onTick);
    }

    public static STServer getInstance() {
        return _instance;
    }

//    private int tickCounter = 0;
//    private void onTick(MinecraftServer server) {
//        tickCounter++;
//        if (tickCounter > 20) {
//            tickCounter = 0;
//            // Send full metrics every so often to prevent desync
//            listeners.forEach(player -> sendMetrics(server, player));
//        }
//    }

    private void sendMetrics(MinecraftServer server, PlayerEntity player) {
        MetricsData metrics = ((MinecraftServerAccessor)server).getMetricsData();
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        ((SerializableMetricsData)metrics).serialize(data);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Packets.PACKET_FULL_METRICS, data);
    }

    private void processTogglePacket(PacketContext ctx, PacketByteBuf data) {
        boolean state = data.readBoolean();
        PlayerEntity player = ctx.getPlayer();
        MinecraftServer server = ctx.getPlayer().getServer();
        ctx.getTaskQueue().execute(() -> {
            if (state) {
                if (player.hasPermissionLevel(4)) {
                    listeners.add(player);
                    sendMetrics(server, player);
                }
            } else {
                listeners.remove(player);
            }
        });
    }

    public void pushSample(long time) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeLong(time);
        listeners.forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Packets.PACKET_SAMPLE_METRICS, data));
    }

    public void onPlayerDisconnected(ServerPlayerEntity player) {
        this.listeners.remove(player);
    }
}
