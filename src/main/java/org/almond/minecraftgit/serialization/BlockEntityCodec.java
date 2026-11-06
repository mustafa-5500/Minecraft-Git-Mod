package org.almond.minecraftgit.serialization;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Serializes block-entity data as native SNBT, prefixed with {@code nbt:} inside
 * the CSV cell. Unlike the Paper plugin's typed JSON subset this can carry the
 * full data the client knows about, including inventories and component data.
 *
 * <p>Payloads written by the plugin start with {@code {}} and are deliberately
 * not parsed here; the block itself still applies and the caller can warn about
 * the unsupported payload.</p>
 */
public final class BlockEntityCodec {

    public static final String PREFIX = "nbt:";

    private BlockEntityCodec() {
    }

    /** @return the payload, or {@code null} when there is nothing worth storing. */
    public static String encode(BlockEntity blockEntity, HolderLookup.Provider registries) {
        CompoundTag tag = blockEntity.saveWithFullMetadata(registries);
        return tag.isEmpty() ? null : PREFIX + tag;
    }

    public static boolean isNative(String payload) {
        return payload != null && payload.startsWith(PREFIX);
    }

    /**
     * Apply a native payload to a block entity that already exists at the target
     * position. Returns false for plugin/unknown payloads instead of guessing.
     */
    public static boolean apply(BlockEntity blockEntity, String payload, HolderLookup.Provider registries) {
        if (payload == null || payload.isBlank()) {
            return false;
        }
        if (!isNative(payload)) {
            return false;
        }
        try {
            CompoundTag tag = TagParser.parseTag(payload.substring(PREFIX.length()));
            blockEntity.loadWithComponents(tag, registries);
            blockEntity.setChanged();
            return true;
        } catch (CommandSyntaxException | RuntimeException e) {
            throw new SerializationException("Invalid block-entity NBT payload: " + e.getMessage(), e);
        }
    }

    public static int payloadBytes(String payload) {
        return payload == null ? 0 : payload.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
    }
}
