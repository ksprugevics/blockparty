package org.pine.blockparty.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.pine.blockparty.Blockparty;
import org.pine.blockparty.exceptions.BlockpartyException;
import org.pine.blockparty.model.Difficulty;
import org.pine.blockparty.model.GameState;
import org.pine.blockparty.model.Round;
import org.pine.blockparty.model.XBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.pine.blockparty.managers.PlatformManager.platformRemoveXBlock;
import static org.pine.blockparty.managers.PlatformManager.platformToPattern;
import static org.pine.blockparty.managers.PlayerManager.*;
import static org.pine.blockparty.managers.UiManager.*;

public class GameManager {

    private static final long STARTING_TIMER_TICKS = 140L;
    private static final long SHOW_XBLOCK_AFTER_TICKS = 70L;
    private static final long SECONDS_3_TICKS = 60L;

    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);
    private static World world;

    private final Blockparty blockparty;
    private final LevelManager levelManager;
    private final UiManager uiManager;

    private GameState currentState = GameState.IDLE;
    private BukkitTask currentGameTask;
    private Round currentRound;
    private boolean singlePlayerMode = false;

    public GameManager(Blockparty blockparty, LevelManager levelManager, UiManager uiManager) {
        this.blockparty = blockparty;
        this.levelManager = levelManager;
        this.uiManager = uiManager;
        world = Bukkit.getWorld("world");

        if (world == null) {
            throw new BlockpartyException("Can't start game, world reference is null");
        }

        platformToPattern(levelManager.getStartingLevel().getPattern());
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void startGame() {
        if (currentState != GameState.IDLE) {
            return;
        }

        currentRound = null;
        singlePlayerMode = world.getPlayers().size() == 1;
        removeAllItems();

        currentState = GameState.STARTING_FIRST_ROUND;
        processGameLoop();
    }

    public void stopGame() {
        if (currentState == GameState.IDLE) {
            return;
        }

        teleportAllPlayersToLobby();
        platformToPattern(levelManager.getStartingLevel().getPattern());
        removeAllItems();
        uiManager.updateBossBar(Component.text("§5§lBlockparty"));
        SoundManager.stopMusic();

        if (currentGameTask != null) {
            currentGameTask.cancel();
            currentGameTask = null;
        }
        currentState = GameState.IDLE;
    }

    public void playerLeft(Player player) {
        if (currentRound != null && currentRound.getParticipants().contains(player)) {
            playerEliminated(player);
        }
    }

    public void playerEliminated(Player player) {
        world.strikeLightningEffect(player.getLocation());

        teleportPlayerToLobby(player);
        currentRound.getEliminations().add(player);

        player.sendMessage("You lose!");
        logger.info("Player {} has been eliminated", player.getName());
        broadcastInChat(player.getName() + " have been eliminated");
        uiManager.updateScoreboardRoundParticipants(currentRound.getParticipants().size() - currentRound.getEliminations().size());
        removeAllItems();
    }

    private void scheduleNextStateAfterDelay(long delayTicks) {
        logger.info("Running delay for {} ticks, {} seconds", delayTicks, delayTicks / 20);

        currentGameTask = Bukkit.getScheduler().runTaskLater(blockparty, this::processGameLoop, delayTicks);
    }

    private void processGameLoop() {
        logger.info("Processing game state: {}", currentState);

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
        teleportAllPlayersToStartingPlatform();

        currentRound = new Round(levelManager.getStartingLevel(), Difficulty.LVL_1, world.getPlayers());
        logger.info("Starting game with players: {}", currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", ")));
        uiManager.updateScoreboard(world.getPlayers().size(), currentRound.getDifficulty().getLevel(), currentRound.getDifficulty().getTimeInSeconds(), 1, 1);
        uiManager.updateBossBar(Component.text("Preparing").color(XBlock.WHITE.getDisplayText().color()));
        startSplash();
        SoundManager.playMusic();


        currentState = GameState.SHOW_XBLOCK;
        scheduleNextStateAfterDelay(STARTING_TIMER_TICKS);
    }

    private void processGameStateChangePlatform() {
        platformToPattern(currentRound.getLevel().getPattern());
        logger.info("Changing level to: {}", currentRound.getLevel().getName());
        uiManager.updateBossBar(Component.text("Preparing").color(XBlock.WHITE.getDisplayText().color()));
        removeAllItems();

        currentState = GameState.SHOW_XBLOCK;
        scheduleNextStateAfterDelay(SHOW_XBLOCK_AFTER_TICKS);
    }

    private void processGameStateShowXBlock() {
        colorCountdown(blockparty, currentRound.getxBlock().getDisplayText(), (int) currentRound.getDifficulty().getTimeInTicks() / 10 - 1);
        uiManager.updateBossBar(currentRound.getxBlock().getDisplayText());
        giveColorItemInHotbar(currentRound.getxBlock().getMaterial());

        currentState = GameState.XBLOCK_REMOVAL;
        scheduleNextStateAfterDelay(currentRound.getDifficulty().getTimeInTicks());
    }

    private void processGameStateXBlockRemoval() {
        broadcastActionBar(Component.text("§c§lX Stop X"));
        platformRemoveXBlock(currentRound.getxBlock().getMaterial());

        currentState = GameState.ROUND_EVALUATION;
        scheduleNextStateAfterDelay(SECONDS_3_TICKS);
    }

    private void processGameStateRoundEvaluation() {
        final List<Player> roundEliminations = currentRound.getEliminations();
        List<Player> roundParticipants = currentRound.getParticipants();

        roundParticipants.removeAll(roundEliminations);
        if (roundParticipants.isEmpty()) {
            currentState = GameState.WIN_CONDITION_TIE;
        } else if (Difficulty.getNextDifficulty(currentRound.getDifficulty()) == Difficulty.BLANK) {
            currentState = GameState.WIN_CONDITION_ROUND_END;
        } else if (!singlePlayerMode && roundParticipants.size() == 1) {
            currentState = GameState.WIN_CONDITION_WINNER;
        } else {
            currentState = GameState.UPDATE_DIFFICULTY;
        }

        scheduleNextStateAfterDelay(0L);
    }

    private void processGameStateWinConditionTie() {
        if (singlePlayerMode) {
            broadcastTitle(Component.text("§c§lYou lose!"), Component.empty());
        } else {
            broadcastTitle(Component.text("§c§lTie - you all lost!"), Component.empty());
        }
        platformToPattern(levelManager.getStartingLevel().getPattern());
        teleportPlayersToPlatform(currentRound.getEliminations());

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(0L);
    }

    private void processGameStateWinConditionWinner() {
        broadcastTitle(Component.text("§b§l" + currentRound.getParticipants().getFirst().getName() + " won!"), Component.empty());
        platformToPattern(levelManager.getStartingLevel().getPattern());
        launchFireworkShow();

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(240L);
    }

    private void processGameStateWinConditionRoundEnd() {
        if (singlePlayerMode) {
            broadcastTitle(Component.text("§a§lYou won!"), Component.empty());
        } else {
            broadcastTitle(Component.text("§b§l" +
                    currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", "))),
                    Component.text("§e§lare the winners!"));
        }
        platformToPattern(levelManager.getStartingLevel().getPattern());
        launchFireworkShow();

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(240L);
    }

    private void processGameStateUpdateDifficulty() {
        final List<Player> roundParticipants = currentRound.getParticipants();
        Difficulty nextDifficulty = Difficulty.getNextDifficulty(currentRound.getDifficulty());
        currentRound = new Round(levelManager.getRandomLevel(), nextDifficulty, roundParticipants);

        logger.info("Increasing speed level to: {}", nextDifficulty.getTimeInSeconds());
        logger.info("Participants left: {}", roundParticipants.stream().map(Player::getName).collect(Collectors.joining(", ")));

        uiManager.updateScoreboardRoundInfo(roundParticipants.size(), nextDifficulty.getLevel(), nextDifficulty.getTimeInSeconds());

        currentState = GameState.CHANGE_PLATFORM;
        scheduleNextStateAfterDelay(0L);
    }

    private void launchFireworkShow() {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 15) {
                    cancel();
                    return;
                }

                UiManager.launchRandomFirework();
                count++;
            }
        }.runTaskTimer(blockparty, 0L, 25L);
    }
}
