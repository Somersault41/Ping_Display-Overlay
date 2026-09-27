package com.pingdisplayoverlay.forge.mixin;

import com.pingdisplayoverlay.forge.FastPing;
import com.pingdisplayoverlay.forge.config.PingConfig;
import com.pingdisplayoverlay.forge.render.PingBarRenderer;
import com.pingdisplayoverlay.forge.render.PingTextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Shadow
    private static Identifier PING_UNKNOWN_SPRITE;
    @Shadow
    private static Identifier PING_1_SPRITE;
    @Shadow
    private static Identifier PING_2_SPRITE;
    @Shadow
    private static Identifier PING_3_SPRITE;
    @Shadow
    private static Identifier PING_4_SPRITE;
    @Shadow
    private static Identifier PING_5_SPRITE;

    /**
     * Tab genişliği hesaplamasındaki sabit padding değeri (vanilla: 13).
     * Bizim daha geniş kafa+ping göstergesi için 60'a çıkarıldı.
     */
    @ModifyConstant(method = "extractRenderState", constant = @Constant(intValue = 13))
    private int pdo$widerSlot(int original) {
        PingConfig cfg = PingConfig.get();
        return cfg.tabEnabled ? cfg.tabWidth : original;
    }

    /**
     * Vanilla, her satırın arkasına tam satır genişliğinde bir arka plan çizer
     * (minecraft.options.getBackgroundColor(...) ile hesaplanan renk). "Tab arka plan" ayarımızı
     * bu mekanizmaya bagliyoruz ki tüm satırı kaplasın (sadece ping degeri degil).
     */
    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getBackgroundColor(I)I"))
    private int pdo$rowBackground(Options options, int defaultColor) {
        PingConfig cfg = PingConfig.get();
        if (!cfg.tabEnabled) {
            return options.getBackgroundColor(defaultColor);
        }
        // %0 = tamamen seffaf (arka plan yok). %50 = vanilla varsayilani (beyaz, alpha 32/255).
        // %100 = tam opak siyah. Iki parcali dogrusal gecis: 0->50 seffaf->vanilla, 50->100 vanilla->siyah.
        float t = Math.max(0, Math.min(100, cfg.tabBackgroundOpacity)) / 100f;
        int alpha;
        int channel;
        if (t <= 0.5f) {
            float seg = t / 0.5f;
            alpha = Math.round(32 * seg);
            channel = 255;
        } else {
            float seg = (t - 0.5f) / 0.5f;
            alpha = Math.round(32 + (255 - 32) * seg);
            channel = Math.round(255 * (1 - seg));
        }
        int rgb = (channel << 16) | (channel << 8) | channel;
        return (alpha << 24) | rgb;
    }

    /**
     * showHead = minecraft.getConnection().onlineMode() hesaplamasını devreye alır.
     * tabEnabled kapalıysa gerçek online-mode davranışı aynen korunur (tam vanilla).
     * tabEnabled açıksa, gösterim tamamen "Oyuncu kafalarını göster" ayarına göre belirlenir
     * (online-mode true/false farketmeksizin) - böylece hem tekrar çizim hem eksik çizim olmaz.
     */
    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;onlineMode()Z"))
    private boolean pdo$showHead(ClientPacketListener connection) {
        PingConfig cfg = PingConfig.get();
        if (!cfg.tabEnabled) {
            // Ana ayar (Etkinleştir) kapaliyken kafalar da kapali olmali, alt ayardan bagimsiz.
            return false;
        }
        return cfg.tabShowHeads;
    }

    @Overwrite
    protected void extractPingIcon(final GuiGraphicsExtractor graphics, final int slotWidth, final int xo, final int yo, final PlayerInfo info) {
        PingConfig cfg = PingConfig.get();
        if (!cfg.tabEnabled) {
            Identifier sprite;
            int latency = info.getLatency();
            if (latency < 0) {
                sprite = PING_UNKNOWN_SPRITE;
            } else if (latency < 150) {
                sprite = PING_5_SPRITE;
            } else if (latency < 300) {
                sprite = PING_4_SPRITE;
            } else if (latency < 600) {
                sprite = PING_3_SPRITE;
            } else if (latency < 1000) {
                sprite = PING_2_SPRITE;
            } else {
                sprite = PING_1_SPRITE;
            }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, xo + slotWidth - 11, yo, 10, 8);
            return;
        }

        if (!cfg.tabShowPing && !cfg.tabShowIndicator) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        int fast = FastPing.get();
        boolean isSelf = mc.player != null && mc.player.getUUID().equals(info.getProfile().id());
        int ping = isSelf ? (fast == -1 ? info.getLatency() : fast) : info.getLatency();
        Component text = PingTextUtil.format(ping);
        int textWidth = cfg.tabShowPing ? mc.font.width(text) : 0;
        int barWidth = cfg.tabShowIndicator ? PingBarRenderer.ICON_WIDTH : 0;
        int gap = (cfg.tabShowPing && cfg.tabShowIndicator) ? 2 : 0;

        // Gosterge, ping degerinin saginda yer alir (tab listesinde).
        int rightEdge = xo + slotWidth;
        int barX = rightEdge - barWidth;
        int numberX = barX - gap - textWidth;

        if (cfg.tabShowIndicator) {
            PingBarRenderer.draw(graphics::fill, cfg, barX, yo, ping, cfg.tabPingOpacity);
        }
        if (cfg.tabShowPing) {
            int tier = cfg.tierForPing(ping);
            int rgb = tier == 0 ? 0x808080 : cfg.colorForTier(tier);
            int color = PingTextUtil.withAlpha(rgb, cfg.tabPingOpacity);
            graphics.text(mc.font, text, numberX, yo, color);
        }
    }
}
