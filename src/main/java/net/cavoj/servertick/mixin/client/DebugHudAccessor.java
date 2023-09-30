package net.cavoj.servertick.mixin.client;

import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.profiler.PerformanceLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugHud.class)
public interface DebugHudAccessor {
    @Accessor("tickNanosLog")
    PerformanceLog getTickNanosLog();
}
