package net.cavoj.servertick.mixin.client;

import net.cavoj.servertick.ServerTickClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.debug.TickChart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow private boolean renderingAndTickChartsVisible;

    @Shadow @Final private TickChart tickChart;

    @Shadow private boolean showDebugHud;

    @Inject(method = "method_51746", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/hud/debug/RenderingChart.render(Lnet/minecraft/client/gui/DrawContext;II)V", ordinal = 0))
    private void render(DrawContext context, CallbackInfo ci) {
        if (this.client.getServer() == null &&
                this.renderingAndTickChartsVisible &&
                ServerTickClient.getInstance().getServerResponded()) {
            this.client.getProfiler().push("debug");
            int i = context.getScaledWindowWidth();
            int j = i / 2;
            int k = this.tickChart.getWidth(j);
            this.tickChart.render(context, i - k, k);
            this.client.getProfiler().pop();
        }
    }

    @Inject(method = "toggleDebugHud", at = @At("RETURN"))
    private void toggleDebugHud(CallbackInfo ci) {
        checkIfVisible();
    }

    @Inject(method = "toggleRenderingAndTickCharts", at = @At("RETURN"))
    private void toggleRenderingAndTickCharts(CallbackInfo ci) {
        checkIfVisible();
    }

    @Inject(method = "togglePacketSizeAndPingCharts", at = @At("RETURN"))
    private void togglePacketSizeAndPingCharts(CallbackInfo ci) {
        checkIfVisible();
    }

    @Unique
    private void checkIfVisible() {
        if (this.client.getServer() != null) return; // Do nothing when playing on integrated
        ServerTickClient.getInstance().setTpsEnabled(this.showDebugHud && this.renderingAndTickChartsVisible);
    }

    @ModifyConstant(method = "drawLeftText", constant = @Constant(stringValue = "FPS "))
    private String drawLeftTextStr(String old) {
        return "FPS + TPS (st) ";
    }

}
