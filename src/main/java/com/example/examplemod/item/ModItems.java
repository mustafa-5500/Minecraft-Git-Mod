package com.example.examplemod.item;

import java.util.function.Function;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import com.example.examplemod.ExampleMod;

/**
 * Registers the mod's items.
 *
 * <p>Item identifiers live in the {@code examplemod} namespace and are used by
 * registries, models, textures and translations.</p>
 */
public final class ModItems {
    public static final Item EXAMPLE_ITEM = register(
            "example_item",
            Item::new,
            new Item.Properties().stacksTo(16)
    );

    private ModItems() {
    }

    /**
     * Creates an item with the given registry name and registers it.
     */
    public static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
        return Registry.register(BuiltInRegistries.ITEM, ExampleMod.id(name), factory.apply(properties));
    }

    /**
     * Called from {@link ExampleMod#onInitialize()} to force static initialisation.
     */
    public static void initialize() {
        // Add the item to the vanilla Ingredients creative tab.
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> entries.accept(EXAMPLE_ITEM));
    }
}
