package com.pingdisplayoverlay.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import com.pingdisplayoverlay.neoforge.config.PingConfig;
import com.pingdisplayoverlay.neoforge.gui.PingConfigScreen;
import com.pingdisplayoverlay.neoforge.render.PingHudLayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = PingDisplayOverlayNeoForge.MODID, dist = Dist.CLIENT)
public final class PingDisplayOverlayNeoForge {
    public static final String MODID = "pingdisplayoverlay";

    private static KeyMapping openConfigKey;

    public PingDisplayOverlayNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        PingConfig.get();

        modContainer.registerExtensionPoint(
            IConfigScreenFactory.class,
            (container, parent) -> new PingConfigScreen(parent)
        );

        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MODID, "main"));
        openConfigKey = new KeyMapping(
            "key.pingdisplayoverlay.open_config",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_RSHIFT,
            category
        );

        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            event.registerCategory(category);
            event.register(openConfigKey);
        });

        modEventBus.addListener((RegisterGuiLayersEvent event) -> event.registerAboveAll(
            Identifier.fromNamespaceAndPath(MODID, "ping_hud"),
            new PingHudLayer()
        ));

        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
            Minecraft mc = Minecraft.getInstance();
            while (openConfigKey.consumeClick()) {
                mc.gui.setScreen(new PingConfigScreen(null));
            }
            FastPing.tick(mc.getConnection());
        });
    }
}
