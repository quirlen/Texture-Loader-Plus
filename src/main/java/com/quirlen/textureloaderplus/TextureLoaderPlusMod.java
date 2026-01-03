package com.quirlen.textureloaderplus;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextureLoaderPlusMod implements ModInitializer {
    public static final String MOD_ID = "texture-loader-plus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Texture Loader Plus initializing...");
        javax.imageio.ImageIO.scanForPlugins();
    }
}
