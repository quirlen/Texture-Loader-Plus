package com.quirlen.textureloaderplus.mixin;

import com.quirlen.textureloaderplus.ImageReaderHelper;
import com.quirlen.textureloaderplus.TextureLoaderPlusMod;
import com.quirlen.textureloaderplus.config.TextureLoaderPlusConfig;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@Mixin(NativeImage.class)
public class NativeImageMixin {

    @ModifyVariable(method = "read(Ljava/io/InputStream;)Lnet/minecraft/client/texture/NativeImage;", at = @At("HEAD"), argsOnly = true)
    private static InputStream ensureBuffered(InputStream stream) {
        if (!stream.markSupported()) {
            return new BufferedInputStream(stream);
        }
        return stream;
    }

    @Inject(method = "read(Ljava/io/InputStream;)Lnet/minecraft/client/texture/NativeImage;", at = @At("HEAD"), cancellable = true)
    private static void read(InputStream stream, CallbackInfoReturnable<NativeImage> cir) throws IOException {
        if (!processCustomImage(stream, cir)) {
        }
    }

    @ModifyVariable(method = "read(Lnet/minecraft/client/texture/NativeImage$Format;Ljava/io/InputStream;)Lnet/minecraft/client/texture/NativeImage;", at = @At("HEAD"), argsOnly = true)
    private static InputStream ensureBuffered2(InputStream stream) {
        if (!stream.markSupported()) {
            return new BufferedInputStream(stream);
        }
        return stream;
    }

    @Inject(method = "read(Lnet/minecraft/client/texture/NativeImage$Format;Ljava/io/InputStream;)Lnet/minecraft/client/texture/NativeImage;", at = @At("HEAD"), cancellable = true)
    private static void read(NativeImage.Format format, InputStream stream, CallbackInfoReturnable<NativeImage> cir)
            throws IOException {
        processCustomImage(stream, cir);
    }

    private static boolean processCustomImage(InputStream stream, CallbackInfoReturnable<NativeImage> cir)
            throws IOException {

        if (TextureLoaderPlusConfig.enableCache
                && stream instanceof com.quirlen.textureloaderplus.caching.CachingInputStream) {
            com.quirlen.textureloaderplus.caching.CachingInputStream cStream = (com.quirlen.textureloaderplus.caching.CachingInputStream) stream;
            NativeImage cached = com.quirlen.textureloaderplus.caching.ImageCache.get(cStream.getId(),
                    cStream.getLastModified());
            if (cached != null) {
                if (TextureLoaderPlusConfig.debugLog) {
                    TextureLoaderPlusMod.LOGGER.info("Serving parsed image from cache for " + cStream.getId());
                }
                cir.setReturnValue(cached);
                return true;
            }
        }

        if (!stream.markSupported()) {
            return false;
        }

        stream.mark(4);
        byte[] header = new byte[4];
        int read = stream.read(header);
        stream.reset();

        if (read < 2)
            return false;

        boolean isCustom = false;
        String detectedFormat = null;

        String knownFormat = (stream instanceof com.quirlen.textureloaderplus.caching.CachingInputStream)
                ? ((com.quirlen.textureloaderplus.caching.CachingInputStream) stream).getFormat()
                : null;

        if (TextureLoaderPlusConfig.enablePSD && header[0] == '8' && header[1] == 'B' && header[2] == 'P'
                && header[3] == 'S') {
            isCustom = true;
            detectedFormat = "PSD";
        } else if (TextureLoaderPlusConfig.enableTIFF &&
                ((header[0] == 0x49 && header[1] == 0x49) || (header[0] == 0x4D && header[1] == 0x4D))) {
            isCustom = true;
            detectedFormat = "TIFF";
        } else if (TextureLoaderPlusConfig.enablePNM && header[0] == 'P' && header[1] >= '1' && header[1] <= '6') {
            isCustom = true;
            detectedFormat = "PNM";
        }

        if (!isCustom && knownFormat != null) {
            if (TextureLoaderPlusConfig.enablePSD && "psd".equalsIgnoreCase(knownFormat)) {
                isCustom = true;
                detectedFormat = "PSD";
            } else if (TextureLoaderPlusConfig.enableTIFF
                    && ("tiff".equalsIgnoreCase(knownFormat) || "tif".equalsIgnoreCase(knownFormat))) {
                isCustom = true;
                detectedFormat = "TIFF";
            } else if (TextureLoaderPlusConfig.enablePNM
                    && ("pnm".equalsIgnoreCase(knownFormat) || "ppm".equalsIgnoreCase(knownFormat))) {
                isCustom = true;
                detectedFormat = "PNM";
            }
        }

        if (isCustom) {
            if (TextureLoaderPlusConfig.debugLog) {
                TextureLoaderPlusMod.LOGGER.info("Detected " + detectedFormat + " file! Attempting to decode...");
            }
            try {
                NativeImage image = ImageReaderHelper.readImage(stream);
                if (image != null) {

                    if (TextureLoaderPlusConfig.enableCache
                            && stream instanceof com.quirlen.textureloaderplus.caching.CachingInputStream) {
                        com.quirlen.textureloaderplus.caching.CachingInputStream cStream = (com.quirlen.textureloaderplus.caching.CachingInputStream) stream;
                        com.quirlen.textureloaderplus.caching.ImageCache.put(cStream.getId(), cStream.getLastModified(),
                                image);
                    }

                    cir.setReturnValue(image);
                    return true;
                } else {
                    TextureLoaderPlusMod.LOGGER
                            .error("ImageReaderHelper returned null for " + detectedFormat + " file!");
                }
            } catch (Exception e) {
                TextureLoaderPlusMod.LOGGER.error("Failed to read " + detectedFormat + " file!", e);
                throw new IOException("Failed to load " + detectedFormat + " texture", e);
            }
        }
        return false;
    }
}
