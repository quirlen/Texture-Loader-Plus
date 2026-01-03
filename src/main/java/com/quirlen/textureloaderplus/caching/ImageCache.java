package com.quirlen.textureloaderplus.caching;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ImageCache {

    private static final Map<Identifier, CacheEntry> CACHE = new HashMap<>();

    public static class CacheEntry {
        public final NativeImage image;
        public final long lastModified;

        public CacheEntry(NativeImage image, long lastModified) {
            this.image = image;
            this.lastModified = lastModified;
        }
    }

    public static NativeImage get(Identifier id, long currentModified) {
        CacheEntry entry = CACHE.get(id);
        if (entry != null) {
            if (entry.lastModified == currentModified) {
                int w = entry.image.getWidth();
                int h = entry.image.getHeight();

                NativeImage copy = new NativeImage(entry.image.getFormat(), w, h, false);
                copy.copyFrom(entry.image);
                return copy;
            } else {
                CACHE.remove(id).image.close();
            }
        }
        return null;
    }

    public static void put(Identifier id, long lastModified, NativeImage image) {
        if (CACHE.containsKey(id)) {
            CACHE.get(id).image.close();
        }

        NativeImage cacheCopy = new NativeImage(image.getFormat(), image.getWidth(), image.getHeight(), false);
        cacheCopy.copyFrom(image);

        CACHE.put(id, new CacheEntry(cacheCopy, lastModified));
    }
}
