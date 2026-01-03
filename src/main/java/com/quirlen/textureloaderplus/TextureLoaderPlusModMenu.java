package com.quirlen.textureloaderplus;

import com.quirlen.textureloaderplus.config.TextureLoaderPlusConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class TextureLoaderPlusModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return TextureLoaderPlusConfig::createConfigScreen;
    }
}
