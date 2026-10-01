package com.pingdisplayoverlay.forge.mixin;

import net.minecraft.locale.Language;
import net.minecraftforge.forgespi.language.IModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * ModInfo (mods.toml'dan gelen kayit) cok erken, Mixin altyapisi devreye girmeden once
 * yuklendigi icin ModInfo.getDescription()'a doğrudan mixin islemiyor. Bunun yerine, aciklamanin
 * gercekten okundugu yer olan mod listesi ekranini (gec, kullanici actiginda yuklenir) hedefliyoruz.
 */
@Mixin(net.minecraftforge.client.gui.ModListScreen.class)
public class ModListInfoPanelMixin {
    @Redirect(
        method = "updateCache",
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/forgespi/language/IModInfo;getDescription()Ljava/lang/String;")
    )
    private String pdo$translateDescription(IModInfo info) {
        String key = "fml.menu.mods.info.description." + info.getModId();
        if (Language.getInstance().has(key)) {
            return Language.getInstance().getOrDefault(key);
        }
        return info.getDescription();
    }
}
