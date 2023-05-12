package de.rusticprism.scrollabletooltips.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import de.rusticprism.scrollabletooltips.ScrollableTooltips;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScoreTextContent;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Mixin(Screen.class)
public class ToolTippMixin {

    @Shadow protected TextRenderer textRenderer;

    @Shadow protected ItemRenderer itemRenderer;

    @Inject(method = "renderTooltipFromComponents", at = @At("HEAD"), cancellable = true)
    public void renderTooltip(MatrixStack matrices, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
        ci.cancel();
        if (!components.isEmpty()) {
            if(components.size() > ScrollableTooltips.scrollDistance) {
                if (ScrollableTooltips.scrollDistance > 0) {
                    components.subList(0, ScrollableTooltips.scrollDistance).clear();
                }
            }else {
                TooltipComponent component = components.get(components.size() - 1);
                ScrollableTooltips.scrollDistance = components.size() -1;
                components.clear();
                components.add(component);
            }
            int i = 0;
            int j = components.size() == 1 ? -2 : 0;

            TooltipComponent tooltipComponent;
            for(Iterator<TooltipComponent> var8 = components.iterator(); var8.hasNext(); j += tooltipComponent.getHeight()) {
                tooltipComponent = var8.next();
                int k = tooltipComponent.getWidth(this.textRenderer);
                if (k > i) {
                    i = k;
                }
            }

            Vector2ic vector2ic = positioner.getPosition(MinecraftClient.getInstance().currentScreen, x, y, i, j);
            int n = vector2ic.x();
            int o = vector2ic.y();
            matrices.push();
            boolean p = true;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            TooltipBackgroundRenderer.render(DrawableHelper::fillGradient, matrix4f, bufferBuilder, n, o, i, j, 400);
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            matrices.translate(0.0F, 0.0F, 400.0F);
            int q = o;

            int r;
            TooltipComponent tooltipComponent2;
            for(r = 0; r < components.size(); ++r) {
                tooltipComponent2 = components.get(r);
                tooltipComponent2.drawText(this.textRenderer, n, q, matrix4f, immediate);
                q += tooltipComponent2.getHeight() + (r == 0 ? 2 : 0);
            }

            immediate.draw();
            q = o;

            for(r = 0; r < components.size(); ++r) {
                tooltipComponent2 = components.get(r);
                tooltipComponent2.drawItems(this.textRenderer, n, q, matrices, this.itemRenderer);
                q += tooltipComponent2.getHeight() + (r == 0 ? 2 : 0);
            }

            matrices.pop();
        }
    }
}
