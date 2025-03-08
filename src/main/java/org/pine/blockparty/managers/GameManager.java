package org.pine.blockparty.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.pine.blockparty.Blockparty;
import org.pine.blockparty.exceptions.BlockpartyException;
import org.pine.blockparty.exceptions.WorldNullException;
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
import static org.pine.blockparty.model.Difficulty.*;

public class GameManager {

    // todo if "Difficulty" enum was something more like "Duration", this might be moved there. consider
    private static final long STARTING_TIMER_TICKS = 140L;
    private static final long SHOW_XBLOCK_AFTER_TICKS = 70L;
    private static final long SECONDS_3_TICKS = 60L;

    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);

    private final World world;
    private final Blockparty plugin;
    private final ArenaManager arenaManager;
    private final UiManager uiManager;

    private GameState currentState = GameState.IDLE;
    private BukkitTask currentGameTask;
    private Round currentRound;
    private boolean isSinglePlayerMode = false;

    public GameManager(Blockparty plugin, ArenaManager arenaManager, UiManager uiManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.uiManager = uiManager;
        this.world = Bukkit.getWorld("world"); // todo probably should be an enum in configuration

        if (world == null) {
            throw new WorldNullException();
        }

        platformToPattern(arenaManager.getStartingArena().pattern());
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void startGame() {
        if (currentState != GameState.IDLE) {
            return;
        }

        currentRound = null;
        isSinglePlayerMode = world.getPlayers().size() == 1;
        removeAllItems();

        currentState = GameState.FIRST_ROUND_START;
        processGameLoop();
    }

    public void stopGame() {
        if (currentState == GameState.IDLE) {
            return;
        }

        teleportAllPlayersToLobby();
        platformToPattern(arenaManager.getStartingArena().pattern());
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

        currentGameTask = Bukkit.getScheduler().runTaskLater(plugin, this::processGameLoop, delayTicks);
    }

    private void processGameLoop() {
        logger.info("Processing game state: {}", currentState);

        switch (currentState) {
            case FIRST_ROUND_START -> processGameStateStartingFirstRound();
            case PLATFORM_CHANGE -> processGameStateChangePlatform();
            case XBLOCK_DISPLAY -> processGameStateShowXBlock();
            case XBLOCK_REMOVAL -> processGameStateXBlockRemoval();
            case ROUND_EVALUATION -> processGameStateRoundEvaluation();
            case DIFFICULTY_UPDATE -> processGameStateUpdateDifficulty();
            case WIN_CONDITION_TIE -> processGameStateWinConditionTie();
            case WIN_CONDITION_SINGLE_WINNER -> processGameStateWinConditionWinner();
            case WIN_CONDITION_MAX_ROUNDS_EXCEEDED -> processGameStateWinConditionRoundEnd();
            case GAME_OVER -> stopGame();
            default -> throw new BlockpartyException("Unsupported game state: " + currentState);
        }
    }

    private void processGameStateStartingFirstRound() {
        platformToPattern(arenaManager.getStartingArena().pattern());
        teleportAllPlayersToStartingPlatform();

        currentRound = new Round(arenaManager.getStartingArena(), DIFFICULTY_1, world.getPlayers());
        logger.info("Starting game with players: {}", currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", ")));
        uiManager.updateScoreboard(world.getPlayers().size(), currentRound.getDifficulty().getCounter(), currentRound.getDifficulty().getDurationInSecondsLabel(), 1, 1);
        uiManager.updateBossBar(Component.text("Preparing").color(XBlock.WHITE.getDisplayText().color()));
        startSplash();
        SoundManager.playMusic();

        currentState = GameState.XBLOCK_DISPLAY;
        scheduleNextStateAfterDelay(STARTING_TIMER_TICKS);
    }

    private void processGameStateChangePlatform() {
        platformToPattern(currentRound.getArena().pattern());
        logger.info("Changing level to: {}", currentRound.getArena().name());
        uiManager.updateBossBar(Component.text("Preparing").color(XBlock.WHITE.getDisplayText().color()));
        removeAllItems();

        currentState = GameState.XBLOCK_DISPLAY;
        scheduleNextStateAfterDelay(SHOW_XBLOCK_AFTER_TICKS);
    }

    private void processGameStateShowXBlock() {
        colorCountdown(plugin, currentRound.getxBlock().getDisplayText(), (int) currentRound.getDifficulty().getDurationInTicks() / 10 - 1);
        uiManager.updateBossBar(currentRound.getxBlock().getDisplayText());
        giveColorItemInHotbar(currentRound.getxBlock().getMaterial());

        currentState = GameState.XBLOCK_REMOVAL;
        scheduleNextStateAfterDelay(currentRound.getDifficulty().getDurationInTicks());
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
        } else if (getNextDifficulty(currentRound.getDifficulty()) == DIFFICULTY_0) {
            currentState = GameState.WIN_CONDITION_MAX_ROUNDS_EXCEEDED;
        } else if (!isSinglePlayerMode && roundParticipants.size() == 1) {
            currentState = GameState.WIN_CONDITION_SINGLE_WINNER;
        } else {
            currentState = GameState.DIFFICULTY_UPDATE;
        }

        scheduleNextStateAfterDelay(0L);
    }

    private void processGameStateWinConditionTie() {
        if (isSinglePlayerMode) {
            broadcastTitle(Component.text("§c§lYou lose!"), Component.empty());
        } else {
            broadcastTitle(Component.text("§c§lTie - you all lost!"), Component.empty());
        }
        platformToPattern(arenaManager.getStartingArena().pattern());
        teleportPlayersToPlatform(currentRound.getEliminations());

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(0L); // todo constant
    }

    private void processGameStateWinConditionWinner() {
        broadcastTitle(Component.text("§b§l" + currentRound.getParticipants().getFirst().getName() + " won!"), Component.empty());
        platformToPattern(arenaManager.getStartingArena().pattern());
        launchFireworkShow();

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(240L); // todo constant
    }

    private void processGameStateWinConditionRoundEnd() {
        if (isSinglePlayerMode) {
            broadcastTitle(Component.text("§a§lYou won!"), Component.empty());
        } else {
            broadcastTitle(Component.text("§b§l" +
                            currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", "))),
                    Component.text("§e§lare the winners!"));
        }
        platformToPattern(arenaManager.getStartingArena().pattern());
        launchFireworkShow();

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(240L); // todo constant
    }

    private void processGameStateUpdateDifficulty() {
        final List<Player> roundParticipants = currentRound.getParticipants();
        Difficulty nextDifficulty = getNextDifficulty(currentRound.getDifficulty());
        currentRound = new Round(arenaManager.getRandomArena(), nextDifficulty, roundParticipants);

        logger.info("Increasing speed level to: {}", nextDifficulty.getDurationInSecondsLabel());
        logger.info("Participants left: {}", roundParticipants.stream().map(Player::getName).collect(Collectors.joining(", ")));

        uiManager.updateScoreboardRoundInfo(roundParticipants.size(), nextDifficulty.getCounter(), nextDifficulty.getDurationInSecondsLabel());

        currentState = GameState.PLATFORM_CHANGE;
        scheduleNextStateAfterDelay(0L); // todo constant
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
        }.runTaskTimer(plugin, 0L, 25L); // todo constant
    }
}
