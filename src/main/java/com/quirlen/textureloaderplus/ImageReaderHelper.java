package com.quirlen.textureloaderplus;

import com.quirlen.textureloaderplus.mixin.NativeImageAccessor;
import net.minecraft.client.texture.NativeImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageReaderHelper {

    static {
        javax.imageio.ImageIO.scanForPlugins();
    }

    public static NativeImage readImage(InputStream stream) throws IOException {
        ImageIO.scanForPlugins();

        BufferedImage bufferedImage = ImageIO.read(stream);

        if (bufferedImage == null) {
            String availableReaders = java.util.Arrays.toString(ImageIO.getReaderFormatNames());
            throw new IOException("Failed to read image. Available readers: " + availableReaders);
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = bufferedImage.getRGB(x, y);

                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = (argb) & 0xFF;

                int abgr = (alpha << 24) | (blue << 16) | (green << 8) | red;

                ((NativeImageAccessor) (Object) nativeImage).invokeSetColor(x, y, abgr);
            }
        }

        return nativeImage;
    }
}
