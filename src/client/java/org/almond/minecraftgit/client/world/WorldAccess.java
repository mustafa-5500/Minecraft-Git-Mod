package org.almond.minecraftgit.client.world;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.almond.minecraftgit.client.GitServices;
import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.model.Snapshot;
import org.almond.minecraftgit.util.UserFacingException;
import org.almond.minecraftgit.world.CaptureResult;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Chooses where a capture runs. In single-player the integrated server owns the
 * authoritative world and can load chunks; in multiplayer only client-loaded
 * chunks are available.
 */
public final class WorldAccess {

    private WorldAccess() {
    }

    public static CompletableFuture<CaptureResult> capture(GitServices services, WorldContext world,
                                                           Selection selection) {
        Minecraft client = Minecraft.getInstance();
        if (world.singleplayer()) {
            MinecraftServer server = client.getSingleplayerServer();
            ServerLevel level = world.serverLevel(client);
            if (server == null || level == null) {
                return CompletableFuture.failedFuture(new UserFacingException(
                        "The integrated server for this dimension is not available."));
            }
            CompletableFuture<CaptureResult> future = new CompletableFuture<>();
            server.execute(() -> {
                try {
                    future.complete(services.snapshots().capture(selection, level, true));
                } catch (Throwable error) {
                    future.completeExceptionally(error);
                }
            });
            return future;
        }
        return captureOnClientThread(client, () ->
                services.snapshots().capture(selection, client.level, false));
    }

    public static CompletableFuture<Snapshot> capturePositions(GitServices services, WorldContext world,
                                                               String selectionName, BlockPos origin,
                                                               Collection<BlockPos> positions) {
        Minecraft client = Minecraft.getInstance();
        if (world.singleplayer()) {
            MinecraftServer server = client.getSingleplayerServer();
            ServerLevel level = world.serverLevel(client);
            if (server == null || level == null) {
                return CompletableFuture.failedFuture(new UserFacingException(
                        "The integrated server for this dimension is not available."));
            }
            CompletableFuture<Snapshot> future = new CompletableFuture<>();
            server.execute(() -> {
                try {
                    future.complete(services.snapshots().capturePositions(
                            level, selectionName, origin, positions, true));
                } catch (Throwable error) {
                    future.completeExceptionally(error);
                }
            });
            return future;
        }
        if (client.level == null) {
            return CompletableFuture.failedFuture(new UserFacingException("Open a world first."));
        }
        return CompletableFuture.completedFuture(services.snapshots().capturePositions(
                client.level, selectionName, origin, positions, false));
    }

    private static <T> CompletableFuture<T> captureOnClientThread(Minecraft client,
                                                                  java.util.function.Supplier<T> task) {
        if (client.level == null) {
            return CompletableFuture.failedFuture(new UserFacingException("Open a world first."));
        }
        if (client.isSameThread()) {
            try {
                return CompletableFuture.completedFuture(task.get());
            } catch (Throwable error) {
                return CompletableFuture.failedFuture(error);
            }
        }
        CompletableFuture<T> future = new CompletableFuture<>();
        client.execute(() -> {
            try {
                future.complete(task.get());
            } catch (Throwable error) {
                future.completeExceptionally(error);
            }
        });
        return future;
    }
}
