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

import static org.pine.UiUtils.broadcastMessage;
import static org.pine.UiUtils.sendMessageToAllPlayersInChat;
import static org.pine.managers.PlatformManager.platformRemoveXBlock;
import static org.pine.managers.PlatformManager.platformToPattern;
import static org.pine.managers.PlayerManager.teleportAllPlayersToPlatform;
import static org.pine.managers.PlayerManager.teleportPlayerToLobby;

public class GameManager {

    private static final int SPEED_MULTIPLIER = 4;
    private static final long STARTING_TIMER_TICKS = Tick.tick().fromDuration(Duration.ofSeconds(10));
    private static final long SECONDS_5_TICKS = Tick.tick().fromDuration(Duration.ofSeconds(5));

    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);
    private static World world;

    private final Blockparty blockparty;
    private final LevelManager levelManager;

    private int speedLevel = 1;
    private Round currentRound;
    private List<Player> roundEliminations = new ArrayList<>();
    private GameState currentState = GameState.IDLE;
    private BukkitTask currentGameTask;

    public GameManager(Blockparty blockparty, LevelManager levelManager) {
        this.blockparty = blockparty;
        this.levelManager = levelManager;
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

        if (currentGameTask != null) {
            currentGameTask.cancel();
            currentGameTask = null;
        }

        currentState = GameState.IDLE;
    }

    public void playerEliminated(Player player) {
        player.sendMessage("You lose!");
        teleportPlayerToLobby(player);
        logger.info("Player {} has been eliminated", player.getName());
        sendMessageToAllPlayersInChat(player.getName() + "has been eliminated");
        currentRound.getParticipants().remove(player);
        roundEliminations.add(player);
    }

    private void scheduleNextStateAfterDelay(long delayTicks) {
        currentGameTask = Bukkit.getScheduler().runTaskLater(blockparty, this::processGameLoop, delayTicks);

    }

    private void processGameLoop() {
        sendMessageToAllPlayersInChat(currentState.name());
        switch (currentState) {
            case STARTING_FIRST_ROUND -> processGameStateStartingFirstRound();
            case STARTING -> processGameStateStarting();
            case ROUND_PREPARATION -> processGameStateRoundPreparation();
            case BLOCK_REMOVAL -> processGameStateBlockRemoval();
            case ROUND_EVALUATION -> processGameStateRoundEvaluation();
            case GAME_OVER -> stopGame();
            default -> throw new BlockpartyException("Unsupported game state: " + currentState);
        }
    }

    private void processGameStateStartingFirstRound() {
        platformToPattern(levelManager.getStartingLevel().getPattern());
        teleportAllPlayersToPlatform();

        currentRound = new Round(levelManager.getStartingLevel(), world.getPlayers());
        currentState = GameState.ROUND_PREPARATION;

        logger.info("Starting initial startup timer for {} seconds", STARTING_TIMER_TICKS / 20);
        scheduleNextStateAfterDelay(STARTING_TIMER_TICKS);
    }

    private void processGameStateStarting() {
        platformToPattern(currentRound.getLevel().getPattern());
        currentState = GameState.ROUND_PREPARATION;

        scheduleNextStateAfterDelay(20);
    }

    private void processGameStateRoundPreparation() {
        broadcastMessage("The block is: " + currentRound.getxBlock().toString());
        currentState = GameState.BLOCK_REMOVAL;
        scheduleNextStateAfterDelay(calculateTimer());
    }

    private void processGameStateBlockRemoval() {
        platformRemoveXBlock(currentRound.getxBlock());
        currentState = GameState.ROUND_EVALUATION;
        scheduleNextStateAfterDelay(SECONDS_5_TICKS);
    }

    private void processGameStateRoundEvaluation() {
        List<Player> roundParticipants = currentRound.getParticipants();
        roundParticipants.removeAll(roundEliminations);
        if (roundParticipants.isEmpty()) {
            broadcastMessage("Game over - tie");
            currentState = GameState.GAME_OVER;
//        } else if (roundParticipants.size() == 1) {
//            broadcastMessage("Winner is: " + roundParticipants.getFirst().getName());
//            currentState = GameState.GAME_OVER; // todo this doesnt allow for single player ATM
        } else {
            speedLevel += 1;
            currentRound = new Round(levelManager.getRandomLevel(), roundParticipants);
            sendMessageToAllPlayersInChat("next level, new speed level: " + speedLevel);
            sendMessageToAllPlayersInChat("participants left: " + currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", ")));
            currentState = GameState.STARTING;
        }

        scheduleNextStateAfterDelay(0);
    }

    private long calculateTimer() {
        return STARTING_TIMER_TICKS - (long) speedLevel * SPEED_MULTIPLIER;
    }
}
