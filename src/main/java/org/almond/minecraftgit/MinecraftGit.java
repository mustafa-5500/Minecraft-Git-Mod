package org.almond.minecraftgit;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared identity for the client-side Minecraft Git mod.
 *
 * <p>This class lives in {@code src/main} so the repository and serialization
 * layers can refer to the mod id without pulling in client-only classes.</p>
 */
public final class MinecraftGit {
    public static final String MOD_ID = "minecraftgit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private MinecraftGit() {
    }

    /** Creates a {@link ResourceLocation} in this mod's namespace. */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
