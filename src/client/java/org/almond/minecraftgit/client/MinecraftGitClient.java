package org.almond.minecraftgit.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.almond.minecraftgit.MinecraftGit;
import org.almond.minecraftgit.client.command.GitCommand;
import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.debug.DebugLogger;

import java.nio.file.Path;

/**
 * Client entrypoint. All commands, world access and rendering are client-side;
 * the mod is marked {@code "environment": "client"} and has no server
 * entrypoint.
 */
public final class MinecraftGitClient implements ClientModInitializer {

    private static GitServices services;

    @Override
    public void onInitializeClient() {
        Path root = FabricLoader.getInstance().getConfigDir().resolve(MinecraftGit.MOD_ID);
        ModConfig config = ModConfig.load(root);
        DebugLogger debug = new DebugLogger(config.logsRoot(), config.debug, config.debugBlocks);
        services = new GitServices(root, config, debug);

        try {
            Class.forName("org.eclipse.jgit.api.Git");
            MinecraftGit.LOGGER.info("JGit is available to Minecraft Git");
        } catch (ClassNotFoundException e) {
            MinecraftGit.LOGGER.error("JGit is missing from the mod jar; repository commands will fail", e);
        }

        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> GitCommand.register(dispatcher, services));

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            if (services != null) {
                services.close();
                services = null;
            }
        });

        MinecraftGit.LOGGER.info("Minecraft Git initialized; local data is under {}", root);
    }

    public static GitServices services() {
        GitServices current = services;
        if (current == null) {
            throw new IllegalStateException("Minecraft Git is not initialized.");
        }
        return current;
    }
}
