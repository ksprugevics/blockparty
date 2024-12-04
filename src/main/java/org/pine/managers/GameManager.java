package org.pine.managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.pine.Blockparty;
import org.pine.exceptions.BlockpartyException;
import org.pine.model.Difficulty;
import org.pine.model.GameState;
import org.pine.model.Round;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.pine.managers.PlatformManager.platformRemoveXBlock;
import static org.pine.managers.PlatformManager.platformToPattern;
import static org.pine.managers.PlayerManager.*;
import static org.pine.managers.UiManager.*;

public class GameManager {

    private static final long STARTING_TIMER_TICKS = 140L;
    private static final long SHOW_XBLOCK_AFTER_TICKS = 70L;
    private static final long SECONDS_3_TICKS = 60L;

    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);
    private static World world;

    private final Blockparty blockparty;
    private final LevelManager levelManager;

    private Round currentRound;
    private List<Player> roundEliminations; // todo move to Round?
    private Difficulty currentDifficulty;

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

        currentDifficulty = Difficulty.LVL_1;
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
            case WIN_CONDITION_ROUND_END -> processGameStateWinConditionRoundEnd();
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
        scheduleNextStateAfterDelay(SHOW_XBLOCK_AFTER_TICKS);
    }

    private void processGameStateShowXBlock() {
        broadcastActionBar(currentRound.getxBlock().getDisplayText());

        currentState = GameState.XBLOCK_REMOVAL;
        scheduleNextStateAfterDelay(currentDifficulty.getTimeInTicks());
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
        } else if (Difficulty.getNextDifficulty(currentDifficulty) == Difficulty.BLANK) {
            currentState = GameState.WIN_CONDITION_ROUND_END;
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

    private void processGameStateWinConditionRoundEnd() {
        broadcastTitle("Tie: " + currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", ")) + " won!");
        platformToPattern(levelManager.getStartingLevel().getPattern());

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(0L);
    }

    private void processGameStateUpdateDifficulty() {
        List<Player> roundParticipants = currentRound.getParticipants();
        currentDifficulty = Difficulty.getNextDifficulty(currentDifficulty);
        currentRound = new Round(levelManager.getRandomLevel(), roundParticipants);

        broadcastInChat("Increasing speed level to: " + currentDifficulty.getTimeInSeconds());
        broadcastInChat("Participants left: " + roundParticipants.stream().map(Player::getName).collect(Collectors.joining(", ")));

        currentState = GameState.CHANGE_PLATFORM;
        scheduleNextStateAfterDelay(0L);
    }
}
