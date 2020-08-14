package net.cavoj.servertick.extensions;

import net.minecraft.entity.player.PlayerEntity;

public interface MinecraftServerWithST {
    void registerSTListener(PlayerEntity player);
    void removeSTListener(PlayerEntity player);
    void tickST();
}
