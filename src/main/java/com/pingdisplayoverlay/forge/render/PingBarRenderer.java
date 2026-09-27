package com.pingdisplayoverlay.forge.render;

import com.pingdisplayoverlay.forge.config.PingConfig;

/**
 * Tab liste ve HUD tarafından ortak kullanılan ping çubuğu (indicator) çizimi.
 * Vanilla'nın sprite tabanlı ping ikonu yerine, kullanıcının tarif ettiği
 * merdiven-basamak geometrisiyle tamamen özel piksel çizimi yapılır.
 */
public final class PingBarRenderer {
    public static final int ICON_WIDTH = 10;
    public static final int ICON_HEIGHT = 8;

    private PingBarRenderer() {
    }

    @FunctionalInterface
    public interface FillOp {
        void fill(int x1, int y1, int x2, int y2, int argbColor);
    }

    /** Aynı hex rengin daha koyu bir tonunu üretir (siyaha doğru harmanlama). */
    private static int darken(int rgb, float amount) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        r = (int) (r * (1 - amount));
        g = (int) (g * (1 - amount));
        b = (int) (b * (1 - amount));
        return (r << 16) | (g << 8) | b;
    }

    private static int withAlpha(int rgb, int opacityPercent) {
        int alpha = Math.round(255 * Math.max(0, Math.min(100, opacityPercent)) / 100f);
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    /**
     * @param x     ikonun sol üst x'i
     * @param yTop  ikonun sol üst y'si (taban = yTop + ICON_HEIGHT)
     * @param ping  ms cinsinden gecikme (negatif = bilinmiyor)
     * @return çizilen genişlik (piksel)
     */
    public static int draw(FillOp fillOp, PingConfig cfg, int x, int yTop, int ping, int opacityPercent) {
        int tier = cfg.tierForPing(ping);
        int baseY = yTop + ICON_HEIGHT;

        if (tier == 0) {
            int grey = withAlpha(0x808080, opacityPercent);
            for (int i = 0; i < 5; i++) {
                int h = 2 + i;
                int gx = x + i * 2;
                fillOp.fill(gx, baseY - h, gx + 1, baseY, grey);
            }
            return 10;
        }

        // Acik renk = ping degerinin rengiyle birebir ayni (kullanici istegi)
        int lightColor = withAlpha(cfg.colorForTier(tier), opacityPercent);
        int darkColor = withAlpha(darken(cfg.colorForTier(tier), 0.45f), opacityPercent);

        int groups = Math.min(tier, 5); // tier 6 (baglanti kaybi) = 5 bar, lacivert renkte
        for (int i = 0; i < groups; i++) {
            int h = 2 + i;
            int gx = x + i * 2;
            // acik bar: solda, 1 piksel yukarı kaydırılmış (tabanla arasında 1px boşluk)
            fillOp.fill(gx, baseY - 1 - h, gx + 1, baseY - 1, lightColor);
            // koyu bar: sagda, tabana değiyor
            fillOp.fill(gx + 1, baseY - h, gx + 2, baseY, darkColor);
        }
        return groups * 2;
    }
}
