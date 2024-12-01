package org.pine.managers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.pine.exceptions.LevelLoadException;
import org.pine.model.Level;
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
    private final List<Level> levelList = new ArrayList<>();

    public LevelManager() {
        loadLevels();
    }

    public List<Level> getLevelList() {
        return levelList;
    }

    public Level getLevelByName(String name) {
        return levelList.stream().filter(lvl -> Objects.equals(lvl.getName(), name)).findFirst().orElse(null);
    }

    public String getLevelInfo() {
        return "Total enabled level count = " + levelList.size() + ", levels = " +
                levelList.stream().map(Level::getName).collect(Collectors.joining(", "));
    }

    public Level getRandomLevel() {
        return levelList.get(random.nextInt(1, levelList.size()));
    }

    public Level getStartingLevel() {
        return levelList.getFirst();
    }

    private void loadLevels() {
        final List<Level> allLevels = loadLevelsFromFile();
        final List<Level> enabledLevels = allLevels.stream().filter(Level::isEnable).toList();
        levelList.addAll(enabledLevels);
        logger.info("Successfully loaded levels from file: {}", getLevelInfo());
    }

    private List<Level> loadLevelsFromFile() {
        final Gson gson = new Gson();

        try (FileReader reader = new FileReader(LEVEL_FILE_PATH)) {
            final Type type = new TypeToken<List<org.pine.model.Level>>() {}.getType();
            final List<org.pine.model.Level> loadedData = gson.fromJson(reader, type);

            if (loadedData == null || loadedData.isEmpty()) {
                throw new LevelLoadException("No levels found. File is empty?");
            }

            return loadedData;
        } catch (IOException e) {
            throw new LevelLoadException(e.getMessage());
        }
    }
}
