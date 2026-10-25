package org.almond.minecraftgit.serialization;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.almond.minecraftgit.model.EntitySnapshot;

/**
 * Serializes entities as SNBT plus their registry type id. The position and
 * rotation live in {@link EntitySnapshot} so a build can be shifted to a new
 * origin without rewriting NBT.
 */
public final class EntityCodec {

    public static final String PREFIX = "nbt:";

    private EntityCodec() {
    }

    public static String encode(Entity entity) {
        CompoundTag tag = entity.saveWithoutId(new CompoundTag());
        return PREFIX + tag;
    }

    /**
     * Create and add the entity at an absolute position. Returns {@code null}
     * when the payload or type is not loadable in this world.
     */
    public static Entity apply(ServerLevel level, EntitySnapshot snapshot,
                               double x, double y, double z) {
        String raw = snapshot.data();
        if (raw == null || raw.isBlank()) {
            return null;
        }
        if (raw.startsWith(PREFIX)) {
            raw = raw.substring(PREFIX.length());
        }
        try {
            CompoundTag tag = TagParser.parseTag(raw);
            tag.putString("id", snapshot.type());
            Entity entity = EntityType.loadEntityRecursive(tag, level, loaded -> loaded);
            if (entity == null) {
                return null;
            }
            entity.setPos(x, y, z);
            entity.setYRot(snapshot.yaw());
            entity.setXRot(snapshot.pitch());
            level.addFreshEntity(entity);
            return entity;
        } catch (CommandSyntaxException | RuntimeException e) {
            throw new SerializationException("Could not restore entity " + snapshot.type() + ": "
                    + e.getMessage(), e);
        }
    }

    public static int payloadBytes(String payload) {
        return payload == null ? 0 : payload.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
    }
}
