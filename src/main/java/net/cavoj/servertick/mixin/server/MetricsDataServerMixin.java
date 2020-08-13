package net.cavoj.servertick.mixin.server;

import net.cavoj.servertick.STServer;
import net.minecraft.util.MetricsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MetricsData.class)
public abstract class MetricsDataServerMixin {

    @Inject(method = "pushSample", at = @At("HEAD"))
    public void pushSample(long time, CallbackInfo ci) {
        STServer.getInstance().pushSample(time);
    }
}
