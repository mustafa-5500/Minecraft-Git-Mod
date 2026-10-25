package com.example.examplemod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.examplemod.command.ExampleCommand;
import com.example.examplemod.item.ModItems;

/**
 * Main entrypoint for the example mod.
 *
 * <p>This class is referenced from the {@code main} entrypoint in
 * {@code fabric.mod.json}. Keep code that must run on both the client and the
 * dedicated server here.</p>
 */
public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "examplemod";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Touch ModItems so its static fields are evaluated and the item is registered.
        ModItems.initialize();

        // Register the /examplemod command.
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                ExampleCommand.register(dispatcher));

        LOGGER.info("Example Mod initialized. Try /examplemod give in game.");
    }

    /**
     * Creates a {@link ResourceLocation} in this mod's namespace.
     */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
