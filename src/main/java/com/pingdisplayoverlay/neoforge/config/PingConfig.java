package com.pingdisplayoverlay.neoforge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PingConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("pingdisplayoverlay.json");
    private static PingConfig instance;

    // GENEL
    public int thresholdVeryGood = 20;
    public int thresholdGood = 50;
    public int thresholdOk = 100;
    public int thresholdBad = 200;
    public int colorVeryGood = 0x087F23;
    public int colorGood = 0x55C94B;
    public int colorOk = 0xF2D21B;
    public int colorBad = 0xE53935;
    public int colorVeryBad = 0x8B0000;
    public int colorLost = 0x000080;

    // TAB
    public boolean tabEnabled = true;
    public boolean tabShowPing = true;
    public boolean tabShowIndicator = true;
    public boolean tabShowHeads = true;
    public boolean tabShowHat = true;
    public int tabHeadOpacity = 100;
    public int tabPingOpacity = 100;
    public int tabBackgroundOpacity = 50;
    public int tabWidth = 60;

    // HUD
    public boolean hudEnabled = true;
    public boolean hudShowPing = true;
    public boolean hudShowIndicator = true;
    public int hudX = 0;
    public int hudY = 1;
    public int hudPingOpacity = 100;
    public int hudBackgroundOpacity = 0;

    /** Karşılaştırma/Sıfırla butonları için değişmeyen varsayılan değerler. */
    public static final PingConfig DEFAULTS = new PingConfig();

    public static PingConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static PingConfig load() {
        if (Files.exists(FILE)) {
            try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                PingConfig loaded = GSON.fromJson(reader, PingConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException | RuntimeException ignored) {
            }
        }
        PingConfig fresh = new PingConfig();
        fresh.save();
        return fresh;
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public int tierForPing(int latencyMs) {
        if (latencyMs == com.pingdisplayoverlay.neoforge.FastPing.PING_LOST) return 6;
        if (latencyMs < 0) return 0;
        if (latencyMs <= thresholdVeryGood) return 5;
        if (latencyMs <= thresholdGood) return 4;
        if (latencyMs <= thresholdOk) return 3;
        if (latencyMs <= thresholdBad) return 2;
        return 1;
    }

    public int colorForTier(int tier) {
        return switch (tier) {
            case 6 -> colorLost;
            case 5 -> colorVeryGood;
            case 4 -> colorGood;
            case 3 -> colorOk;
            case 2 -> colorBad;
            default -> colorVeryBad;
        };
    }
}
