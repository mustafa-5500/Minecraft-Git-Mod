package org.almond.minecraftgit.client.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.client.selection.SelectionManager;
import org.almond.minecraftgit.client.selection.SelectionRenderer;
import org.almond.minecraftgit.client.selection.WandEvents;
import org.almond.minecraftgit.client.selection.WandItem;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.selection.SelectionException;

import java.util.List;
import java.util.Locale;

/** {@code /git selection ...}: create and manage named multi-cuboid selections. */
public final class SelectionCommands {

    private SelectionCommands() {
    }

    public static void attach(LiteralArgumentBuilder<FabricClientCommandSource> root, GitServices services) {
        root.then(ClientCommandManager.literal("selection")
                .executes(context -> usage(context.getSource(), services))
                .then(ClientCommandManager.literal("create")
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> create(context.getSource(), services,
                                        StringArgumentType.getString(context, "name")))))
                .then(ClientCommandManager.literal("list")
                        .executes(context -> list(context.getSource(), services)))
                .then(ClientCommandManager.literal("select")
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .suggests(names(services))
                                .executes(context -> select(context.getSource(), services,
                                        StringArgumentType.getString(context, "name")))))
                .then(ClientCommandManager.literal("show")
                        .executes(context -> show(context.getSource(), services, null))
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .suggests(names(services))
                                .executes(context -> show(context.getSource(), services,
                                        StringArgumentType.getString(context, "name")))))
                .then(ClientCommandManager.literal("add")
                        .executes(context -> add(context.getSource(), services, null))
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .suggests(names(services))
                                .executes(context -> add(context.getSource(), services,
                                        StringArgumentType.getString(context, "name")))))
                .then(ClientCommandManager.literal("confirm")
                        .executes(context -> confirm(context.getSource(), services)))
                .then(ClientCommandManager.literal("cancel")
                        .executes(context -> cancel(context.getSource(), services)))
                .then(ClientCommandManager.literal("remove")
                        .then(ClientCommandManager.argument("index", IntegerArgumentType.integer(1))
                                .executes(context -> remove(context.getSource(), services,
                                        IntegerArgumentType.getInteger(context, "index"), null))
                                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                        .suggests(names(services))
                                        .executes(context -> remove(context.getSource(), services,
                                                IntegerArgumentType.getInteger(context, "index"),
                                                StringArgumentType.getString(context, "name"))))))
                .then(ClientCommandManager.literal("reorigin")
                        .executes(context -> reorigin(context.getSource(), services, null))
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .suggests(names(services))
                                .executes(context -> reorigin(context.getSource(), services,
                                        StringArgumentType.getString(context, "name")))))
                .then(ClientCommandManager.literal("delete")
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .suggests(names(services))
                                .executes(context -> delete(context.getSource(), services,
                                        StringArgumentType.getString(context, "name")))))
                .then(ClientCommandManager.literal("info")
                        .executes(context -> info(context.getSource(), services, null))
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .suggests(names(services))
                                .executes(context -> info(context.getSource(), services,
                                        StringArgumentType.getString(context, "name")))))
                .then(ClientCommandManager.literal("wand")
                        .executes(context -> wand(context.getSource(), services)))
                .then(ClientCommandManager.literal("pos1")
                        .executes(context -> position(context.getSource(), services, true)))
                .then(ClientCommandManager.literal("pos2")
                        .executes(context -> position(context.getSource(), services, false))));

        root.then(ClientCommandManager.literal("wand")
                .executes(context -> wand(context.getSource(), services)));
    }

    private static int usage(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.run(source, () -> {
            services.messages().raw(source, "Selection commands:");
            services.messages().raw(source, "  /git selection create <name>");
            services.messages().raw(source, "  /git selection list | select <name> | info [name]");
            services.messages().raw(source, "  /git selection add [name] | confirm | cancel");
            services.messages().raw(source, "  /git selection remove <index> [name] | reorigin [name]");
            services.messages().raw(source, "  /git selection delete <name> | show [name] | wand | pos1 | pos2");
            services.messages().raw(source, "Wand: left-click = corner A, right-click = corner B. "
                    + "A completed box waits for confirm or cancel.");
        });
    }

    private static int create(FabricClientCommandSource source, GitServices services, String name) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            SelectionManager manager = services.selectionManager();
            SelectionManager.PendingCuboid pending = manager.takePending(player);
            Selection selection = null;
            try {
                selection = services.selections().create(world.id(), player.getUUID(),
                        player.getName().getString(), name);
                if (pending != null) {
                    try {
                        services.selections().addCuboid(world.id(), player.getUUID(), name, pending.cuboid());
                    } catch (SelectionException e) {
                        services.selections().delete(world.id(), player.getUUID(), name);
                        selection = null;
                        throw e;
                    }
                }
                manager.setActive(world, player, name);
                services.messages().success(source, "Created selection '" + name + "'"
                        + (selection.cuboidCount() > 0 ? " with the pending cuboid" : "")
                        + ". Origin: " + selection.origin() + ".");
            } catch (SelectionException e) {
                if (selection == null && pending != null) {
                    manager.setPending(player, pending);
                }
                throw e;
            }
            return 1;
        });
    }

    private static int list(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            List<Selection> selections = services.selections().list(world.id(), player.getUUID());
            if (selections.isEmpty()) {
                services.messages().send(source, "No selections yet. Create one, then click two corners "
                        + "with /git wand.");
                return 1;
            }
            services.messages().raw(source, "Selections in " + world.id() + ":");
            String active = services.selectionManager().active(world, player);
            for (Selection selection : selections) {
                String marker = selection.name().equalsIgnoreCase(active) ? "*" : " ";
                services.messages().raw(source, " " + marker + " " + selection.name()
                        + "  cuboids=" + selection.cuboidCount()
                        + "  volume=" + selection.volume()
                        + "  origin=" + (selection.hasOrigin() ? selection.origin() : "unset")
                        + (selection.isTracked() ? "  [tracked]" : ""));
            }
            return 1;
        });
    }

    private static int select(FabricClientCommandSource source, GitServices services, String name) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            Selection selection = services.selections().get(world.id(), player.getUUID(), name);
            services.selectionManager().setActive(world, player, selection.name());
            services.messages().success(source, "Active selection: " + selection.name() + ".");
            return 1;
        });
    }

    private static int show(FabricClientCommandSource source, GitServices services, String name) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            Selection selection = resolve(services, world, player, name);
            if (selection.cuboidCount() == 0) {
                services.messages().send(source, "Selection '" + selection.name() + "' has no cuboids yet.");
                return 1;
            }
            SelectionRenderer.highlight(world.id(), selection.name(), 8);
            services.messages().success(source, "Showing '" + selection.name() + "' for 8 seconds ("
                    + selection.cuboidCount() + " cuboid(s)).");
            return 1;
        });
    }

    private static int add(FabricClientCommandSource source, GitServices services, String name) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            SelectionManager manager = services.selectionManager();
            SelectionManager.PendingCuboid pending = manager.takePending(player);
            if (pending == null) {
                services.messages().send(source, "No pending cuboid. Click two corners with /git wand first.");
                return 1;
            }
            Selection selection = resolve(services, world, player, name);
            try {
                services.selections().addCuboid(world.id(), player.getUUID(), selection.name(), pending.cuboid());
            } catch (SelectionException e) {
                manager.setPending(player, pending);
                throw e;
            }
            manager.setActive(world, player, selection.name());
            services.messages().success(source, "Added " + pending.cuboid().dimensions() + " to '"
                    + selection.name() + "' (" + selection.cuboidCount() + " cuboid(s)).");
            return 1;
        });
    }

    private static int confirm(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            SelectionManager manager = services.selectionManager();
            SelectionManager.PendingCuboid pending = manager.takePending(player);
            if (pending == null) {
                services.messages().send(source, "No pending cuboid to confirm. "
                        + "Click two corners with /git wand first.");
                return 1;
            }
            String target = pending.selectionName() != null && !pending.selectionName().isBlank()
                    ? pending.selectionName()
                    : manager.active(world, player);
            if (target == null || target.isBlank()) {
                manager.setPending(player, pending);
                services.messages().warn(source, "No active selection. Create one with "
                        + "/git selection create <name> or add the cuboid with /git selection add <name>.");
                return 1;
            }
            try {
                Selection selection = services.selections().addCuboid(world.id(), player.getUUID(),
                        target, pending.cuboid());
                manager.setActive(world, player, selection.name());
                services.messages().success(source, "Added " + pending.cuboid() + " to '"
                        + selection.name() + "' (" + selection.cuboidCount() + " cuboid(s)).");
            } catch (SelectionException e) {
                manager.setPending(player, pending);
                throw e;
            }
            return 1;
        });
    }

    private static int cancel(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            SelectionManager.PendingCuboid pending = services.selectionManager().takePending(player);
            if (pending == null) {
                services.messages().send(source, "No pending cuboid to cancel.");
                return 1;
            }
            services.messages().success(source, "Discarded pending cuboid " + pending.cuboid() + ".");
            return 1;
        });
    }

    private static int remove(FabricClientCommandSource source, GitServices services, int index, String name) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            Selection selection = resolve(services, world, player, name);
            Cuboid removed = services.selections().removeCuboid(world.id(), player.getUUID(),
                    selection.name(), index);
            CommandSupport.complete(services, source, () -> {
                services.staging().restrictStaged(world.id(), player.getUUID(), selection);
                return removed;
            }, ignored -> services.messages().success(source, "Removed cuboid " + index + " ("
                    + removed.dimensions() + ") from '" + selection.name() + "'. Origin stays at "
                    + selection.origin() + "."));
            return 1;
        });
    }

    private static int reorigin(FabricClientCommandSource source, GitServices services, String name) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            Selection selection = resolve(services, world, player, name);
            BlockPos previous = services.selections().reorigin(world.id(), player.getUUID(), selection.name());
            CommandSupport.complete(services, source, () -> {
                services.staging().rebaseStaged(world.id(), player.getUUID(), selection.name(),
                        selection.origin());
                return previous;
            }, ignored -> services.messages().success(source, "Re-origined '" + selection.name() + "' from "
                    + (previous == null ? "unset" : previous) + " to " + selection.origin() + "."));
            return 1;
        });
    }

    private static int delete(FabricClientCommandSource source, GitServices services, String name) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            Selection selection = services.selections().get(world.id(), player.getUUID(), name);
            services.selections().delete(world.id(), player.getUUID(), selection.name());
            String active = services.selectionManager().active(world, player);
            if (selection.name().equalsIgnoreCase(active)) {
                services.selectionManager().setActive(world, player, "");
            }
            CommandSupport.complete(services, source, () -> {
                services.staging().unstage(world.id(), player.getUUID(), selection.name());
                return null;
            }, ignored -> services.messages().success(source, "Deleted selection '" + selection.name() + "'."));
            return 1;
        });
    }

    private static int info(FabricClientCommandSource source, GitServices services, String name) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            WorldContext world = world(source);
            Selection selection = resolve(services, world, player, name);
            services.messages().raw(source, "Selection: " + selection.name()
                    + (selection.isTracked() ? " [tracked]" : " [untracked]"));
            services.messages().raw(source, "  world: " + selection.world());
            services.messages().raw(source, "  origin: " + (selection.hasOrigin() ? selection.origin() : "unset"));
            services.messages().raw(source, "  cuboids: " + selection.cuboidCount() + ", volume: " + selection.volume());
            if (selection.cuboidCount() > 0) {
                services.messages().raw(source, "  bounds: " + selection.boundsMin() + " .. " + selection.boundsMax());
            }
            int index = 1;
            for (Cuboid cuboid : selection.cuboids()) {
                services.messages().raw(source, "  " + index++ + ". " + cuboid + "  volume=" + cuboid.volume());
            }
            return 1;
        });
    }

    private static int wand(FabricClientCommandSource source, GitServices services) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            if (WandItem.give(player, services.config(), source.getClient())) {
                services.messages().success(source, "Git Wand given. Left-click = corner A, right-click = corner B.");
            } else {
                services.messages().warn(source, "In multiplayer the server owns your inventory. "
                        + "Hold " + services.config().wandItem + " while sneaking, or use "
                        + "/git selection pos1 and pos2.");
            }
            return 1;
        });
    }

    private static int position(FabricClientCommandSource source, GitServices services, boolean first) {
        return CommandSupport.runInt(source, () -> {
            Player player = requirePlayer(source);
            Minecraft client = source.getClient();
            if (!(client.hitResult instanceof BlockHitResult hit)) {
                services.messages().send(source, "Look at a block within reach, then try again.");
                return 1;
            }
            var pos = hit.getBlockPos();
            WandEvents.handleCorner(services, player, new BlockPos(pos.getX(), pos.getY(), pos.getZ()), first);
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
