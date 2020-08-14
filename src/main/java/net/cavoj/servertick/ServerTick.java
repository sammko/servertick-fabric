package net.cavoj.servertick;

import net.cavoj.servertick.extensions.MinecraftServerWithST;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class ServerTick implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerSidePacketRegistry.INSTANCE.register(NetworkC2S.PACKET_ENABLED, this::processTogglePacket);
        ServerTickEvents.END_SERVER_TICK.register((minecraftServer -> {
            ((MinecraftServerWithST)minecraftServer).tickST();
        }));
    }

    private boolean checkPlayerPrivilege(PlayerEntity player) {
        return player.hasPermissionLevel(4) || !player.getServer().isDedicated();
    }

    private void processTogglePacket(PacketContext ctx, PacketByteBuf data) {
        boolean state = data.readBoolean();
        ctx.getTaskQueue().execute(() -> {
            PlayerEntity player = ctx.getPlayer();
            MinecraftServerWithST server = (MinecraftServerWithST)ctx.getPlayer().getServer();
            assert server != null;
            if (state) {
                if (checkPlayerPrivilege(player)) {
                    server.registerSTListener(player);
                }
            } else {
                server.removeSTListener(player);
            }
        });
    }
}
