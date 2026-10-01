package com.pingdisplayoverlay.forge;

import com.mojang.blaze3d.platform.InputConstants;
import com.pingdisplayoverlay.forge.config.PingConfig;
import com.pingdisplayoverlay.forge.gui.PingConfigScreen;
import com.pingdisplayoverlay.forge.render.PingHudLayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PingDisplayOverlayForge.MODID)
public final class PingDisplayOverlayForge {
    public static final String MODID = "pingdisplayoverlay";

    private static KeyMapping openConfigKey;

    public PingDisplayOverlayForge(FMLJavaModLoadingContext context) {
        PingConfig.get();

        context.getContainer().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new PingConfigScreen(parent))
        );

        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MODID, "main"));
        openConfigKey = new KeyMapping(
            "key.pingdisplayoverlay.open_config",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_RSHIFT,
            category
        );

        RegisterKeyMappingsEvent.BUS.addListener(event -> event.register(openConfigKey));

        AddGuiOverlayLayersEvent.BUS.addListener(event -> event.getLayeredDraw().add(
            Identifier.fromNamespaceAndPath(MODID, "ping_hud"),
            new PingHudLayer()
        ));

        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> {
            Minecraft mc = Minecraft.getInstance();
            while (openConfigKey.consumeClick()) {
                mc.setScreen(new PingConfigScreen(null));
            }
            FastPing.tick(mc.getConnection());
        });
    }
}
