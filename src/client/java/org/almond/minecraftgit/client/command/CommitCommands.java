package org.almond.minecraftgit.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.world.entity.player.Player;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.commit.CommitRequest;
import org.almond.minecraftgit.commit.CommitResult;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.selection.SelectionException;

import java.util.ArrayList;
import java.util.List;

/** {@code /git commit [message]}: write all staged selections to the repository. */
public final class CommitCommands {

    private CommitCommands() {
    }

    public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services) {
        root.then(ClientCommandManager.literal("commit")
                .executes(context -> commit(context.getSource(), services, null, false))
                .then(ClientCommandManager.literal("--empty")
                        .executes(context -> commit(context.getSource(), services, null, true))
                        .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                                .executes(context -> commit(context.getSource(), services,
                                        StringArgumentType.getString(context, "message"), true))))
                .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                        .executes(context -> commit(context.getSource(), services,
                                StringArgumentType.getString(context, "message"), false))));
    }

    private static int commit(FabricClientCommandSource source, GitServices services,
                              String message, boolean allowEmpty) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            if (!services.repos().isInitialized(world.id())) {
                throw new SelectionException("No repository for this world yet. Run /git init first.");
            }

            List<Selection> staged = new ArrayList<>();
            for (Selection selection : services.selections().list(world.id(), player.getUUID())) {
                if (services.staging().hasStaged(world.id(), player.getUUID(), selection.name())) {
                    staged.add(selection);
                }
            }
            if (staged.isEmpty() && !allowEmpty) {
                throw new SelectionException("Nothing is staged. Use /git add <selection> first.");
            }

            CommitRequest request = new CommitRequest(player.getName().getString(), player.getUUID().toString(),
                    world.id(), world.gameMode(source.getClient()), List.copyOf(staged), message, allowEmpty);
            services.messages().send(source, "Committing " + staged.size() + " staged selection(s)...");
            CommandSupport.complete(services, source,
                    () -> services.commits().commit(request),
                    result -> showResult(source, services, result));
            return 1;
        });
    }

    private static void showResult(FabricClientCommandSource source, GitServices services, CommitResult result) {
        services.messages().success(source, "Committed " + result.shortId() + " ("
                + result.selections().size() + " selection(s)): +" + result.added()
                + " ~" + result.changed() + " -" + result.removed()
                + ", " + result.entityChanges() + " entity change(s).");
        String firstLine = result.message() == null ? "" : result.message().split("\\R", 2)[0];
        services.messages().raw(source, "  " + firstLine);
        if (result.selections().isEmpty()) {
            services.messages().warn(source, "This was an empty commit; no selection files changed.");
        }
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
}
