package com.quirlen.textureloaderplus.caching;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

public class CachedResource extends Resource {

    private final Resource original;
    private final Identifier id;
    private final String fileExtension;

    public CachedResource(Resource original, Identifier id, String fileExtension) {
        super(original.getPack(), original::getInputStream);
        this.original = original;
        this.id = id;
        this.fileExtension = fileExtension;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new CachingInputStream(original.getInputStream(), id, System.currentTimeMillis() / 10000, fileExtension);
    }

    @Override
    public ResourcePack getPack() {
        return original.getPack();
    }

    @Override
    public ResourceMetadata getMetadata() throws IOException {
        return original.getMetadata();
    }
}
