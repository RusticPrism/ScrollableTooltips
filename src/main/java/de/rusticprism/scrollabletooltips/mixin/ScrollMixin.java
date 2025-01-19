package de.rusticprism.scrollabletooltips.mixin;

import de.rusticprism.scrollabletooltips.ScrollableTooltips;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class ScrollMixin {

    private boolean scrolled = false;
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    public void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen instanceof AbstractInventoryScreen<?>) {
            return;
        }
        if(!scrolled) {
            if (ScrollableTooltips.scrollDistance > -1) {
                ScrollableTooltips.scrollDistance = ScrollableTooltips.scrollDistance + (int) vertical;
            }else {
                ScrollableTooltips.scrollDistance = 0;
            }
            scrolled = true;
        }else {
            scrolled = false;
        }
        ci.cancel();
    }
}
