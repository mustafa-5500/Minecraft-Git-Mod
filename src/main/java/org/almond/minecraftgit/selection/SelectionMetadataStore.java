package org.almond.minecraftgit.selection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.almond.minecraftgit.model.Selection;
import org.almond.minecraftgit.util.Names;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Machine-local selection state. This is intentionally stored outside the
 * repository: it records absolute world coordinates, owners, and the current
 * origin, none of which belong in a shared commit.
 */
public final class SelectionMetadataStore {

    public static final int FORMAT_VERSION = 1;

    private final Path root;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * Constructor for the per-repository meta-data Read & Write Service
     * @param root
     */
    public SelectionMetadataStore(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    public Path file(String world, UUID owner) {
        return root.resolve(Names.safeWorldName(world)).resolve(owner + ".json");
    }

    public List<Selection> load(String world, UUID owner) {
        return loadFile(file(world, owner), world, owner, "");
    }

    public List<Selection> loadFile(Path file, String world, UUID fallbackOwner, String fallbackOwnerName) {
        if (!Files.isRegularFile(file)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            JsonObject rootObject = JsonParser.parseString(json).getAsJsonObject();
            JsonArray array = rootObject.has("selections") ? rootObject.getAsJsonArray("selections") : new JsonArray();
            Selection[] parsed = gson.fromJson(array, Selection[].class);
            List<Selection> result = new ArrayList<>();
            if (parsed != null) {
                for (Selection selection : parsed) {
                    if (selection != null) {
                        result.add(selection.normalize(world, fallbackOwner, fallbackOwnerName));
                    }
                }
            }
            return result;
        } catch (JsonSyntaxException | IllegalStateException e) {
            throw new SelectionException("Selection metadata at " + file + " is corrupt. "
                    + "Delete that file to reset this player's selections in this world.");
        } catch (IOException e) {
            throw new SelectionException("Could not read selection metadata at " + file + ": " + e.getMessage());
        }
    }

    /** All players' selections in a world. Used by status/pull for console contexts. */
    public List<Selection> loadAll(String world) {
        Path worldDir = root.resolve(Names.safeWorldName(world));
        if (!Files.isDirectory(worldDir)) {
            return List.of();
        }
        List<Selection> result = new ArrayList<>();
        try (Stream<Path> files = Files.list(worldDir)) {
            files.filter(path -> path.getFileName().toString().endsWith(".json"))
                    .forEach(path -> {
                        UUID owner = ownerFromFileName(path);
                        result.addAll(loadFile(path, world, owner, ""));
                    });
        } catch (IOException e) {
            throw new SelectionException("Could not list selection metadata in " + worldDir + ": " + e.getMessage());
        }
        result.sort(Selection.byName());
        return result;
    }

    public void save(String world, UUID owner, String ownerName, List<Selection> selections) {
        Path file = file(world, owner);
        try {
            Files.createDirectories(file.getParent());
            JsonObject rootObject = new JsonObject();
            rootObject.addProperty("format", FORMAT_VERSION);
            rootObject.addProperty("world", world);
            rootObject.addProperty("owner", owner.toString());
            rootObject.addProperty("ownerName", ownerName == null ? "" : ownerName);
            rootObject.add("selections", gson.toJsonTree(selections));
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            Files.writeString(tmp, gson.toJson(rootObject), StandardCharsets.UTF_8);
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new SelectionException("Could not save selection metadata at " + file + ": " + e.getMessage());
        }
    }

    private static UUID ownerFromFileName(Path file) {
        String name = file.getFileName().toString();
        if (name.endsWith(".json")) {
            name = name.substring(0, name.length() - 5);
        }
        try {
            return UUID.fromString(name);
        } catch (IllegalArgumentException e) {
            return new UUID(0L, 0L);
        }
    }
}
