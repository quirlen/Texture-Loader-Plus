package com.quirlen.textureloaderplus.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.Screen;

public class TextureLoaderPlusConfig {

    public static boolean enablePSD = true;
    public static boolean enableTIFF = true;
    public static boolean enablePNM = true;
    public static boolean enableCache = true;
    public static boolean debugLog = false;

    public static enum PriorityFormat {
        PNG("png"),
        PSD("psd"),
        TIFF("tiff"),
        PNM("pnm");

        private final String extension;

        PriorityFormat(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }

        @Override
        public String toString() {
            return name();
        }
    }

    public static PriorityFormat primaryFormat = PriorityFormat.PSD;

    public static String getEffectivePrimaryExtension() {
        if (primaryFormat == null)
            return "png";

        switch (primaryFormat) {
            case PSD:
                return enablePSD ? "psd" : "png";
            case TIFF:
                return enableTIFF ? "tiff" : "png";
            case PNM:
                return enablePNM ? "pnm" : "png";
            case PNG:
            default:
                return "png";
        }
    }

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("Texture Loader Plus"));

        ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder
                .startEnumSelector(Text.of("Primary Priority"), PriorityFormat.class, primaryFormat)
                .setDefaultValue(PriorityFormat.PSD)
                .setTooltip(Text.of("Format checked first. Falls back to PNG if disabled."))
                .setSaveConsumer(newValue -> primaryFormat = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable PSD"), enablePSD)
                .setDefaultValue(true)
                .setTooltip(Text.of("Load .psd (Photoshop) files"))
                .setSaveConsumer(newValue -> enablePSD = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable TIFF"), enableTIFF)
                .setDefaultValue(true)
                .setTooltip(Text.of("Load .tiff/.tif files"))
                .setSaveConsumer(newValue -> enableTIFF = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable PNM"), enablePNM)
                .setDefaultValue(true)
                .setTooltip(Text.of("Load .pnm/.ppm files"))
                .setSaveConsumer(newValue -> enablePNM = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Smart Caching"), enableCache)
                .setDefaultValue(true)
                .setTooltip(Text.of("Cache converted images for faster loading"))
                .setSaveConsumer(newValue -> enableCache = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Debug Logging"), debugLog)
                .setDefaultValue(false)
                .setTooltip(Text.of("Enable detailed log output"))
                .setSaveConsumer(newValue -> debugLog = newValue)
                .build());

        return builder.build();
    }
}
