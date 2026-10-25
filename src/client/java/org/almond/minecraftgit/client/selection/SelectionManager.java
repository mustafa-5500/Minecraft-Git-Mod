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
 * Per-player wand session state: the two pending corners, the last completed
 * cuboid, and the active selection name. The durable selection list lives in
 * {@link SelectionService}; the remembered active name is stored in local repo
 * metadata so it survives a restart.
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

    public Cuboid takePending(Player player) {
        Session session = session(player);
        Cuboid pending = session.pending;
        session.pending = null;
        return pending;
    }

    public void setPending(Player player, Cuboid cuboid) {
        session(player).pending = cuboid;
    }

    public Cuboid peekPending(Player player) {
        return session(player).pending;
    }

    public void clear(Player player) {
        sessions.remove(player.getUUID());
    }

    public static final class Session {
        private BlockPos cornerA;
        private BlockPos cornerB;
        private Cuboid pending;

        public BlockPos cornerA() {
            return cornerA;
        }

        public BlockPos cornerB() {
            return cornerB;
        }

        public Cuboid pendingCuboid() {
            return pending;
        }
    }
}
