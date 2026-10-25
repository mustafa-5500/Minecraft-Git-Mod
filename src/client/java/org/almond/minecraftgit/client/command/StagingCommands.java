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
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.selection.SelectionException;
import org.almond.minecraftgit.staging.StagedSelection;

import java.util.List;

/** {@code /git add} and {@code /git rm}: stage and untrack a selection. */
public final class StagingCommands {

    private StagingCommands() {
    }

    public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services) {
        root.then(ClientCommandManager.literal("add")
                .executes(context -> add(context.getSource(), services, null))
                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .suggests(names(services))
                        .executes(context -> add(context.getSource(), services,
                                StringArgumentType.getString(context, "name")))));

        root.then(ClientCommandManager.literal("rm")
                .executes(context -> rm(context.getSource(), services, null))
                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .suggests(names(services))
                        .executes(context -> rm(context.getSource(), services,
                                StringArgumentType.getString(context, "name")))));
    }

    private static int add(FabricClientCommandSource source, GitServices services, String requested) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            if (!services.repos().isInitialized(world.id())) {
                throw new SelectionException("No repository for this world yet. Run /git init first.");
            }
            Selection selection = resolve(services, world, player, requested);
            if (selection.cuboidCount() == 0) {
                throw new SelectionException("Selection '" + selection.name() + "' has no cuboids yet.");
            }
            services.messages().send(source, "Capturing '" + selection.name() + "'"
                    + (world.singleplayer() ? " on the integrated server" : " from loaded client chunks") + "...");

            var client = source.getClient();
            WorldAccess.capture(services, world, selection).whenComplete((capture, error) ->
                    client.execute(() -> {
                        if (client.player == null || client.level == null) {
                            return;
                        }
                        if (error != null) {
                            CommandSupport.handleError(source, CommandSupport.unwrap(error));
                            return;
                        }
                        services.messages().send(source, capture.summary());
                        for (int i = 0; i < Math.min(5, capture.warnings().size()); i++) {
                            services.messages().warn(source, capture.warnings().get(i));
                        }
                        CommandSupport.complete(services, source,
                                () -> services.staging().stage(selection, capture.snapshot()),
                                staged -> onStaged(source, services, world, player, selection, staged));
                    }));
            return 1;
        });
    }

    private static void onStaged(FabricClientCommandSource source, GitServices services, WorldContext world,
                                 Player player, Selection selection, StagedSelection staged) {
        services.selections().setTracked(world.id(), player.getUUID(), selection.name(), true);
        services.selectionManager().setActive(world, player, selection.name());
        services.messages().success(source, "Staged '" + selection.name() + "': "
                + staged.blockCount() + " block(s), " + staged.entityCount() + " entity(ies), origin "
                + staged.snapshot().origin() + ".");
        services.messages().send(source, "Next: /git commit" + (staged.snapshot().blockCount() == 0
                ? " (this will record an empty selection)" : "") + ".");
    }

    private static int rm(FabricClientCommandSource source, GitServices services, String requested) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            if (!services.repos().isInitialized(world.id())) {
                throw new SelectionException("No repository for this world yet. Run /git init first.");
            }
            Selection selection = resolve(services, world, player, requested);
            CommandSupport.complete(services, source, () -> {
                services.staging().unstage(world.id(), player.getUUID(), selection.name());
                return selection;
            }, ignored -> {
                services.selections().setTracked(world.id(), player.getUUID(), selection.name(), false);
                services.messages().success(source, "Untracked '" + selection.name()
                        + "' and removed its staged snapshot.");
            });
            return 1;
        });
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
                    List<Selection> selections = services.selections().list(world.id(), player.getUUID());
                    for (Selection selection : selections) {
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
