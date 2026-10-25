package org.almond.minecraftgit.client.command;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import org.almond.minecraftgit.MinecraftGit;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.util.UserFacingException;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Shared plumbing for client commands: player checks, one-line error handling,
 * and returning async results to the client thread.
 */
public final class CommandSupport {

    private CommandSupport() {
    }

    public static FabricClientCommandSource requirePlayer(FabricClientCommandSource source) {
        if (source.getPlayer() == null) {
            throw new UserFacingException("That command can only be run by a player.");
        }
        return source;
    }

    public static int run(FabricClientCommandSource source, Runnable action) {
        try {
            action.run();
            return 1;
        } catch (Throwable error) {
            handleError(source, error);
            return 0;
        }
    }

    public static int runInt(FabricClientCommandSource source, Supplier<Integer> action) {
        try {
            return action.get();
        } catch (Throwable error) {
            handleError(source, error);
            return 0;
        }
    }

    /**
     * Run a blocking task on the IO executor, then invoke {@code success} on the
     * client thread. If the player left the world while the task was running,
     * the result is silently dropped.
     */
    public static <T> void complete(GitServices services, FabricClientCommandSource source,
                                    Supplier<T> task, Consumer<T> success) {
        Minecraft client = source.getClient();
        services.async(task).whenComplete((value, error) -> client.execute(() -> {
            if (client.player == null || client.level == null) {
                return;
            }
            if (error != null) {
                handleError(source, unwrap(error));
                return;
            }
            try {
                success.accept(value);
            } catch (Throwable failure) {
                handleError(source, failure);
            }
        }));
    }

    public static void handleError(FabricClientCommandSource source, Throwable error) {
        Throwable cause = unwrap(error);
        if (cause instanceof UserFacingException) {
            source.sendError(net.minecraft.network.chat.Component.literal(cause.getMessage()));
            return;
        }
        if (cause instanceof IllegalArgumentException) {
            source.sendError(net.minecraft.network.chat.Component.literal(cause.getMessage()));
            return;
        }
        MinecraftGit.LOGGER.error("Git operation failed", cause);
        String message = cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
        source.sendError(net.minecraft.network.chat.Component.literal(
                "Git operation failed: " + message + " (see the game log for details)"));
    }

    public static Throwable unwrap(Throwable error) {
        Throwable current = error;
        while ((current instanceof CompletionException || current instanceof ExecutionException)
                && current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    public static String join(String[] args, int from) {
        if (from >= args.length) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = from; i < args.length; i++) {
            if (i > from) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

    public static String lower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
