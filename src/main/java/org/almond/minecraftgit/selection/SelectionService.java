package org.almond.minecraftgit.selection;

import org.almond.minecraftgit.config.ModConfig;
import org.almond.minecraftgit.model.BlockPos;
import org.almond.minecraftgit.model.Cuboid;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.util.Names;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads, mutates, and persists named selections per world id + player. All
 * mutation methods write through immediately so a crash or disconnect cannot
 * lose a selection definition.
 */
public final class SelectionService {

    private final ModConfig config;
    private final SelectionMetadataStore store;
    private final Map<String, List<Selection>> cache = new ConcurrentHashMap<>();

    /**
     * Constructor for the {@link SelectionService} 
     * @param config
     */
    public SelectionService(ModConfig config) {
        this.config = config;
        this.store = new SelectionMetadataStore(config.selectionsRoot());
    }

    public Path root() {
        return config.selectionsRoot();
    }

    public List<Selection> list(String worldId, UUID owner) {
        return List.copyOf(load(worldId, owner));
    }

    public List<Selection> listAll(String worldId) {
        return store.loadAll(worldId);
    }

    public Optional<Selection> find(String worldId, UUID owner, String name) {
        if (name == null) {
            return Optional.empty();
        }
        return load(worldId, owner).stream()
                .filter(selection -> selection.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public Selection get(String worldId, UUID owner, String name) {
        return find(worldId, owner, name)
                .orElseThrow(() -> new SelectionException("No selection named '" + name + "'."));
    }

    public Selection create(String worldId, UUID owner, String ownerName, String name) {
        return createAt(worldId, owner, ownerName, name, null);
    }

    public Selection createAt(String worldId, UUID owner, String ownerName, String name, BlockPos origin) {
        requireName(name);
        List<Selection> selections = load(worldId, owner);
        if (selections.stream().anyMatch(selection -> selection.name().equalsIgnoreCase(name))) {
            throw new SelectionException("A selection named '" + name + "' already exists.");
        }
        Selection selection = new Selection(name, owner, ownerName, worldId, origin);
        selections.add(selection);
        selections.sort(Selection.byName());
        persist(worldId, owner, ownerName, selections);
        return selection;
    }

    public Selection addCuboid(String worldId, UUID owner, String name, Cuboid cuboid) {
        Selection selection = get(worldId, owner, name);
        if (selection.cuboidCount() >= config.maxCuboidsPerSelection) {
            throw new SelectionException("This selection already has the maximum of "
                    + config.maxCuboidsPerSelection + " cuboids.");
        }
        long newVolume = saturatedAdd(selection.volume(), cuboid.volume());
        if (newVolume > config.maxVolumePerSelection) {
            throw new SelectionException("This selection would exceed the maximum volume of "
                    + config.maxVolumePerSelection + " blocks.");
        }
        selection.addCuboid(cuboid);
        persist(worldId, owner, selection.ownerName(), load(worldId, owner));
        return selection;
    }

    public Cuboid removeCuboid(String worldId, UUID owner, String name, int oneBasedIndex) {
        Selection selection = get(worldId, owner, name);
        if (oneBasedIndex < 1 || oneBasedIndex > selection.cuboidCount()) {
            throw new SelectionException("Cuboid index must be between 1 and " + selection.cuboidCount() + ".");
        }
        Cuboid removed = selection.removeCuboidAt(oneBasedIndex - 1);
        persist(worldId, owner, selection.ownerName(), load(worldId, owner));
        return removed;
    }

    /** @return the previous origin */
    public BlockPos reorigin(String worldId, UUID owner, String name) {
        Selection selection = get(worldId, owner, name);
        if (selection.cuboidCount() == 0) {
            throw new SelectionException("Selection '" + name + "' has no cuboids to re-origin from.");
        }
        BlockPos previous = selection.reorigin();
        persist(worldId, owner, selection.ownerName(), load(worldId, owner));
        return previous;
    }

    public void delete(String worldId, UUID owner, String name) {
        List<Selection> selections = load(worldId, owner);
        Selection selection = selections.stream()
                .filter(candidate -> candidate.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new SelectionException("No selection named '" + name + "'."));
        selections.remove(selection);
        persist(worldId, owner, selection.ownerName(), selections);
    }

    public void setTracked(String worldId, UUID owner, String name, boolean tracked) {
        Selection selection = get(worldId, owner, name);
        selection.setTracked(tracked);
        persist(worldId, owner, selection.ownerName(), load(worldId, owner));
    }

    public void save(String worldId, UUID owner) {
        List<Selection> selections = load(worldId, owner);
        String ownerName = selections.isEmpty() ? "" : selections.get(0).ownerName();
        persist(worldId, owner, ownerName, selections);
    }

    public SelectionMetadataStore store() {
        return store;
    }

    /**
     * Load selection of the Repository into cache per-world
     * @param worldId
     * @param owner
     * @return
     */
    private List<Selection> load(String worldId, UUID owner) {
        return cache.computeIfAbsent(key(worldId, owner), ignored -> store.load(worldId, owner));
    }

    private void persist(String worldId, UUID owner, String ownerName, List<Selection> selections) {
        selections.sort(Comparator.comparing(selection -> selection.name().toLowerCase(Locale.ROOT)));
        store.save(worldId, owner, ownerName, selections);
        cache.put(key(worldId, owner), selections);
    }

    private static String key(String worldId, UUID owner) {
        return worldId + "|" + owner;
    }

    private static void requireName(String name) {
        try {
            Names.requireSelectionName(name);
        } catch (IllegalArgumentException e) {
            throw new SelectionException(e.getMessage());
        }
    }

    private static long saturatedAdd(long a, long b) {
        if (a > Long.MAX_VALUE - b) {
            return Long.MAX_VALUE;
        }
        return a + b;
    }

    public void invalidate(String worldId, UUID owner) {
        cache.remove(key(worldId, owner));
    }

    public List<Selection> tracked(String worldId, UUID owner) {
        List<Selection> result = new ArrayList<>();
        for (Selection selection : load(worldId, owner)) {
            if (selection.isTracked()) {
                result.add(selection);
            }
        }
        return result;
    }
}
