package org.almond.minecraftgit.serialization;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Converts block states to and from the canonical repository string.
 *
 * <p>Format: {@code namespace:block[property=value,property=value]}. Properties
 * are sorted by name so captures are reproducible regardless of registry
 * iteration order. Unknown blocks and properties decode to air/default values
 * rather than aborting an apply.</p>
 */
public final class BlockStateCodec {

    private BlockStateCodec() {
    }

    public static String encode(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        List<Property<?>> properties = new ArrayList<>(state.getProperties());
        properties.sort(Comparator.comparing(Property::getName));
        if (properties.isEmpty()) {
            return id.toString();
        }
        StringBuilder builder = new StringBuilder(id.toString()).append('[');
        for (int i = 0; i < properties.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            Property<?> property = properties.get(i);
            builder.append(property.getName()).append('=').append(valueName(state, property));
        }
        return builder.append(']').toString();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static String valueName(BlockState state, Property property) {
        Object value = state.getValue(property);
        return property.getName((Comparable) value);
    }

    /** Registry-safe decode; a missing/unknown state becomes air. */
    public static BlockState decode(String blockData) {
        if (blockData == null || blockData.isBlank()) {
            return Blocks.AIR.defaultBlockState();
        }
        String base = blockData.trim();
        String propertyText = null;
        int open = base.indexOf('[');
        if (open >= 0 && base.endsWith("]")) {
            propertyText = base.substring(open + 1, base.length() - 1);
            base = base.substring(0, open);
        }
        ResourceLocation id = ResourceLocation.tryParse(base);
        if (id == null || !BuiltInRegistries.BLOCK.containsKey(id)) {
            return Blocks.AIR.defaultBlockState();
        }
        Block block = BuiltInRegistries.BLOCK.get(id);
        if (block == null) {
            return Blocks.AIR.defaultBlockState();
        }
        BlockState state = block.defaultBlockState();
        if (propertyText == null || propertyText.isBlank()) {
            return state;
        }
        for (String token : propertyText.split(",")) {
            int equals = token.indexOf('=');
            if (equals <= 0) {
                continue;
            }
            String name = token.substring(0, equals).trim();
            String value = token.substring(equals + 1).trim();
            Property<?> property = block.getStateDefinition().getProperty(name);
            if (property != null) {
                state = applyProperty(state, property, value);
            }
        }
        return state;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property,
                                                                      String value) {
        return property.getValue(value)
                .map(parsed -> state.setValue(property, parsed))
                .orElse(state);
    }

    public static boolean isAir(String blockData) {
        return blockData == null || decode(blockData).isAir();
    }

    /** True when the block id exists in this registry (properties are optional). */
    public static boolean isKnown(String blockData) {
        if (blockData == null || blockData.isBlank()) {
            return false;
        }
        String base = blockData.trim();
        int open = base.indexOf('[');
        if (open >= 0) {
            base = base.substring(0, open);
        }
        ResourceLocation id = ResourceLocation.tryParse(base);
        return id != null && BuiltInRegistries.BLOCK.containsKey(id);
    }
}
