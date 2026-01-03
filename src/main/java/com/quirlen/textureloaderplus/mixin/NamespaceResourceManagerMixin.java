package com.quirlen.textureloaderplus.mixin;

import com.quirlen.textureloaderplus.TextureLoaderPlusMod;
import com.quirlen.textureloaderplus.config.TextureLoaderPlusConfig;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin {

    @Shadow
    public abstract Optional<Resource> getResource(Identifier id);

    @Inject(method = "getResource", at = @At("RETURN"), cancellable = true)
    private void onGetResource(Identifier id, CallbackInfoReturnable<Optional<Resource>> cir) {
        if (id.getPath().endsWith(".png")) {
            String path = id.getPath();
            String basePath = path.substring(0, path.length() - 4);

            java.util.List<String> checkOrder = new java.util.ArrayList<>();
            String primaryExt = TextureLoaderPlusConfig.getEffectivePrimaryExtension();
            if (primaryExt != null && !primaryExt.isEmpty()) {
                checkOrder.add(primaryExt);
            }

            if (TextureLoaderPlusConfig.enablePSD && !checkOrder.contains("psd"))
                checkOrder.add("psd");
            if (TextureLoaderPlusConfig.enableTIFF && !checkOrder.contains("tiff"))
                checkOrder.add("tiff");
            if (TextureLoaderPlusConfig.enablePNM && !checkOrder.contains("pnm"))
                checkOrder.add("pnm");

            for (String ext : checkOrder) {
                if (ext == null || ext.isEmpty())
                    continue;

                if (ext.equalsIgnoreCase("png")) {
                    if (cir.getReturnValue().isPresent()) {
                        return;
                    }
                    continue;
                }

                boolean enabled = false;
                if (ext.equalsIgnoreCase("psd") && TextureLoaderPlusConfig.enablePSD)
                    enabled = true;
                else if ((ext.equalsIgnoreCase("tiff") || ext.equalsIgnoreCase("tif"))
                        && TextureLoaderPlusConfig.enableTIFF)
                    enabled = true;
                else if ((ext.equalsIgnoreCase("pnm") || ext.equalsIgnoreCase("ppm"))
                        && TextureLoaderPlusConfig.enablePNM)
                    enabled = true;

                if (enabled) {
                    if (checkAndReturn(id, basePath + "." + ext, cir))
                        return;

                    if (ext.equalsIgnoreCase("tiff")) {
                        if (checkAndReturn(id, basePath + ".tif", cir))
                            return;
                    }
                    if (ext.equalsIgnoreCase("pnm")) {
                        if (checkAndReturn(id, basePath + ".ppm", cir))
                            return;
                    }
                }
            }
        }
    }

    private boolean checkAndReturn(Identifier originalId, String newPath,
            CallbackInfoReturnable<Optional<Resource>> cir) {
        if (originalId.getPath().equals(newPath))
            return false;

        Identifier newId = Identifier.of(originalId.getNamespace(), newPath);
        Optional<Resource> res = this.getResource(newId);
        if (res.isPresent()) {
            if (TextureLoaderPlusConfig.debugLog) {
                TextureLoaderPlusMod.LOGGER.info("Found override for " + originalId + " -> " + newId);
            }
            Resource original = res.get();
            Resource cached = new com.quirlen.textureloaderplus.caching.CachedResource(original, newId,
                    newPath.substring(newPath.lastIndexOf('.') + 1));

            cir.setReturnValue(Optional.of(cached));
            return true;
        }
        return false;
    }

    @Inject(method = "findResources", at = @At("RETURN"))
    private void onFindResources(String startingPath, Predicate<Identifier> allowedPathPredicate,
            CallbackInfoReturnable<Map<Identifier, Resource>> cir) {
        Map<Identifier, Resource> resources = cir.getReturnValue();

        try {
            for (Identifier id : resources.keySet().stream().toList()) {
                if (id.getPath().endsWith(".png")) {
                    String basePath = id.getPath().substring(0, id.getPath().length() - 4);

                    boolean replacedForThisId = false;
                    java.util.List<String> checkOrder = new java.util.ArrayList<>();
                    String primaryExt = TextureLoaderPlusConfig.getEffectivePrimaryExtension();
                    if (primaryExt != null && !primaryExt.isEmpty()) {
                        checkOrder.add(primaryExt);
                    }
                    if (TextureLoaderPlusConfig.enablePSD && !checkOrder.contains("psd"))
                        checkOrder.add("psd");
                    if (TextureLoaderPlusConfig.enableTIFF && !checkOrder.contains("tiff"))
                        checkOrder.add("tiff");
                    if (TextureLoaderPlusConfig.enablePNM && !checkOrder.contains("pnm"))
                        checkOrder.add("pnm");

                    for (String ext : checkOrder) {
                        if (ext == null || ext.isEmpty())
                            continue;

                        if (ext.equalsIgnoreCase("png")) {
                            break;
                        }

                        boolean enabled = false;
                        if (ext.equalsIgnoreCase("psd") && TextureLoaderPlusConfig.enablePSD)
                            enabled = true;
                        else if ((ext.equalsIgnoreCase("tiff") || ext.equalsIgnoreCase("tif"))
                                && TextureLoaderPlusConfig.enableTIFF)
                            enabled = true;
                        else if ((ext.equalsIgnoreCase("pnm") || ext.equalsIgnoreCase("ppm"))
                                && TextureLoaderPlusConfig.enablePNM)
                            enabled = true;

                        if (enabled) {
                            if (checkAndReplace(resources, id, basePath + "." + ext)) {
                                replacedForThisId = true;
                                break;
                            }
                            if (ext.equalsIgnoreCase("tiff")) {
                                if (checkAndReplace(resources, id, basePath + ".tif")) {
                                    replacedForThisId = true;
                                    break;
                                }
                            }
                            if (ext.equalsIgnoreCase("pnm")) {
                                if (checkAndReplace(resources, id, basePath + ".ppm")) {
                                    replacedForThisId = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (replacedForThisId) {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            TextureLoaderPlusMod.LOGGER.warn("Failed to modify findResources map: " + e.getMessage());
        }
    }

    private boolean checkAndReplace(Map<Identifier, Resource> resources, Identifier originalId, String newPath) {
        Identifier newId = Identifier.of(originalId.getNamespace(), newPath);
        Optional<Resource> res = this.getResource(newId);
        if (res.isPresent()) {
            if (TextureLoaderPlusConfig.debugLog) {
                TextureLoaderPlusMod.LOGGER.info("findResources: Replacing " + originalId + " with " + newId);
            }
            Resource original = res.get();
            Resource cached = new com.quirlen.textureloaderplus.caching.CachedResource(original, newId,
                    newPath.substring(newPath.lastIndexOf('.') + 1));

            resources.put(originalId, cached);
            return true;
        }
        return false;
    }
}
