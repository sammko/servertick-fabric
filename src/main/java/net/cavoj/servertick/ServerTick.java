package net.cavoj.servertick;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import net.cavoj.servertick.extensions.MinecraftServerWithST;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class ServerTick implements ModInitializer {
    private ConfigHolder<ModConfig> configHolder;

    @Override
    public void onInitialize() {
        ServerSidePacketRegistry.INSTANCE.register(NetworkC2S.PACKET_ENABLED, this::processTogglePacket);
        ServerTickEvents.END_SERVER_TICK.register((minecraftServer -> {
            ((MinecraftServerWithST)minecraftServer).tickST();
        }));
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
            this.configHolder = AutoConfig.getConfigHolder(ModConfig.class);
        }
    }

    private boolean checkPlayerPrivilege(PlayerEntity player) {
        return (player.getServer() != null && !player.getServer().isDedicated()) ||
               (this.configHolder != null && !this.configHolder.getConfig().requireOP) ||
               player.hasPermissionLevel(4);
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
