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

import static org.pine.blockparty.model.Difficulty.*;

public class GameManager {

    // todo if "Difficulty" enum was something more like "Duration", this might be moved there. consider
    private static final long STARTING_TIMER_TICKS = 140L;
    private static final long SHOW_XBLOCK_AFTER_TICKS = 70L;
    private static final long SECONDS_3_TICKS = 60L;

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
        playerManager.removeAllItems();

        currentState = GameState.FIRST_ROUND_START;
        processGameLoop();
    }

    public void stopGame() {
        if (currentState == GameState.IDLE) {
            return;
        }

        playerManager.teleportAllPlayersToLobby();
        this.platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        playerManager.removeAllItems();
        uiManager.updateBossBar(Component.text("§5§lBlockparty"));
        soundManager.stopMusic();

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

        player.sendMessage("You lose!");
        logger.info("Player {} has been eliminated", player.getName());
        uiManager.broadcastInChat(player.getName() + " have been eliminated");
        uiManager.updateScoreboardRoundParticipants(currentRound.getParticipants().size() - currentRound.getEliminations().size());
        playerManager.removeAllItems();
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
        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        playerManager.teleportAllPlayersToStartingPlatform();

        currentRound = new Round(arenaManager.getStartingArena(), DIFFICULTY_1, gameWorld.getPlayers());
        logger.info("Starting game with players: {}", currentRound.getParticipants().stream().map(Player::getName).collect(Collectors.joining(", ")));
        uiManager.updateScoreboard(gameWorld.getPlayers().size(), currentRound.getDifficulty().getCounter(), currentRound.getDifficulty().getDurationInSecondsLabel(), 1, 1);
        uiManager.updateBossBar(Component.text("Preparing").color(XBlock.WHITE.getDisplayText().color()));
        uiManager.startSplash();
        String songTitle = soundManager.playMusic();
        uiManager.broadcastInChat("§5§lLet's party! Now playing: " + songTitle);

        currentState = GameState.XBLOCK_DISPLAY;
        scheduleNextStateAfterDelay(STARTING_TIMER_TICKS);
    }

    private void processGameStateChangePlatform() {
        platformManager.platformToPattern(currentRound.getArena().pattern());
        logger.info("Changing level to: {}", currentRound.getArena().name());
        uiManager.updateBossBar(Component.text("Preparing").color(XBlock.WHITE.getDisplayText().color()));
        playerManager.removeAllItems();

        currentState = GameState.XBLOCK_DISPLAY;
        scheduleNextStateAfterDelay(SHOW_XBLOCK_AFTER_TICKS);
    }

    private void processGameStateShowXBlock() {
        uiManager.colorCountdown(plugin, currentRound.getxBlock().getDisplayText(), (int) currentRound.getDifficulty().getDurationInTicks() / 10 - 1);
        uiManager.updateBossBar(currentRound.getxBlock().getDisplayText());
        playerManager.giveColorItemInHotbar(currentRound.getxBlock().getMaterial());

        currentState = GameState.XBLOCK_REMOVAL;
        scheduleNextStateAfterDelay(currentRound.getDifficulty().getDurationInTicks());
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

        scheduleNextStateAfterDelay(0L);
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
        scheduleNextStateAfterDelay(0L); // todo constant
    }

    private void processGameStateWinConditionWinner() {
        uiManager.broadcastTitle(Component.text("§b§l" + currentRound.getParticipants().getFirst().getName() + " won!"), Component.empty());
        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        launchFireworkShow();

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(240L); // todo constant
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

                platformManager.launchRandomFirework();
                count++;
            }
        }.runTaskTimer(plugin, 0L, 25L); // todo constant
    }
}
