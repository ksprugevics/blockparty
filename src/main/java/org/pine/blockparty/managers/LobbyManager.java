package org.pine.blockparty.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.pine.blockparty.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobbyManager {

    private static final int SECOND_30_INTERVAL = 30;
    private static final int SECOND_15_INTERVAL = 15;
    private static final int SECOND_10_INTERVAL = 10;
    private static final int SECOND_5_INTERVAL = 5;
    private static final int SECOND_4_INTERVAL = 4;
    private static final int SECOND_3_INTERVAL = 3;
    private static final int SECOND_2_INTERVAL = 2;
    private static final int SECOND_1_INTERVAL = 1;

    private static final long SECOND_INTERVAL_TICKS = 20L;
    private static final long SECOND_30_INTERVAL_TICKS = SECOND_INTERVAL_TICKS * SECOND_30_INTERVAL;
    private static final long SECOND_15_INTERVAL_TICKS = SECOND_INTERVAL_TICKS * SECOND_15_INTERVAL;
    private static final long SECOND_10_INTERVAL_TICKS = SECOND_INTERVAL_TICKS * SECOND_10_INTERVAL;
    private static final long SECOND_5_INTERVAL_TICKS = SECOND_INTERVAL_TICKS * SECOND_5_INTERVAL;
    private static final long SECOND_4_INTERVAL_TICKS = SECOND_INTERVAL_TICKS * SECOND_4_INTERVAL;
    private static final long SECOND_3_INTERVAL_TICKS = SECOND_INTERVAL_TICKS * SECOND_3_INTERVAL;
    private static final long SECOND_2_INTERVAL_TICKS = SECOND_INTERVAL_TICKS * SECOND_2_INTERVAL;

    private static final Logger logger = LoggerFactory.getLogger(LobbyManager.class);

    private final GameManager gameManager;
    private final UiManager uiManager;
    private final World gameWorld;
    private final Plugin plugin;
    private final long initialTimerDelayTicks;
    private final long gamesStatusCheckIntervalTicks;
    private final long gameStartDelayTicks;

    private boolean timerStarted = false;
    private long ticksTilGameStart = 0L;
    private BukkitTask timerTask;

    public LobbyManager(World gameWorld, GameManager gameManager, UiManager uiManager, ConfigurationManager configurationManager, Plugin plugin) {
        this.gameWorld = gameWorld;
        this.gameManager = gameManager;
        this.uiManager = uiManager;
        this.plugin = plugin;

        this.initialTimerDelayTicks = Integer.parseInt(configurationManager.getConfigurationValue(Configuration.LOBBY_CHECK_INITIAL_DELAY_SECONDS)) * SECOND_INTERVAL_TICKS;
        this.gamesStatusCheckIntervalTicks = Integer.parseInt(configurationManager.getConfigurationValue(Configuration.LOBBY_CHECK_INTERVAL_SECONDS)) * SECOND_INTERVAL_TICKS;
        this.gameStartDelayTicks = Integer.parseInt(configurationManager.getConfigurationValue(Configuration.LOBBY_GAME_START_SECONDS)) * SECOND_INTERVAL_TICKS;

        startLobbyScheduledTask();
    }

    public void startLobbyScheduledTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            logger.info("Checking for game start conditions");
            if (!gameWorld.getPlayers().isEmpty() && !gameManager.isGameOngoing() && !timerStarted) {
                logger.info("Game start conditions met - starting game start timer");
                initiateStartTimer();
            }
        }, initialTimerDelayTicks, gamesStatusCheckIntervalTicks);
    }

    public void initiateStartTimer() {
        timerStarted = true;
        ticksTilGameStart = gameStartDelayTicks;
        timerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            ticksTilGameStart -= SECOND_INTERVAL_TICKS;
            checkTimerConditions();
        }, 0L, SECOND_INTERVAL_TICKS);
    }

    private void checkTimerConditions() {
        if (ticksTilGameStart <= 0) {
            logger.info("Starting the game");
            gameManager.startGame();
            timerStarted = false;
            timerTask.cancel();
        } else if (ticksTilGameStart == SECOND_30_INTERVAL_TICKS) {
            notifyPlayersOfTimeRemaining(SECOND_30_INTERVAL);
        }else if (ticksTilGameStart == SECOND_15_INTERVAL_TICKS) {
            notifyPlayersOfTimeRemaining(SECOND_15_INTERVAL);
        } else if (ticksTilGameStart == SECOND_10_INTERVAL_TICKS) {
            notifyPlayersOfTimeRemaining(SECOND_10_INTERVAL);
        } else if (ticksTilGameStart == SECOND_5_INTERVAL_TICKS) {
            notifyPlayersOfTimeRemaining(SECOND_5_INTERVAL);
        } else if (ticksTilGameStart == SECOND_4_INTERVAL_TICKS) {
            notifyPlayersOfTimeRemaining(SECOND_4_INTERVAL);
        } else if (ticksTilGameStart == SECOND_3_INTERVAL_TICKS) {
            notifyPlayersOfTimeRemaining(SECOND_3_INTERVAL);
        } else if (ticksTilGameStart == SECOND_2_INTERVAL_TICKS) {
            notifyPlayersOfTimeRemaining(SECOND_2_INTERVAL);
        } else if (ticksTilGameStart == SECOND_INTERVAL_TICKS) {
            notifyPlayersOfTimeRemaining(SECOND_1_INTERVAL);
        }
    }

    private void notifyPlayersOfTimeRemaining(int secondsLeft) {
        uiManager.broadcastInChat(Component.text("Game starting in %d seconds".formatted(secondsLeft)).color(TextColor.color(0xCAE340)));
    }
}
