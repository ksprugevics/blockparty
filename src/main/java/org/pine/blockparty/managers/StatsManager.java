package org.pine.blockparty.managers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.pine.blockparty.exceptions.StatsLoadException;
import org.pine.blockparty.model.PlayerStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class StatsManager {

    private static final Logger logger = LoggerFactory.getLogger(StatsManager.class);

    private final Gson gson = new Gson();
    private final String statsFilePath;
    private Map<String, PlayerStats> playerStatsMap;

    public StatsManager(String statsFilePath) {
        this.statsFilePath = statsFilePath;
        createStatsFileIfMissing();
        this.playerStatsMap = loadStatsFromConfiguredFile();
    }

    public PlayerStats getPlayerStats(Player player) {
        final String uuid = player.getUniqueId().toString();
        PlayerStats playerStats = playerStatsMap.get(uuid);

        return playerStats != null ? playerStats : addPlayerToStatsFile(uuid);
    }

    public void incrementPlayerWins(Player player) {
        PlayerStats playerStats = getPlayerStats(player);
        playerStats.incrementWins();
    }

    public void incrementPlayerLoses(Player player) {
        PlayerStats playerStats = getPlayerStats(player);
        playerStats.incrementLosses();
    }

    public void incrementPlayerTies(Player player) {
        PlayerStats playerStats = getPlayerStats(player);
        playerStats.incrementTies();
    }

    public void incrementPlayerRoundsSurvived(Player player) {
        PlayerStats playerStats = getPlayerStats(player);
        playerStats.incrementRoundsSurvived();
    }

    public void savePlayerStatsToConfiguredFile() {
        try (FileWriter writer = new FileWriter(this.statsFilePath)) {
            gson.toJson(playerStatsMap, writer);
        } catch (IOException e) {
            throw new StatsLoadException("Failed to save stats file: " + e.getMessage());
        }
    }

    private void createStatsFileIfMissing() {
        final File statsFile = new File(this.statsFilePath);

        if (!statsFile.exists() || statsFile.length() == 0) {
            createEmptyStatsFile();
        }
    }

    private void createEmptyStatsFile() {
        final File statsFile = new File(this.statsFilePath);
        logger.info("Creating an empty stats file: {}", this.statsFilePath);

        try (FileWriter writer = new FileWriter(statsFile)) {
            gson.toJson(new HashMap<String, PlayerStats>(), writer);
        } catch (IOException e) {
            throw new StatsLoadException("Failed to create empty stats file: " + e.getMessage());
        } finally {
            logger.info("Successfully created a new stats file: {}", this.statsFilePath);
        }
    }

    private Map<String, PlayerStats> loadStatsFromConfiguredFile() {
        logger.info("Loading stats file: {}", this.statsFilePath);
        try (FileReader reader = new FileReader(this.statsFilePath)) {
            final Type type = new TypeToken<Map<String, PlayerStats>>() {}.getType();
            final Map<String, PlayerStats> loadedData = gson.fromJson(reader, type);

            return loadedData != null ? loadedData : new HashMap<>();
        } catch (IOException e) {
            throw new StatsLoadException(e.getMessage());
        } finally {
            logger.info("Successfully loaded stats file");
        }
    }

    private PlayerStats addPlayerToStatsFile(String uuid) {
        logger.info("Adding new player to stats:");
        final PlayerStats playerStats = new PlayerStats(0, 0, 0, 0);
        playerStatsMap.put(uuid, playerStats);

        return playerStats;
    }
}
