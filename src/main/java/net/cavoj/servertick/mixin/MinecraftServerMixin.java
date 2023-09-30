package net.cavoj.servertick.mixin;

import net.cavoj.servertick.ServerTick;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {


    @Inject(method = "tickTickLog", at = @At("HEAD"))
    protected void tickTickLog(long nanos, CallbackInfo ci) {
        ServerTick.getInstance().pushSample(nanos);
    }

}
