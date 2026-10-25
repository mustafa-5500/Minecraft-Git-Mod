package org.almond.minecraftgit.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.almond.minecraftgit.backup.BackupService;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.repo.CommitMeta;
import org.almond.minecraftgit.repo.PullPlan;
import org.almond.minecraftgit.repo.RepoMetadata;
import org.almond.minecraftgit.repo.RepoProgress;
import org.almond.minecraftgit.repo.RepoService;
import org.almond.minecraftgit.selection.SelectionException;
import org.almond.minecraftgit.sync.PullException;
import org.almond.minecraftgit.sync.PullService;
import org.almond.minecraftgit.util.UserFacingException;
import org.almond.minecraftgit.world.ApplyPlan;
import org.almond.minecraftgit.world.ApplyResult;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code /git pull} and {@code /git checkout}.
 *
 * <p>Pull is fast-forward-only and single-player-only for the apply step. The
 * local branch is moved only after every selected build has been written, so a
 * failed world write never leaves Git ahead of the world. A pre-apply backup is
 * written for each selection and can be restored with {@code --abort}.</p>
 */
public final class PullCommands {

    private PullCommands() {
    }

    public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services) {
        root.then(ClientCommandManager.literal("pull")
                .executes(context -> pull(context.getSource(), services, null, null))
                .then(ClientCommandManager.literal("--abort")
                        .executes(context -> abort(context.getSource(), services, null))
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .suggests(names(services))
                                .executes(context -> abort(context.getSource(), services,
                                        StringArgumentType.getString(context, "name")))))
                .then(ClientCommandManager.literal("--remote")
                        .then(ClientCommandManager.argument("remote", StringArgumentType.string())
                                .executes(context -> pull(context.getSource(), services, null,
                                        StringArgumentType.getString(context, "remote")))
                                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                        .suggests(names(services))
                                        .executes(context -> pull(context.getSource(), services,
                                                StringArgumentType.getString(context, "name"),
                                                StringArgumentType.getString(context, "remote"))))))
                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .suggests(names(services))
                        .executes(context -> pull(context.getSource(), services,
                                StringArgumentType.getString(context, "name"), null))));

        root.then(ClientCommandManager.literal("checkout")
                .then(ClientCommandManager.argument("commit", StringArgumentType.string())
                        .executes(context -> checkout(context.getSource(), services,
                                StringArgumentType.getString(context, "commit"), null))
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .suggests(names(services))
                                .executes(context -> checkout(context.getSource(), services,
                                        StringArgumentType.getString(context, "commit"),
                                        StringArgumentType.getString(context, "name"))))));
    }

    private static int pull(FabricClientCommandSource source, GitServices services,
                            String onlySelection, String requestedRemote) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            if (!world.singleplayer()) {
                throw new PullException("Applying a build requires a single-player world. "
                        + "Use /git fetch (and /git push) for repository sync in multiplayer.");
            }
            if (!services.repos().isInitialized(world.id())) {
                throw new PullException("No repository for this world yet. Run /git init first.");
            }
            RepoService repo = services.repos().service(world.id());
            String remote = remoteFor(services, world, requestedRemote);
            BlockPos playerOrigin = new BlockPos(player.blockPosition().getX(),
                    player.blockPosition().getY(), player.blockPosition().getZ());
            List<Selection> localSelections = services.selections().list(world.id(), player.getUUID());
            services.messages().send(source, "Fetching from " + remote + "...");

            CommandSupport.complete(services, source,
                    () -> repo.planPull(remote, RepoProgress.NOOP),
                    plan -> onPlan(source, services, world, player, localSelections, playerOrigin,
                            repo, plan, onlySelection));
            return 1;
        });
    }

    private static void onPlan(FabricClientCommandSource source, GitServices services, WorldContext world,
                               Player player, List<Selection> localSelections, BlockPos playerOrigin,
                               RepoService repo, PullPlan plan, String onlySelection) {
        if (plan.type() == PullPlan.Type.UP_TO_DATE) {
            services.messages().send(source, "Already up to date with " + plan.remote()
                    + "/" + plan.branch() + ".");
            return;
        }
        if (!plan.canFastForward()) {
            throw new PullException("Local and remote histories have diverged. Version 1 does not merge "
                    + "conflicting histories; push your local work or clone into a fresh world.");
        }
        services.messages().send(source, "Fast-forward available; preparing remote build(s)...");
        CommandSupport.complete(services, source,
                () -> services.pull().prepare(world.id(), localSelections, playerOrigin, repo, plan, onlySelection),
                prepared -> {
                    if (prepared.isEmpty()) {
                        finishPull(source, services, world, repo, plan, prepared);
                        return;
                    }
                    applyNext(source, services, world, player, repo, plan, prepared, 0);
                });
    }

    private static void applyNext(FabricClientCommandSource source, GitServices services, WorldContext world,
                                  Player player, RepoService repo, PullPlan plan,
                                  List<PullService.PreparedPull> prepared, int index) {
        if (index >= prepared.size()) {
            finishPull(source, services, world, repo, plan, prepared);
            return;
        }
        PullService.PreparedPull current = prepared.get(index);
        try {
            Selection selection = ensureSelection(services, world, player, current);
            services.messages().send(source, "Applying '" + current.name() + "' ("
                    + current.target().blockCount() + " blocks)...");
            ApplyPlan applyPlan = ApplyPlan.between(selection, current.localHead(), current.localOrigin(),
                    current.target(), current.localOrigin(), true);
            Minecraft client = source.getClient();
            services.apply().apply(world, applyPlan).whenComplete((result, error) -> client.execute(() -> {
                if (client.player == null || client.level == null) {
                    return;
                }
                if (error != null) {
                    fail(source, services, prepared, CommandSupport.unwrap(error));
                    return;
                }
                sendApplyResult(source, services, result);
                applyNext(source, services, world, player, repo, plan, prepared, index + 1);
            }));
        } catch (RuntimeException e) {
            fail(source, services, prepared, e);
        }
    }

    private static Selection ensureSelection(GitServices services, WorldContext world, Player player,
                                             PullService.PreparedPull prepared) {
        Selection selection = prepared.localSelection();
        boolean created = false;
        if (selection == null || services.selections().find(world.id(), player.getUUID(),
                prepared.name()).isEmpty()) {
            selection = services.selections().createAt(world.id(), player.getUUID(),
                    player.getName().getString(), prepared.name(), prepared.localOrigin());
            created = true;
        } else {
            selection = services.selections().get(world.id(), player.getUUID(), prepared.name());
            if (!selection.hasOrigin()) {
                selection.setOrigin(prepared.localOrigin());
                services.selections().save(world.id(), player.getUUID());
            }
        }
        if (selection.cuboidCount() == 0) {
            addCuboids(services, world, player, selection, prepared, created);
        }
        services.selections().setTracked(world.id(), player.getUUID(), selection.name(), true);
        return services.selections().get(world.id(), player.getUUID(), selection.name());
    }

    private static void addCuboids(GitServices services, WorldContext world, Player player,
                                   Selection selection, PullService.PreparedPull prepared, boolean created) {
        List<Cuboid> relative = new ArrayList<>();
        if (prepared.meta() != null && !prepared.meta().relativeCuboids().isEmpty()) {
            relative.addAll(prepared.meta().relativeCuboids());
        } else {
            Snapshot target = prepared.target();
            if (!target.blocks().isEmpty()) {
                int minX = Integer.MAX_VALUE;
                int minY = Integer.MAX_VALUE;
                int minZ = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int maxY = Integer.MIN_VALUE;
                int maxZ = Integer.MIN_VALUE;
                for (var block : target.blocks()) {
                    minX = Math.min(minX, block.rx());
                    minY = Math.min(minY, block.ry());
                    minZ = Math.min(minZ, block.rz());
                    maxX = Math.max(maxX, block.rx());
                    maxY = Math.max(maxY, block.ry());
                    maxZ = Math.max(maxZ, block.rz());
                }
                relative.add(new Cuboid(minX, minY, minZ, maxX, maxY, maxZ));
            }
        }
        try {
            for (Cuboid cuboid : relative) {
                BlockPos origin = prepared.localOrigin();
                services.selections().addCuboid(world.id(), player.getUUID(), selection.name(),
                        new Cuboid(cuboid.minX() + origin.x(), cuboid.minY() + origin.y(),
                                cuboid.minZ() + origin.z(), cuboid.maxX() + origin.x(),
                                cuboid.maxY() + origin.y(), cuboid.maxZ() + origin.z()));
            }
        } catch (RuntimeException e) {
            if (created) {
                services.selections().delete(world.id(), player.getUUID(), selection.name());
            }
            throw e;
        }
    }

    private static void finishPull(FabricClientCommandSource source, GitServices services, WorldContext world,
                                   RepoService repo, PullPlan plan, List<PullService.PreparedPull> prepared) {
        CommandSupport.complete(services, source, () -> {
            boolean moved = repo.fastForward(plan);
            services.pull().cleanup(prepared);
            return moved;
        }, moved -> {
            if (!moved) {
                services.messages().error(source, "Applied the remote build, but could not move the local branch.");
                return;
            }
            if (prepared.isEmpty()) {
                services.messages().success(source, "Fast-forwarded " + plan.branch() + " to "
                        + shortId(plan.remoteHead()) + " (remote has no selections).");
            } else {
                services.messages().success(source, "Pulled " + prepared.size() + " selection(s) from "
                        + plan.remote() + "/" + plan.branch() + " and fast-forwarded to "
                        + shortId(plan.remoteHead()) + ".");
            }
        });
    }

    private static void fail(FabricClientCommandSource source, GitServices services,
                             List<PullService.PreparedPull> prepared, Throwable error) {
        services.asyncRun(() -> services.pull().cleanup(prepared));
        services.messages().error(source, "Pull failed; the local branch was not moved. " + message(error));
    }

    private static int abort(FabricClientCommandSource source, GitServices services, String requested) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            if (!world.singleplayer()) {
                throw new PullException("Restoring a backup requires a single-player world.");
            }
            Selection selection = resolve(services, world, player, requested);
            BackupService.Backup backup = services.backups()
                    .latest(world.id(), player.getUUID(), selection.name())
                    .orElseThrow(() -> new PullException("No pre-apply backup exists for '"
                            + selection.name() + "'."));
            services.messages().send(source, "Restoring backup from " + backup.file().getFileName() + "...");
            ApplyPlan plan = ApplyPlan.restore(selection, backup.snapshot());
            CommandSupport.completeFuture(source, services.apply().apply(world, plan),
                    result -> sendApplyResult(source, services, result));
            return 1;
        });
    }

    private static int checkout(FabricClientCommandSource source, GitServices services,
                                String revision, String requested) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            if (!world.singleplayer()) {
                throw new PullException("Checkout requires a single-player world.");
            }
            if (!services.repos().isInitialized(world.id())) {
                throw new PullException("No repository for this world yet. Run /git init first.");
            }
            Selection selection = resolve(services, world, player, requested);
            if (!selection.hasOrigin()) {
                throw new PullException("Selection '" + selection.name() + "' has no origin yet.");
            }
            RepoService repo = services.repos().service(world.id());
            var commit = repo.resolve(revision)
                    .orElseThrow(() -> new PullException("Unknown commit or revision '" + revision + "'."));
            services.messages().send(source, "Reading " + shortId(commit) + "...");
            CommandSupport.complete(services, source,
                    () -> services.pull().prepareCheckout(selection, repo, commit),
                    prepared -> {
                        // The local build lives at the selection's local origin, not
                        // at the absolute origin recorded in the remote commit meta.
                        ApplyPlan plan = ApplyPlan.between(selection, prepared.localHead(),
                                selection.origin(), prepared.target(), selection.origin(), true);
                        services.messages().send(source, "Applying " + shortId(commit) + " into '"
                                + selection.name() + "'...");
                        CommandSupport.completeFuture(source, services.apply().apply(world, plan), result -> {
                            sendApplyResult(source, services, result);
                            services.asyncRun(() -> services.pull().cleanup(prepared.tempDirectory()));
                        });
                    });
            return 1;
        });
    }

    private static void sendApplyResult(FabricClientCommandSource source, GitServices services,
                                        ApplyResult result) {
        services.messages().success(source, "Applied: " + result.summary()
                + (result.backup() == null ? "" : " (backup " + result.backup().getFileName() + ")") + ".");
        for (int i = 0; i < Math.min(5, result.warnings().size()); i++) {
            services.messages().warn(source, result.warnings().get(i));
        }
    }

    private static Selection resolve(GitServices services, WorldContext world, Player player, String requested) {
        String name = requested != null && !requested.isBlank()
                ? requested
                : services.selectionManager().active(world, player);
        if (name == null || name.isBlank()) {
            throw new SelectionException(
                    "Specify a selection name, or select one with /git selection select <name>.");
        }
        return services.selections().get(world.id(), player.getUUID(), name);
    }

    private static String remoteFor(GitServices services, WorldContext world, String requested) {
        if (requested != null && !requested.isBlank()) {
            return requested;
        }
        RepoMetadata metadata = services.repos().metadata(world.id());
        return metadata.remoteName() == null || metadata.remoteName().isBlank()
                ? services.config().defaultRemote : metadata.remoteName();
    }

    private static Player requirePlayer(FabricClientCommandSource source) {
        Player player = source.getPlayer();
        if (player == null) {
            throw new SelectionException("That command can only be run by a player.");
        }
        return player;
    }

    private static WorldContext world(FabricClientCommandSource source) {
        return WorldContext.current(source.getClient());
    }

    private static String shortId(org.eclipse.jgit.lib.ObjectId id) {
        return id == null ? "?" : id.abbreviate(8).name();
    }

    private static String message(Throwable error) {
        return error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
    }

    private static com.mojang.brigadier.suggestion.SuggestionProvider<FabricClientCommandSource> names(
            GitServices services) {
        return (context, builder) -> {
            try {
                FabricClientCommandSource source = context.getSource();
                Player player = source.getPlayer();
                if (player != null) {
                    WorldContext world = WorldContext.current(source.getClient());
                    for (Selection selection : services.selections().list(world.id(), player.getUUID())) {
                        builder.suggest(selection.name());
                    }
                }
            } catch (RuntimeException ignored) {
                // Suggestions must never break command parsing.
            }
            return builder.buildFuture();
        };
    }
}
