package org.almond.minecraftgit.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.world.entity.player.Player;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.client.world.WorldAccess;
import org.almond.minecraftgit.model.BlockSnapshot;
import org.almond.minecraftgit.model.BuildDiff;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.selection.SelectionException;
import org.almond.minecraftgit.status.StatusService;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/** {@code /git status} and {@code /git diff}. */
public final class StatusCommands {

    private StatusCommands() {
    }

    public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services) {
        root.then(ClientCommandManager.literal("status")
                .executes(context -> status(context.getSource(), services)));

        root.then(ClientCommandManager.literal("diff")
                .executes(context -> diff(context.getSource(), services, null))
                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .suggests(names(services))
                        .executes(context -> diff(context.getSource(), services,
                                StringArgumentType.getString(context, "name")))));
    }

    private static int status(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            List<Selection> selections = services.selections().list(world.id(), player.getUUID());
            if (selections.isEmpty()) {
                services.messages().send(source, "No selections yet. Use /git wand and /git selection create.");
                return 1;
            }

            boolean initialized = services.repos().isInitialized(world.id());
            List<Selection> candidates = new ArrayList<>();
            Set<String> tooLarge = new HashSet<>();
            for (Selection selection : selections) {
                boolean inHead = initialized && Files.isDirectory(
                        services.repos().repoPath(world.id()).resolve(selection.name()));
                boolean staged = services.staging().hasStaged(world.id(), player.getUUID(), selection.name());
                if (selection.cuboidCount() == 0 || (!selection.isTracked() && !inHead && !staged)) {
                    continue;
                }
                if (selection.volume() > services.config().statusMaxVolume) {
                    tooLarge.add(selection.name());
                } else {
                    candidates.add(selection);
                }
            }

            CompletableFuture<Map<String, Snapshot>> chain = CompletableFuture.completedFuture(new LinkedHashMap<>());
            for (Selection selection : candidates) {
                chain = chain.thenCompose(map -> WorldAccess.capture(services, world, selection)
                        .thenApply(result -> {
                            map.put(selection.name(), result.snapshot());
                            return map;
                        }));
            }

            CompletableFuture<StatusService.Report> report = chain.thenCompose(current ->
                    services.async(() -> services.status().report(world.id(), player.getUUID(),
                            selections, current, tooLarge)));
            CommandSupport.completeFuture(source, report, value -> showStatus(source, services, world, value));
            return 1;
        });
    }

    private static void showStatus(FabricClientCommandSource source, GitServices services, WorldContext world,
                                   StatusService.Report report) {
        services.messages().raw(source, "Status for " + world.id() + (report.repoInitialized()
                ? "  branch " + report.branch() + (report.head().isBlank() ? "" : " @ " + report.head())
                : "  (no repository yet)"));
        if (report.selections().isEmpty()) {
            services.messages().raw(source, "  (no selections)");
        }
        for (StatusService.SelectionStatus status : report.selections()) {
            StringBuilder line = new StringBuilder("  ");
            line.append(status.name());
            if (status.untracked()) {
                line.append("  [untracked]");
            } else if (status.clean()) {
                line.append("  [clean]");
            } else {
                line.append("  [dirty]");
            }
            line.append("  cuboids=").append(status.cuboidCount()).append(" volume=").append(status.volume());
            services.messages().raw(source, line.toString());
            if (status.staged()) {
                services.messages().raw(source, "      staged:  " + status.stagedDiff().summary());
            }
            if (status.currentChecked()) {
                services.messages().raw(source, "      working: " + status.workingDiff().summary());
            }
            if (status.note() != null) {
                services.messages().warn(source, "      " + status.note());
            }
        }
        services.messages().raw(source, "Summary: " + report.dirtyCount() + " dirty, "
                + report.untrackedCount() + " untracked, " + report.cleanCount() + " clean.");
    }

    private static int diff(FabricClientCommandSource source, GitServices services, String requested) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            Selection selection = resolve(services, world, player, requested);
            boolean tooLarge = selection.volume() > services.config().statusMaxVolume;

            CompletableFuture<Snapshot> currentFuture = tooLarge
                    ? CompletableFuture.completedFuture(null)
                    : WorldAccess.capture(services, world, selection)
                    .thenApply(result -> result.snapshot());
            CompletableFuture<StatusService.DiffReport> diffFuture = currentFuture.thenCompose(current ->
                    services.async(() -> services.status().diff(world.id(), player.getUUID(), selection,
                            current, tooLarge)));
            CommandSupport.completeFuture(source, diffFuture,
                    report -> showDiff(source, services, selection, report));
            return 1;
        });
    }

    private static void showDiff(FabricClientCommandSource source, GitServices services, Selection selection,
                                 StatusService.DiffReport report) {
        StatusService.SelectionStatus status = report.status();
        services.messages().raw(source, "Diff for '" + selection.name() + "' on " + selection.world());
        services.messages().raw(source, "  HEAD: " + (status.inHead() ? "present" : "absent")
                + "   staged: " + (status.staged() ? "yes" : "no")
                + "   current: " + (status.currentChecked() ? "captured" : "not captured"));
        if (status.note() != null) {
            services.messages().warn(source, "  " + status.note());
        }
        services.messages().raw(source, "  staged vs HEAD:  " + report.stagedDiff().summary());
        services.messages().raw(source, "  current vs base: " + report.workingDiff().summary());
        showSamples(source, "added", report.workingDiff().added());
        showSamples(source, "changed", report.workingDiff().changed());
        showSamples(source, "removed", report.workingDiff().removed());
    }

    private static void showSamples(FabricClientCommandSource source, String label,
                                    List<BlockSnapshot> blocks) {
        if (blocks.isEmpty()) {
            return;
        }
        for (int i = 0; i < Math.min(6, blocks.size()); i++) {
            BlockSnapshot block = blocks.get(i);
            source.sendFeedback(net.minecraft.network.chat.Component.literal(
                    "    " + label + " (" + block.rx() + "," + block.ry() + "," + block.rz() + ") "
                            + block.blockData()));
        }
        if (blocks.size() > 6) {
            source.sendFeedback(net.minecraft.network.chat.Component.literal(
                    "    ... " + (blocks.size() - 6) + " more " + label));
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

    private static SuggestionProvider<FabricClientCommandSource> names(GitServices services) {
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
