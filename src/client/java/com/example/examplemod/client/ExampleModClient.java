package com.example.examplemod.client;

import net.fabricmc.api.ClientModInitializer;

import com.example.examplemod.ExampleMod;

/**
 * Client-only entrypoint.
 *
 * <p>Code in {@code src/client} is only loaded on the physical client, so it is
 * safe to reference rendering, input and other client classes here.</p>
 */
public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ExampleMod.LOGGER.info("Example Mod client initialized.");
    }
}
