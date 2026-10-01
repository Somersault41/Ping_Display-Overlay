package com.pingdisplayoverlay.client;

import com.pingdisplayoverlay.client.config.PingConfig;
import com.pingdisplayoverlay.client.gui.PingConfigScreen;
import com.pingdisplayoverlay.client.render.PingHudElement;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class PingDisplayOverlayClient implements ClientModInitializer {
    public static final String MOD_ID = "pingdisplayoverlay";

    private static KeyMapping openConfigKey;

    @Override
    public void onInitializeClient() {
        PingConfig.get();

        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath(MOD_ID, "ping_hud"),
            new PingHudElement()
        );

        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
        openConfigKey = KeyMappingHelper.registerKeyMapping(
            new KeyMapping("key.pingdisplayoverlay.open_config", GLFW.GLFW_KEY_RIGHT_SHIFT, category)
        );

        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openConfigKey.consumeClick()) {
                mc.setScreen(new PingConfigScreen(null));
            }
            FastPing.tick(mc.getConnection());
        });
    }
}
