package com.pingdisplayoverlay.client.render;

import com.pingdisplayoverlay.client.FastPing;
import com.pingdisplayoverlay.client.config.PingConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

public class PingHudElement implements HudElement {
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        PingConfig cfg = PingConfig.get();
        if (!cfg.hudEnabled) {
            return;
        }
        if (!cfg.hudShowPing && !cfg.hudShowIndicator) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) {
            return;
        }
        PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
        int fast = FastPing.get();
        // fast == -1: henuz ilk pong gelmedi, sunucunun bildirdigi degere dus.
        // fast == PING_LOST: baglanti kesildi, oldugu gibi goster (fallback yapma).
        int ping = fast == -1 ? (info != null ? info.getLatency() : -1) : fast;

        Component text = PingTextUtil.format(ping);
        int textWidth = cfg.hudShowPing ? mc.font.width(text) : 0;
        int barWidth = cfg.hudShowIndicator ? PingBarRenderer.ICON_WIDTH : 0;
        int gap = (cfg.hudShowPing && cfg.hudShowIndicator) ? 2 : 0;
        int contentWidth = textWidth + gap + barWidth;
        int contentHeight = Math.max(cfg.hudShowIndicator ? PingBarRenderer.ICON_HEIGHT : 0, cfg.hudShowPing ? mc.font.lineHeight : 0);

        int screenW = graphics.guiWidth();
        int screenH = graphics.guiHeight();
        int x = Math.round((screenW - contentWidth) * (cfg.hudX / 100f));
        int y = Math.round((screenH - contentHeight) * (cfg.hudY / 100f));

        if (cfg.hudBackgroundOpacity > 0) {
            int bg = PingTextUtil.withAlpha(0x000000, cfg.hudBackgroundOpacity);
            graphics.fill(x - 2, y - 2, x + contentWidth + 2, y + contentHeight + 2, bg);
        }

        int cursorX = x;
        if (cfg.hudShowIndicator) {
            PingBarRenderer.draw(graphics::fill, cfg, cursorX, y, ping, cfg.hudPingOpacity);
            cursorX += barWidth + gap;
        }
        if (cfg.hudShowPing) {
            int tier = cfg.tierForPing(ping);
            int rgb = tier == 0 ? 0x808080 : cfg.colorForTier(tier);
            int textColor = PingTextUtil.withAlpha(rgb, cfg.hudPingOpacity);
            graphics.text(mc.font, text, cursorX, y, textColor);
        }
    }
}
