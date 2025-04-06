package org.pine.blockparty.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.pine.blockparty.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LobbyManager {

    private static final int SECOND_30_INTERVAL = 30;
    private static final int SECOND_10_INTERVAL = 10;

    private static final long SECOND_INTERVAL_TICKS = 20L;
    private static final long SECOND_30_INTERVAL_TICKS = SECOND_INTERVAL_TICKS * SECOND_30_INTERVAL;
    private static final long SECOND_10_INTERVAL_TICKS = SECOND_INTERVAL_TICKS * SECOND_10_INTERVAL;

    private static final Logger logger = LoggerFactory.getLogger(LobbyManager.class);

    private final GameManager gameManager;
    private final UiManager uiManager;
    private final World gameWorld;
    private final Plugin plugin;
    private final long initialTimerDelayTicks;
    private final long gamesStatusCheckIntervalTicks;
    private final long gameStartDelayTicks;

    private List<Player> playerList = new ArrayList<>();
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

    public void startGame() {
        gameManager.startGame(playerList);
    }

    public void addPlayerToParticipants(Player player) {
        if (!playerList.contains(player)) {
            playerList.add(player);
            player.removePotionEffect(PotionEffectType.ABSORPTION);
        }
    }

    public void removePlayerFromParticipants(Player player) {
        playerList.remove(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200000, 0));
    }

    public void togglePlayerSpectate(Player player) {
        if (playerList.contains(player)) {
            playerList.remove(player);
            uiManager.broadcastInChat(Component.text("%s is now spectating".formatted(player.getName())).color(TextColor.color(0xC7C9C8)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200000, 0));
        } else {
            playerList.add(player);
            uiManager.broadcastInChat(Component.text("%s will play the next game".formatted(player.getName())).color(TextColor.color(0xC7C9C8)));
            player.removePotionEffect(PotionEffectType.ABSORPTION);
        }
    }

    private void startLobbyScheduledTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!gameWorld.getPlayers().isEmpty() && !gameManager.isGameOngoing() && !playerList.isEmpty() && !timerStarted) {
                logger.info("Game start conditions met - starting game start timer");
                initiateStartTimer();
            }
        }, initialTimerDelayTicks, gamesStatusCheckIntervalTicks);
    }

    private void initiateStartTimer() {
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
            startGame();
            timerStarted = false;
            timerTask.cancel();
            uiManager.broadcastPlayerLevel(0);
        } else if (ticksTilGameStart == SECOND_30_INTERVAL_TICKS) {
            notifyPlayersOfTimeRemaining(SECOND_30_INTERVAL);
        } else if (ticksTilGameStart == SECOND_10_INTERVAL_TICKS) {
            notifyPlayersOfTimeRemaining(SECOND_10_INTERVAL);
        } else if (playerList.isEmpty()) {
            logger.info("No participants - cancelling game");
            timerStarted = false;
            timerTask.cancel();
            uiManager.broadcastPlayerLevel(0);
        }
        uiManager.broadcastPlayerLevel((int) ticksTilGameStart / 20);
    }

    private void notifyPlayersOfTimeRemaining(int secondsLeft) {
        uiManager.broadcastInChat(Component.text("Game starting in %d seconds".formatted(secondsLeft)).color(TextColor.color(0xCAE340)));
    }
}
