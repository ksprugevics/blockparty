package org.pine.managers;

import io.papermc.paper.util.Tick;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.pine.Blockparty;
import org.pine.exceptions.BlockpartyException;
import org.pine.model.GameState;
import org.pine.model.Round;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.pine.managers.PlatformManager.platformRemoveXBlock;
import static org.pine.managers.PlatformManager.platformToPattern;
import static org.pine.managers.PlayerManager.*;
import static org.pine.managers.UiManager.*;

public class GameManager {

    private static final int SPEED_LEVEL_MULTIPLIER = 8;
    private static final int SPEED_INCREASE = 3;
    private static final long STARTING_TIMER_TICKS = Tick.tick().fromDuration(Duration.ofSeconds(10));
    private static final long ROUND_TIME_TICK = Tick.tick().fromDuration(Duration.ofSeconds(10));
    private static final long X_BLOCK_TIME_TICK = Tick.tick().fromDuration(Duration.ofSeconds(7));
    private static final long SECONDS_3_TICKS = Tick.tick().fromDuration(Duration.ofSeconds(3));

    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);
    private static World world;

    private final Blockparty blockparty;
    private final LevelManager levelManager;

    private int speedLevel = 1;
    private Round currentRound;
    private List<Player> roundEliminations;

    private GameState currentState = GameState.IDLE;
    private BukkitTask currentGameTask;

    public GameManager(Blockparty blockparty, LevelManager levelManager) {
        this.blockparty = blockparty;
        this.levelManager = levelManager;
        this.roundEliminations = new ArrayList<>();
        world = Bukkit.getWorld("world");

        if (world == null) {
            throw new BlockpartyException("Can't start game, world reference is null");
        }

        platformToPattern(levelManager.getStartingLevel().getPattern());
    }

    public void startGame() {
        if (currentState != GameState.IDLE) {
            return;
        }

        speedLevel = 1;
        roundEliminations.clear();
        currentRound = null;

        currentState = GameState.STARTING_FIRST_ROUND;
        processGameLoop();
    }

    public void stopGame() {
        if (currentState == GameState.IDLE) {
            return;
        }

        teleportAllPlayersToLobby();
        platformToPattern(levelManager.getStartingLevel().getPattern());

        if (currentGameTask != null) {
            currentGameTask.cancel();
            currentGameTask = null;
        }
        currentState = GameState.IDLE;
    }

    public void playerEliminated(Player player) {
        teleportPlayerToLobby(player);
        currentRound.getParticipants().remove(player);
        roundEliminations.add(player);

        player.sendMessage("You lose!");
        logger.info("Player {} has been eliminated", player.getName());
        broadcastInChat(player.getName() + " has been eliminated");

        // todo check if 1 or less player left
    }

    private void scheduleNextStateAfterDelay(long delayTicks) {
        logger.info("Running delay for {} ticks, {} seconds", delayTicks, delayTicks / 20);
        broadcastInChat("Running delay for " + delayTicks + " ticks, " + delayTicks / 20 + " seconds");

        currentGameTask = Bukkit.getScheduler().runTaskLater(blockparty, this::processGameLoop, delayTicks);
    }

    private void processGameLoop() {
        logger.info("Processing game state: {}", currentState);
        broadcastInChat(currentState.name());

        switch (currentState) {
            case STARTING_FIRST_ROUND -> processGameStateStartingFirstRound();
            case CHANGE_PLATFORM -> processGameStateChangePlatform();
            case SHOW_XBLOCK -> processGameStateShowXBlock();
            case XBLOCK_REMOVAL -> processGameStateXBlockRemoval();
            case ROUND_EVALUATION -> processGameStateRoundEvaluation();
            case UPDATE_DIFFICULTY -> processGameStateUpdateDifficulty();
            case WIN_CONDITION_TIE -> processGameStateWinConditionTie();
            case WIN_CONDITION_WINNER -> processGameStateWinConditionWinner();
            case GAME_OVER -> stopGame();
            default -> throw new BlockpartyException("Unsupported game state: " + currentState);
        }
    }

    private void processGameStateStartingFirstRound() {
        platformToPattern(levelManager.getStartingLevel().getPattern());
        teleportAllPlayersToPlatform();

        currentRound = new Round(levelManager.getStartingLevel(), world.getPlayers());
        broadcastInChat("Starting game with players: " + currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", ")));
        logger.info("Starting game with players: {}", currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", ")));

        currentState = GameState.SHOW_XBLOCK;
        scheduleNextStateAfterDelay(STARTING_TIMER_TICKS);
    }

    private void processGameStateChangePlatform() {
        platformToPattern(currentRound.getLevel().getPattern());
        logger.info("Changing level to: {}", currentRound.getLevel().getName());
        broadcastInChat("Changing level to: " + currentRound.getLevel().getName());

        currentState = GameState.SHOW_XBLOCK;
        scheduleNextStateAfterDelay(calculateXBlockTime());
    }

    private void processGameStateShowXBlock() {
        broadcastActionBar(currentRound.getxBlock().getDisplayText());

        currentState = GameState.XBLOCK_REMOVAL;
        scheduleNextStateAfterDelay(calculateRoundTime());
    }

    private void processGameStateXBlockRemoval() {
        platformRemoveXBlock(currentRound.getxBlock().getMaterial());

        currentState = GameState.ROUND_EVALUATION;
        scheduleNextStateAfterDelay(SECONDS_3_TICKS);
    }

    private void processGameStateRoundEvaluation() {
        List<Player> roundParticipants = currentRound.getParticipants();
        roundParticipants.removeAll(roundEliminations);
        if (roundParticipants.isEmpty()) {
            currentState = GameState.WIN_CONDITION_TIE;
//        } else if (roundParticipants.size() == 1) {
//            currentState = GameState.WIN_CONDITION_WINNER;
        } else {
            currentState = GameState.UPDATE_DIFFICULTY;
        }

        scheduleNextStateAfterDelay(0L);
    }

    private void processGameStateWinConditionTie() {
        broadcastTitle("Game over - tie");
        platformToPattern(levelManager.getStartingLevel().getPattern());
        teleportPlayersToPlatform(roundEliminations);

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(0L);
    }

    private void processGameStateWinConditionWinner() {
        broadcastTitle(currentRound.getParticipants().getFirst().getName() + " won!");
        platformToPattern(levelManager.getStartingLevel().getPattern());

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(0L);
    }

    private void processGameStateUpdateDifficulty() {
        List<Player> roundParticipants = currentRound.getParticipants();
        speedLevel += SPEED_INCREASE;
        currentRound = new Round(levelManager.getRandomLevel(), roundParticipants);

        broadcastInChat("Increasing speed level to: " + speedLevel);
        broadcastInChat("Participants left: " + roundParticipants.stream().map(Player::getName).collect(Collectors.joining(", ")));

        currentState = GameState.CHANGE_PLATFORM;
        scheduleNextStateAfterDelay(0L);
    }

    private long calculateRoundTime() {
        return ROUND_TIME_TICK - (long) speedLevel * SPEED_LEVEL_MULTIPLIER;
    }

    private long calculateXBlockTime() {
        return X_BLOCK_TIME_TICK - (long) speedLevel * SPEED_LEVEL_MULTIPLIER;
    }
}
