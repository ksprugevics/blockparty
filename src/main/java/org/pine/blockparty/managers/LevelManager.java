package org.pine.blockparty.managers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.pine.blockparty.exceptions.LevelLoadException;
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

public class LevelManager {

    private static final String LEVEL_FILE_PATH = "plugins/blockparty/levels.json";

    private static final Logger logger = LoggerFactory.getLogger(LevelManager.class);
    private static final Random random = new Random();
    private final List<Arena> arenaList = new ArrayList<>();

    public LevelManager() {
        loadLevels();
    }

    public List<Arena> getLevelList() {
        return arenaList;
    }

    public Arena getLevelByName(String name) {
        return arenaList.stream().filter(lvl -> Objects.equals(lvl.name(), name)).findFirst().orElse(null);
    }

    public String getLevelInfo() {
        return "Total enabled level count = " + arenaList.size() + ", levels = " +
                arenaList.stream().map(Arena::name).collect(Collectors.joining(", "));
    }

    public Arena getRandomLevel() {
        return arenaList.get(random.nextInt(1, arenaList.size()));
    }

    public Arena getStartingLevel() {
        return arenaList.getFirst();
    }

    private void loadLevels() {
        final List<Arena> allArenas = loadLevelsFromFile();
        final List<Arena> enabledArenas = allArenas.stream().filter(Arena::enabled).toList();
        arenaList.addAll(enabledArenas);
        logger.info("Successfully loaded levels from file: {}", getLevelInfo());
    }

    private List<Arena> loadLevelsFromFile() {
        final Gson gson = new Gson();

        try (FileReader reader = new FileReader(LEVEL_FILE_PATH)) {
            final Type type = new TypeToken<List<Arena>>() {}.getType();
            final List<Arena> loadedData = gson.fromJson(reader, type);

            if (loadedData == null || loadedData.isEmpty()) {
                throw new LevelLoadException("No levels found. File is empty?");
            }

            return loadedData;
        } catch (IOException e) {
            throw new LevelLoadException(e.getMessage());
        }
    }
}
