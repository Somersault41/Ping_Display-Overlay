package com.pingdisplayoverlay.neoforge.mixin;

import com.pingdisplayoverlay.neoforge.FastPing;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handlePongResponse", at = @At("HEAD"))
    private void pdo$onPong(ClientboundPongResponsePacket packet, CallbackInfo ci) {
        FastPing.onPong(packet);
    }
}
