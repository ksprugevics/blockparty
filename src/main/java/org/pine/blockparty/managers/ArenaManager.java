package org.pine.blockparty.managers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.pine.blockparty.exceptions.ArenaLoadException;
import org.pine.blockparty.model.Arena;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class ArenaManager {

    private static final Logger logger = LoggerFactory.getLogger(ArenaManager.class);
    private static final Random random = new Random();

    private final String arenaFilePath;
    private final List<Arena> arenaList = new ArrayList<>();

    public ArenaManager(String arenaFilePath) {
        this.arenaFilePath = arenaFilePath;
        loadEnabledArenas();
    }

    public List<Arena> getArenaList() {
        return arenaList;
    }

    public Arena getArenaByName(String name) {
        return arenaList.stream().filter(arena -> Objects.equals(arena.name(), name)).findFirst().orElse(null);
    }

    public String getArenaInfo() {
        return "Total enabled arena count = " + arenaList.size() + ", arena = " +
                arenaList.stream().map(Arena::name).collect(Collectors.joining(", "));
    }

    public Arena getRandomArena() {
        return arenaList.get(random.nextInt(1, arenaList.size()));
    }

    public Arena getStartingArena() {
        return arenaList.getFirst();
    }

    private void loadEnabledArenas() {
        final List<Arena> allArenas = loadArenasFromConfiguredFile();
        final List<Arena> enabledArenas = allArenas.stream().filter(Arena::enabled).toList();
        arenaList.addAll(enabledArenas);
        logger.info("Successfully loaded arenas from file: {}", getArenaInfo());
    }

    private List<Arena> loadArenasFromConfiguredFile() {
        final Gson gson = new Gson();

        try (FileReader reader = new FileReader(this.arenaFilePath)) {
            final Type type = new TypeToken<List<Arena>>() {}.getType();
            final List<Arena> loadedData = gson.fromJson(reader, type);

            if (loadedData == null || loadedData.isEmpty()) {
                throw new ArenaLoadException("No arenas found. File is empty?");
            }

            return loadedData;
        } catch (IOException e) {
            throw new ArenaLoadException(e.getMessage());
        }
    }
}
