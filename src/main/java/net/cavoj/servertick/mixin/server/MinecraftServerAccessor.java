package net.cavoj.servertick.mixin.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MetricsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
    @Accessor("metricsData")
    MetricsData getMetricsData();
}
