package org.almond.minecraftgit.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import org.almond.minecraftgit.MinecraftGit;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.config.ModConfig;

/**
 * Root {@code /git} command. Phases add subcommands here; the root and help
 * command stay stable so players always have a discovery path.
 */
public final class GitCommand {

    private GitCommand() {
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, GitServices services) {
        LiteralArgumentBuilder<FabricClientCommandSource> config = ClientCommandManager.literal("config")
                .executes(context -> showConfig(context.getSource(), services))
                .then(ClientCommandManager.literal("show")
                        .executes(context -> showConfig(context.getSource(), services)))
                .then(ClientCommandManager.literal("debug")
                        .executes(context -> toggleDebug(context.getSource(), services)))
                .then(ClientCommandManager.literal("blocks")
                        .executes(context -> toggleBlocks(context.getSource(), services)));
        RepoCommands.attachConfig(config, services);

        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal("git")
                .executes(context -> help(context, services))
                .then(ClientCommandManager.literal("help")
                        .executes(context -> help(context, services)))
                .then(ClientCommandManager.literal("version")
                        .executes(context -> version(context.getSource(), services)))
                .then(config)
                .then(ClientCommandManager.literal("debug")
                        .executes(context -> toggleDebug(context.getSource(), services)));

        RepoCommands.attach(root, services);
        SelectionCommands.attach(root, services);
        dispatcher.register(root);
    }

    private static int help(CommandContext<FabricClientCommandSource> context, GitServices services) {
        FabricClientCommandSource source = context.getSource();
        return CommandSupport.run(source, () -> {
            source.sendFeedback(net.minecraft.network.chat.Component.literal("Minecraft Git")
                    .withStyle(net.minecraft.ChatFormatting.AQUA));
            services.messages().raw(source, "Client-side Git for Minecraft builds.");
            services.messages().raw(source, "");
            services.messages().raw(source, "Available now:");
            services.messages().raw(source, "  /git help              this help");
            services.messages().raw(source, "  /git version           mod and Minecraft versions");
            services.messages().raw(source, "  /git init              create this world's repository");
            services.messages().raw(source, "  /git repo              repository information");
            services.messages().raw(source, "  /git config show       effective settings");
            services.messages().raw(source, "  /git config author <name> <email>");
            services.messages().raw(source, "  /git config debug      toggle the debug log");
            services.messages().raw(source, "  /git remote add|remove|list");
            services.messages().raw(source, "  /git log [count]");
            services.messages().raw(source, "  /git push [--force], /git fetch [remote]");
            services.messages().raw(source, "  /git selection create|list|select|show|add|remove|reorigin|delete|info");
            services.messages().raw(source, "  /git selection wand|pos1|pos2, /git wand");
            services.messages().raw(source, "");
            services.messages().raw(source, "Coming in the next phases:");
            services.messages().raw(source, "  /git add, rm, commit, status, diff");
            services.messages().raw(source, "  /git pull, checkout, pull --abort");
            services.messages().raw(source, "");
            services.messages().raw(source, "Data folder: " + services.root());
        });
    }

    private static int version(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.run(source, () -> {
            String modVersion = FabricLoader.getInstance().getModContainer(MinecraftGit.MOD_ID)
                    .map(container -> container.getMetadata().getVersion().getFriendlyString())
                    .orElse("development");
            services.messages().send(source, "Minecraft Git " + modVersion
                    + " | Minecraft " + FabricLoader.getInstance().getModContainer("minecraft")
                    .map(container -> container.getMetadata().getVersion().getFriendlyString())
                    .orElse("unknown"));
        });
    }

    private static int showConfig(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.run(source, () -> {
            ModConfig config = services.config();
            services.messages().raw(source, "Minecraft Git settings");
            services.messages().raw(source, "  data: " + services.root());
            services.messages().raw(source, "  branch: " + config.branch + "   remote: " + config.defaultRemote);
            services.messages().raw(source, "  wand: " + config.wandItem + "   prefix: \"" + config.prefix + "\"");
            services.messages().raw(source, "  author: " + config.committerName + " <" + config.committerEmail + ">");
            services.messages().raw(source, "  limits: " + config.maxCuboidsPerSelection + " cuboids, "
                    + config.maxVolumePerSelection + " blocks/selection, "
                    + config.maxBlocksPerCommit + " blocks/commit, "
                    + config.maxEntitiesPerSelection + " entities");
            services.messages().raw(source, "  apply: " + config.blocksPerTick + " blocks/tick, "
                    + config.maxBackups + " backups");
            services.messages().raw(source, "  debug: " + config.debug + " (blocks: " + config.debugBlocks + ")");
        });
    }

    private static int toggleDebug(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.run(source, () -> {
            ModConfig config = services.config();
            config.debug = !config.debug;
            services.debug().setEnabled(config.debug);
            config.save();
            services.messages().send(source, "Debug logging " + (config.debug ? "enabled" : "disabled")
                    + (config.debug ? " (" + services.debug().file() + ")" : ""));
        });
    }

    private static int toggleBlocks(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.run(source, () -> {
            ModConfig config = services.config();
            config.debugBlocks = !config.debugBlocks;
            services.debug().setBlocks(config.debugBlocks);
            config.save();
            services.messages().send(source, "Per-block debug logging " + (config.debugBlocks ? "enabled" : "disabled")
                    + (config.debugBlocks && !config.debug ? " (also enable /git config debug)" : ""));
        });
    }
}
