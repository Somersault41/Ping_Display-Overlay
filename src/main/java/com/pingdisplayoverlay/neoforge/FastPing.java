package com.pingdisplayoverlay.neoforge;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.util.Util;

/**
 * Vanilla'nin F3+3 ping grafigini besleyen ayni ping/pong paket ciftini kullanarak
 * yerel oyuncunun pingini vanilla'nin ~1sn'lik PlayerInfo yayinindan cok daha hizli olcer.
 */
public final class FastPing {
    /** Baglanti kesildiginde (belirli sure yanit gelmediginde) donen ozel deger. */
    public static final int PING_LOST = -2;
    private static final long TIMEOUT_MS = 2000;

    private static volatile int latestPing = -1;
    private static volatile long lastPongTime = 0;

    private FastPing() {
    }

    public static void tick(ClientPacketListener connection) {
        if (connection != null) {
            connection.send(new ServerboundPingRequestPacket(Util.getMillis()));
        }
    }

    public static void onPong(ClientboundPongResponsePacket packet) {
        latestPing = (int) (Util.getMillis() - packet.time());
        lastPongTime = Util.getMillis();
    }

    public static int get() {
        if (lastPongTime == 0) {
            return -1; // henuz hic pong alinmadi (baglanti yeni kuruluyor)
        }
        if (Util.getMillis() - lastPongTime > TIMEOUT_MS) {
            return PING_LOST; // belirli suredir yanit yok -> baglanti kesildi
        }
        return latestPing;
    }
}
