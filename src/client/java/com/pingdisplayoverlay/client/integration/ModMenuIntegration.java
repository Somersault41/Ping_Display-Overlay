package com.pingdisplayoverlay.client.integration;

import com.pingdisplayoverlay.client.gui.PingConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu kuruluysa "Config" butonuyla ayar ekranimizi acar. ModMenu kurulu degilse
 * bu sinif hic yuklenmez (Fabric entrypoint sistemi lazy - sadece ModMenu bu entrypoint'i sorarsa calisir).
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return PingConfigScreen::new;
    }
}
