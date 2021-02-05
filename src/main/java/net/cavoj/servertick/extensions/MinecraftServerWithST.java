package net.cavoj.servertick.extensions;

import net.minecraft.server.network.ServerPlayerEntity;

public interface MinecraftServerWithST {
    void registerSTListener(ServerPlayerEntity player);
    void removeSTListener(ServerPlayerEntity player);
    void tickST();
}
