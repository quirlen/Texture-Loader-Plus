package com.quirlen.textureloaderplus.caching;

import java.io.FilterInputStream;
import java.io.InputStream;
import net.minecraft.util.Identifier;

public class CachingInputStream extends FilterInputStream {

    private final Identifier id;
    private final long lastModified;
    private final String format;

    public CachingInputStream(InputStream in, Identifier id, long lastModified, String format) {
        super(in);
        this.id = id;
        this.lastModified = lastModified;
        this.format = format;
    }

    public Identifier getId() {
        return id;
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getFormat() {
        return format;
    }
}
