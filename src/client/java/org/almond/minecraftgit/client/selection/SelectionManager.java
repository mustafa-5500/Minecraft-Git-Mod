package org.almond.minecraftgit.client.selection;

import org.almond.minecraftgit.client.WorldContext;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.repo.RepoManager;
import org.almond.minecraftgit.selection.SelectionService;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-player wand session state: the two pending corners, the completed cuboid
 * waiting for confirmation, and the active selection name. The durable
 * selection list lives in {@link SelectionService}; the remembered active name
 * is stored in local repo metadata so it survives a restart.
 */
public final class SelectionManager {

    private final SelectionService selections;
    private final RepoManager repos;
    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    public SelectionManager(SelectionService selections, RepoManager repos) {
        this.selections = selections;
        this.repos = repos;
    }

    public Session session(Player player) {
        return sessions.computeIfAbsent(player.getUUID(), ignored -> new Session());
    }

    /** Active selection name, or {@code null} when none is remembered/valid. */
    public String active(WorldContext world, Player player) {
        String name = repos.lastSelection(world.id());
        if (name == null || name.isBlank()) {
            return null;
        }
        return selections.find(world.id(), player.getUUID(), name).isPresent() ? name : null;
    }

    public void setActive(WorldContext world, Player player, String name) {
        repos.setLastSelection(world.id(), name);
    }

    public Optional<Cuboid> setCorner(Player player, boolean first, BlockPos pos) {
        Session session = session(player);
        if (first) {
            session.cornerA = pos;
        } else {
            session.cornerB = pos;
        }
        if (session.cornerA != null && session.cornerB != null) {
            Cuboid cuboid = Cuboid.of(session.cornerA, session.cornerB);
            session.cornerA = null;
            session.cornerB = null;
            return Optional.of(cuboid);
        }
        return Optional.empty();
    }

    public PendingCuboid takePending(Player player) {
        Session session = session(player);
        PendingCuboid pending = session.pending;
        session.pending = null;
        return pending;
    }

    public void setPending(Player player, Cuboid cuboid) {
        setPending(player, new PendingCuboid(cuboid, null));
    }

    public void setPending(Player player, Cuboid cuboid, String selectionName) {
        setPending(player, new PendingCuboid(cuboid, selectionName));
    }

    public void setPending(Player player, PendingCuboid pending) {
        session(player).pending = pending;
    }

    public Cuboid peekPending(Player player) {
        PendingCuboid pending = session(player).pending;
        return pending == null ? null : pending.cuboid();
    }

    public void clear(Player player) {
        sessions.remove(player.getUUID());
    }

    /**
     * A completed cuboid that has not been applied yet. The selection name is the
     * selection it was built for, or {@code null} when no selection was active.
     */
    public record PendingCuboid(Cuboid cuboid, String selectionName) {
    }

    public static final class Session {
        private BlockPos cornerA;
        private BlockPos cornerB;
        private PendingCuboid pending;

        public BlockPos cornerA() {
            return cornerA;
        }

        public BlockPos cornerB() {
            return cornerB;
        }

        public PendingCuboid pendingCuboid() {
            return pending;
        }
    }
}
