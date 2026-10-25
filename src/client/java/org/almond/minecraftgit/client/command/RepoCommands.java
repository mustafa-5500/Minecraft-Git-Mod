package org.almond.minecraftgit.client.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.server.level.ServerPlayer;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.repo.CommitInfo;
import org.almond.minecraftgit.repo.PullPlan;
import org.almond.minecraftgit.repo.RemoteInfo;
import org.almond.minecraftgit.repo.RepoInfo;
import org.almond.minecraftgit.repo.RepoMetadata;
import org.almond.minecraftgit.repo.RepoProgress;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.util.Names;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Repository lifecycle and transport commands. */
public final class RepoCommands {

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private RepoCommands() {
    }

    public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services) {
        root.then(ClientCommandManager.literal("init")
                .executes(context -> init(context.getSource(), services)));

        root.then(ClientCommandManager.literal("repo")
                .executes(context -> repo(context.getSource(), services)));

        attachRemote(root, services);

        root.then(ClientCommandManager.literal("log")
                .executes(context -> log(context.getSource(), services, 10))
                .then(ClientCommandManager.argument("count", IntegerArgumentType.integer(1, 100))
                        .executes(context -> log(context.getSource(), services,
                                IntegerArgumentType.getInteger(context, "count")))));

        root.then(ClientCommandManager.literal("push")
                .executes(context -> push(context.getSource(), services, false))
                .then(ClientCommandManager.literal("--force")
                        .executes(context -> push(context.getSource(), services, true))));

        root.then(ClientCommandManager.literal("fetch")
                .executes(context -> fetch(context.getSource(), services, null))
                .then(ClientCommandManager.argument("remote", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            try {
                                WorldContext world = currentWorld(context.getSource());
                                if (services.repos().isInitialized(world.id())) {
                                    for (RemoteInfo remote : services.repos().service(world.id()).remoteList()) {
                                        builder.suggest(remote.name());
                                    }
                                }
                            } catch (RuntimeException ignored) {
                                // Suggestions must never break command parsing.
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> fetch(context.getSource(), services,
                                StringArgumentType.getString(context, "remote")))));
    }

    /** Adds the author subcommand to the config literal built by {@link GitCommand}. */
    public static void attachConfig(LiteralArgumentBuilder<FabricClientCommandSource> config,
                                    GitServices services) {
        config.then(ClientCommandManager.literal("author")
                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .then(ClientCommandManager.argument("email", StringArgumentType.string())
                                .executes(context -> setAuthor(context.getSource(), services,
                                        StringArgumentType.getString(context, "name"),
                                        StringArgumentType.getString(context, "email"))))));
    }

    private static void attachRemote(LiteralArgumentBuilder<FabricClientCommandSource> root,
                                     GitServices services) {
        root.then(ClientCommandManager.literal("remote")
                .executes(context -> remoteList(context.getSource(), services))
                .then(ClientCommandManager.literal("list")
                        .executes(context -> remoteList(context.getSource(), services)))
                .then(ClientCommandManager.literal("add")
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .then(ClientCommandManager.argument("url", StringArgumentType.greedyString())
                                        .executes(context -> remoteAdd(context.getSource(), services,
                                                StringArgumentType.getString(context, "name"),
                                                StringArgumentType.getString(context, "url"))))))
                .then(ClientCommandManager.literal("remove")
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> remoteRemove(context.getSource(), services,
                                        StringArgumentType.getString(context, "name"))))));
    }

    private static int init(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.runInt(source, () -> {
            WorldContext world = currentWorld(source);
            String playerName = source.getPlayer() == null ? services.config().committerName
                    : source.getPlayer().getName().getString();
            services.messages().send(source, "Initializing repository for " + world.displayName()
                    + " (" + world.dimension().location() + ")...");
            CommandSupport.complete(services, source,
                    () -> services.repos().init(world.id(), playerName, null),
                    info -> {
                        services.messages().success(source, "Repository ready at " + info.path());
                        services.messages().send(source, "Branch " + info.branch() + ", HEAD "
                                + (info.hasCommits() ? shortId(info.headId()) + " (" + info.headSummary() + ")"
                                : "empty"));
                        if (!world.singleplayer()) {
                            services.messages().warn(source,
                                    "Applying builds later requires single-player; repo commands work here.");
                        }
                    });
            return 1;
        });
    }

    private static int repo(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.runInt(source, () -> {
            WorldContext world = currentWorld(source);
            if (!services.repos().isInitialized(world.id())) {
                services.messages().send(source, "No repository for this world yet. Run /git init.");
                return 1;
            }
            CommandSupport.complete(services, source,
                    () -> services.repos().service(world.id()).info(),
                    info -> showRepo(source, services, world, info));
            return 1;
        });
    }

    private static void showRepo(FabricClientCommandSource source, GitServices services,
                                 WorldContext world, RepoInfo info) {
        services.messages().raw(source, "Repository: " + world.id());
        services.messages().raw(source, "  path: " + info.path());
        services.messages().raw(source, "  branch: " + info.branch());
        services.messages().raw(source, "  HEAD: " + (info.hasCommits()
                ? shortId(info.headId()) + " " + info.headSummary() : "(no commits)"));
        services.messages().raw(source, "  author: " + info.authorName() + " <" + info.authorEmail() + ">");
        services.messages().raw(source, "  remote: " + (info.remoteUrl() == null || info.remoteUrl().isBlank()
                ? "(none)" : info.remoteName() + " -> " + info.remoteUrl()));
        services.messages().raw(source, "  working tree: " + (info.clean() ? "clean" : "dirty"));
    }

    private static int setAuthor(FabricClientCommandSource source, GitServices services, String name, String email) {
        return CommandSupport.runInt(source, () -> {
            WorldContext world = currentWorld(source);
            if (name.isBlank() || email.isBlank()) {
                throw new org.almond.minecraftgit.util.UserFacingException(
                        "Author name and email must not be blank.");
            }
            CommandSupport.complete(services, source,
                    () -> {
                        services.repos().setAuthor(world.id(), name, email);
                        return name;
                    },
                    ignored -> services.messages().success(source,
                            "Commit identity set to " + name + " <" + email + "> for " + world.id() + "."));
            return 1;
        });
    }

    private static int remoteAdd(FabricClientCommandSource source, GitServices services,
                                 String name, String url) {
        return CommandSupport.runInt(source, () -> {
            WorldContext world = currentWorld(source);
            String target = url.trim();
            CommandSupport.complete(services, source,
                    () -> {
                        services.repos().service(world.id()).remoteAdd(name, target);
                        return name;
                    },
                    ignored -> services.messages().success(source,
                            "Remote '" + name + "' -> " + target + "."));
            return 1;
        });
    }

    private static int remoteRemove(FabricClientCommandSource source, GitServices services, String name) {
        return CommandSupport.runInt(source, () -> {
            WorldContext world = currentWorld(source);
            CommandSupport.complete(services, source,
                    () -> {
                        services.repos().service(world.id()).remoteRemove(name);
                        return name;
                    },
                    ignored -> services.messages().success(source, "Removed remote '" + name + "'."));
            return 1;
        });
    }

    private static int remoteList(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.runInt(source, () -> {
            WorldContext world = currentWorld(source);
            CommandSupport.complete(services, source,
                    () -> services.repos().service(world.id()).remoteList(),
                    remotes -> {
                        if (remotes.isEmpty()) {
                            services.messages().send(source, "No remotes configured. "
                                    + "Use /git remote add origin <url>.");
                            return;
                        }
                        for (RemoteInfo remote : remotes) {
                            services.messages().raw(source, "  " + remote.name() + " -> " + remote.url());
                        }
                    });
            return 1;
        });
    }

    private static int log(FabricClientCommandSource source, GitServices services, int count) {
        return CommandSupport.runInt(source, () -> {
            WorldContext world = currentWorld(source);
            if (!services.repos().isInitialized(world.id())) {
                services.messages().send(source, "No repository for this world yet. Run /git init.");
                return 1;
            }
            CommandSupport.complete(services, source,
                    () -> services.repos().service(world.id()).log(count),
                    commits -> showLog(source, services, commits));
            return 1;
        });
    }

    private static void showLog(FabricClientCommandSource source, GitServices services, List<CommitInfo> commits) {
        if (commits.isEmpty()) {
            services.messages().send(source, "No commits yet.");
            return;
        }
        services.messages().raw(source, "Recent commits:");
        for (CommitInfo commit : commits) {
            services.messages().raw(source, "  " + commit.shortId() + "  " + DATE.format(commit.when())
                    + "  " + commit.author() + "  " + commit.message());
        }
    }

    private static int push(FabricClientCommandSource source, GitServices services, boolean force) {
        return CommandSupport.runInt(source, () -> {
            WorldContext world = currentWorld(source);
            if (!services.repos().isInitialized(world.id())) {
                throw new org.almond.minecraftgit.util.UserFacingException(
                        "No repository for this world yet. Run /git init first.");
            }
            RepoMetadata metadata = services.repos().metadata(world.id());
            String remote = metadata.remoteName() == null || metadata.remoteName().isBlank()
                    ? services.config().defaultRemote : metadata.remoteName();
            services.messages().send(source, "Pushing to " + remote + "...");
            CommandSupport.complete(services, source,
                    () -> services.repos().service(world.id()).push(remote, force, RepoProgress.NOOP),
                    summary -> {
                        if (summary.upToDate()) {
                            services.messages().send(source, "Remote is already up to date.");
                        } else {
                            services.messages().success(source, "Pushed " + summary.branch() + " to "
                                    + summary.remote() + " (" + summary.updatedRefs() + " ref update(s)).");
                        }
                    });
            return 1;
        });
    }

    private static int fetch(FabricClientCommandSource source, GitServices services, String requestedRemote) {
        return CommandSupport.runInt(source, () -> {
            WorldContext world = currentWorld(source);
            if (!services.repos().isInitialized(world.id())) {
                throw new org.almond.minecraftgit.util.UserFacingException(
                        "No repository for this world yet. Run /git init first.");
            }
            RepoMetadata metadata = services.repos().metadata(world.id());
            String remote = requestedRemote == null || requestedRemote.isBlank()
                    ? (metadata.remoteName() == null || metadata.remoteName().isBlank()
                    ? services.config().defaultRemote : metadata.remoteName())
                    : requestedRemote;
            services.messages().send(source, "Fetching from " + remote + "...");
            CommandSupport.complete(services, source,
                    () -> services.repos().service(world.id()).fetch(remote, RepoProgress.NOOP),
                    summary -> services.messages().success(source, "Fetched from " + summary.remote()
                            + " (" + summary.updatedRefs() + " ref update(s))."));
            return 1;
        });
    }

    private static WorldContext currentWorld(FabricClientCommandSource source) {
        return WorldContext.current(source.getClient());
    }

    private static String shortId(String id) {
        return id == null || id.length() < 8 ? id : id.substring(0, 8);
    }
}
