package com.pingdisplayoverlay.neoforge.render;

import com.pingdisplayoverlay.neoforge.FastPing;
import net.minecraft.network.chat.Component;

public final class PingTextUtil {
    private PingTextUtil() {
    }

    public static Component format(int ping) {
        if (ping == FastPing.PING_LOST) {
            return Component.translatable("pingdisplayoverlay.ping_ms", -1);
        }
        return ping < 0
            ? Component.translatable("pingdisplayoverlay.ping_unknown")
            : Component.translatable("pingdisplayoverlay.ping_ms", ping);
    }

    public static int withAlpha(int rgb, int opacityPercent) {
        int alpha = Math.round(255 * Math.max(0, Math.min(100, opacityPercent)) / 100f);
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }
}
