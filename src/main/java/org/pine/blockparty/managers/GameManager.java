package org.pine.blockparty.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
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

import static org.pine.blockparty.model.Difficulty.*;

public class GameManager {

    private static final long STARTING_TIMER_TICKS = 140L;
    private static final long SHOW_XBLOCK_AFTER_TICKS = 70L;
    private static final long GAME_END_DELAY_TICKS = 240L;
    private static final long SECONDS_3_TICKS = 60L;
    private static final long SECONDS_1_TICKS = 20L;
    private static final long SECONDS_0_TICKS = 0L;

    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);

    private final World gameWorld;
    private final Blockparty plugin;
    private final ArenaManager arenaManager;
    private final UiManager uiManager;
    private final PlatformManager platformManager;
    private final PlayerManager playerManager;
    private final SoundManager soundManager;

    private GameState currentState = GameState.IDLE;
    private BukkitTask currentGameTask;
    private Round currentRound;
    private boolean isSinglePlayerMode = false;

    public GameManager(World gameWorld, ArenaManager arenaManager, UiManager uiManager, PlatformManager platformManager,
                       PlayerManager playerManager, SoundManager soundManager, Blockparty plugin) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.uiManager = uiManager;
        this.platformManager = platformManager;
        this.playerManager = playerManager;
        this.soundManager = soundManager;
        this.gameWorld = gameWorld;

        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void startGame() {
        if (currentState != GameState.IDLE) {
            return;
        }

        currentRound = null;
        isSinglePlayerMode = gameWorld.getPlayers().size() == 1;
        playerManager.clearAllPlayerInventories();

        currentState = GameState.FIRST_ROUND_START;
        processGameLoop();
    }

    public void stopGame() {
        if (currentState == GameState.IDLE) {
            return;
        }

        playerManager.teleportAllPlayersToLobby();
        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        playerManager.clearAllPlayerInventories();
        uiManager.updateBossBar(Component.text("§5§lBlockparty"));
        soundManager.stopSoundsForAllPlayers();

        if (currentGameTask != null) {
            currentGameTask.cancel();
            currentGameTask = null;
        }
        currentState = GameState.IDLE;
    }

    public void handlePlayerLeaving(Player player) {
        if (currentRound != null && currentRound.getParticipants().contains(player)) {
            handlePlayerEliminated(player);
        }
    }

    public void handlePlayerEliminated(Player player) {
        gameWorld.strikeLightningEffect(player.getLocation());

        playerManager.teleportPlayerToLobby(player);
        currentRound.getEliminations().add(player);

        uiManager.sendMessageToPlayerInChat(player, "You lose!");
        logger.info("Player {} has been eliminated", player.getName());
        uiManager.broadcastInChat(player.getName() + " have been eliminated");
        uiManager.updateScoreboardRoundParticipants(currentRound.getParticipants().size() - currentRound.getEliminations().size());
        playerManager.clearAllPlayerInventories();
    }

    private void scheduleNextStateAfterDelay(long delayTicks) {
        logger.info("Running delay for {} ticks, {} seconds", delayTicks, delayTicks / SECONDS_1_TICKS);
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
        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        playerManager.teleportAllPlayersToStartingPlatform();

        currentRound = new Round(arenaManager.getStartingArena(), DIFFICULTY_1, gameWorld.getPlayers());
        logger.info("Starting game with players: {}", currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", ")));
        uiManager.updateScoreboardEntire(gameWorld.getPlayers().size(), currentRound.getDifficulty().getCounter(), currentRound.getDifficulty().getDurationInSecondsLabel(), 1, 1);
        uiManager.updateBossBar(Component.text("Preparing").color(XBlock.WHITE.getDisplayText().color()));
        uiManager.broadcastStartScreen();
        final String songTitle = soundManager.playRandomSongForAllPlayers();
        uiManager.broadcastInChat("§5§lLet's party! Now playing: " + songTitle);

        currentState = GameState.XBLOCK_DISPLAY;
        scheduleNextStateAfterDelay(STARTING_TIMER_TICKS);
    }

    private void processGameStateChangePlatform() {
        platformManager.platformToPattern(currentRound.getArena().pattern());
        logger.info("Changing level to: {}", currentRound.getArena().name());
        uiManager.updateBossBar(Component.text("Preparing").color(XBlock.WHITE.getDisplayText().color()));
        playerManager.clearAllPlayerInventories();

        currentState = GameState.XBLOCK_DISPLAY;
        scheduleNextStateAfterDelay(SHOW_XBLOCK_AFTER_TICKS);
    }

    private void processGameStateShowXBlock() {
        uiManager.colorCountdown(calculateColorCountdownCounter(), currentRound.getxBlock().getDisplayText(), plugin);
        uiManager.updateBossBar(currentRound.getxBlock().getDisplayText());
        playerManager.giveAllPlayersXblockInHotbar(currentRound.getxBlock().getMaterial());

        currentState = GameState.XBLOCK_REMOVAL;
        scheduleNextStateAfterDelay(currentRound.getDifficulty().getDurationInTicks());
    }

    private int calculateColorCountdownCounter() {
        return (int) currentRound.getDifficulty().getDurationInTicks() / 10 - 1;
    }

    private void processGameStateXBlockRemoval() {
        uiManager.broadcastActionBar(Component.text("§c§lX Stop X"));
        platformManager.platformRemoveXBlock(currentRound.getxBlock().getMaterial());

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

        scheduleNextStateAfterDelay(SECONDS_0_TICKS);
    }

    private void processGameStateWinConditionTie() {
        if (isSinglePlayerMode) {
            uiManager.broadcastTitle(Component.text("§c§lYou lose!"), Component.empty());
        } else {
            uiManager.broadcastTitle(Component.text("§c§lTie - you all lost!"), Component.empty());
        }
        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        playerManager.teleportPlayersToPlatform(currentRound.getEliminations());

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(SECONDS_0_TICKS);
    }

    private void processGameStateWinConditionWinner() {
        uiManager.broadcastTitle(Component.text("§b§l" + currentRound.getParticipants().getFirst().getName() + " won!"), Component.empty());
        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        platformManager.startFireworkShow(plugin);

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(GAME_END_DELAY_TICKS);
    }

    private void processGameStateWinConditionRoundEnd() {
        if (isSinglePlayerMode) {
            uiManager.broadcastTitle(Component.text("§a§lYou won!"), Component.empty());
        } else {
            uiManager.broadcastTitle(Component.text("§b§l" +
                            currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", "))),
                    Component.text("§e§lare the winners!"));
        }
        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        platformManager.startFireworkShow(plugin);

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(GAME_END_DELAY_TICKS);
    }

    private void processGameStateUpdateDifficulty() {
        final List<Player> roundParticipants = currentRound.getParticipants();
        final Difficulty nextDifficulty = getNextDifficulty(currentRound.getDifficulty());
        currentRound = new Round(arenaManager.getRandomArena(), nextDifficulty, roundParticipants);

        logger.info("Increasing speed level to: {}", nextDifficulty.getDurationInSecondsLabel());
        logger.info("Participants left: {}", roundParticipants.stream().map(Player::getName).collect(Collectors.joining(", ")));

        uiManager.updateScoreboardRoundInfo(roundParticipants.size(), nextDifficulty.getCounter(), nextDifficulty.getDurationInSecondsLabel());

        currentState = GameState.PLATFORM_CHANGE;
        scheduleNextStateAfterDelay(SECONDS_0_TICKS);
    }
}
