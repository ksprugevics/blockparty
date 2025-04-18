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
import org.pine.blockparty.model.sound.Music;
import org.pine.blockparty.model.specials.SpecialRound;
import org.pine.blockparty.model.specials.SpecialRoundFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.pine.blockparty.configuration.Configuration.POWER_UP_CHANCE_DENOMINATOR;
import static org.pine.blockparty.configuration.Configuration.SPECIAL_ROUND_CHANCE_DENOMINATOR;
import static org.pine.blockparty.model.Difficulty.DIFFICULTY_0;
import static org.pine.blockparty.model.Difficulty.DIFFICULTY_1;
import static org.pine.blockparty.model.Difficulty.getNextDifficulty;

public class GameManager {

    private static final long STARTING_TIMER_TICKS = 140L;
    private static final long SHOW_XBLOCK_AFTER_TICKS = 70L;
    private static final long GAME_END_DELAY_TICKS = 240L;
    private static final long SECONDS_3_TICKS = 60L;
    private static final long SECONDS_1_TICKS = 20L;
    private static final long SECONDS_0_TICKS = 0L;

    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);
    private static final Random random = new Random();

    private final int powerUpSpawningChanceDenominator;
    private final int specialRoundOccurrenceChanceDenominator;

    private final World gameWorld;
    private final Blockparty plugin;
    private final ArenaManager arenaManager;
    private final UiManager uiManager;
    private final PlatformManager platformManager;
    private final PlayerManager playerManager;
    private final SoundManager soundManager;
    private final StatsManager statsManager;

    private GameState currentState = GameState.IDLE;
    private BukkitTask currentGameTask;
    private BukkitTask currentMusicTask;
    private List<Player> initialParticipants;
    private Round currentRound;
    private Music currentSong;
    private boolean isSinglePlayerMode = false;
    private SpecialRound specialRound;

    public GameManager(World gameWorld, ArenaManager arenaManager, UiManager uiManager, PlatformManager platformManager,
                       PlayerManager playerManager, SoundManager soundManager, StatsManager statsManager,
                       ConfigurationManager configurationManager, Blockparty plugin) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.uiManager = uiManager;
        this.platformManager = platformManager;
        this.playerManager = playerManager;
        this.soundManager = soundManager;
        this.statsManager = statsManager;
        this.gameWorld = gameWorld;

        this.powerUpSpawningChanceDenominator = Integer.parseInt(configurationManager.getConfigurationValue(POWER_UP_CHANCE_DENOMINATOR));
        this.specialRoundOccurrenceChanceDenominator = Integer.parseInt(configurationManager.getConfigurationValue(SPECIAL_ROUND_CHANCE_DENOMINATOR));

        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
    }

    public boolean isGameOngoing() {
        return currentState != GameState.IDLE;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void startGame(List<Player> participants) {
        if (currentState != GameState.IDLE || participants.isEmpty()) {
            return;
        }

        initialParticipants = new ArrayList<>(participants);
        currentRound = null;
        isSinglePlayerMode = participants.size() == 1;
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
        statsManager.savePlayerStatsToConfiguredFile();

        cancelScheduledTasks();
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
        playerManager.teleportPlayersToStartingPlatform(initialParticipants);

        currentRound = new Round(arenaManager.getStartingArena(), DIFFICULTY_1, initialParticipants);
        if (logger.isInfoEnabled()) {
            logger.info("Starting game with players: {}", initialParticipants.stream().map(Player::getName).collect(Collectors.joining(", ")));
        }
        playRandomSong();
        uiManager.updateScoreboardEntire(initialParticipants.size(), currentRound.getDifficulty().getCounter(), currentRound.getDifficulty().getDurationInSecondsLabel(), currentSong.getTitle());
        uiManager.updateBossBar(Component.text("Preparing").color(XBlock.WHITE.getDisplayText().color()));
        uiManager.broadcastStartScreen();

        currentState = GameState.XBLOCK_DISPLAY;
        scheduleNextStateAfterDelay(STARTING_TIMER_TICKS);
    }

    private void scheduleNextSong(long delayTicks) {
        logger.info("Running music delay for {} ticks, {} seconds", delayTicks, delayTicks / SECONDS_1_TICKS);
        currentMusicTask = Bukkit.getScheduler().runTaskLater(plugin, this::playRandomSong, delayTicks);
    }

    private void playRandomSong() {
        currentSong = soundManager.playRandomSongForAllPlayers();
        uiManager.broadcastInChat("§5§lNow playing: " + currentSong.getTitle());
        uiManager.updateSongPlaying(currentSong.getTitle());
        scheduleNextSong(currentSong.getLengthInTicks());
    }

    private void processGameStateChangePlatform() {
        platformManager.platformToPattern(currentRound.getArena().pattern());
        logger.info("Changing level to: {}", currentRound.getArena().name());
        uiManager.updateBossBar(Component.text("Preparing").color(XBlock.WHITE.getDisplayText().color()));
        playerManager.clearAllPlayerInventoriesExceptWhitelisted();
        handlePowerUpSpawn();
        handleSpecialRoundTrigger();

        currentState = GameState.XBLOCK_DISPLAY;
        scheduleNextStateAfterDelay(SHOW_XBLOCK_AFTER_TICKS);
    }

    private void handlePowerUpSpawn() {
        if (random.nextInt(powerUpSpawningChanceDenominator) == 0) {
            spawnPowerup();
        }
    }

    private void spawnPowerup() {
        uiManager.broadcastActionBar(Component.text("§6§l✦ A power-up has spawned! ✦"));
        platformManager.spawnPowerupBlock();
    }

    private void handleSpecialRoundTrigger() {
        if (random.nextInt(specialRoundOccurrenceChanceDenominator) == 0) {
            startSpecialRound();
        }
    }

    private void startSpecialRound() {
        specialRound = SpecialRoundFactory.getRandomSpecialRound();
        specialRound.start(gameWorld, currentRound.getParticipants(), currentRound.getDifficulty().getDurationInTicks(), plugin);
        uiManager.broadcastActionBar(specialRound.getMessage());
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
        platformManager.resetActivePowerUp();

        currentState = GameState.ROUND_EVALUATION;
        scheduleNextStateAfterDelay(SECONDS_3_TICKS);
    }

    private void processGameStateRoundEvaluation() {
        final List<Player> roundEliminations = currentRound.getEliminations();
        roundEliminations.forEach(statsManager::incrementPlayerLoses);

        List<Player> roundParticipants = currentRound.getParticipants();
        stopSpecialRound();
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

    private void stopSpecialRound() {
        if (specialRound != null) {
            specialRound.stop(gameWorld, currentRound.getParticipants(), currentRound.getDifficulty().getDurationInTicks(), plugin);
            specialRound = null;
        }
    }

    private void processGameStateWinConditionTie() {
        if (isSinglePlayerMode) {
            uiManager.broadcastTitle(Component.text("§c§lYou lose!"), Component.empty());
        } else {
            uiManager.broadcastTitle(Component.text("§c§lTie - you all lost!"), Component.empty());
        }

        currentRound.getParticipants().forEach(statsManager::incrementPlayerTies);
        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        playerManager.teleportPlayersToPlatform(currentRound.getEliminations());

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(SECONDS_0_TICKS);
    }

    private void processGameStateWinConditionWinner() {
        final List<Player> roundParticipants = currentRound.getParticipants();
        roundParticipants.forEach(statsManager::incrementPlayerWins);

        uiManager.broadcastTitle(Component.text("§b§l" + roundParticipants.getFirst().getName() + " won!"), Component.empty());
        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        platformManager.startFireworkShow(plugin);

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(GAME_END_DELAY_TICKS);
    }

    private void processGameStateWinConditionRoundEnd() {
        final List<Player> roundParticipants = currentRound.getParticipants();
        roundParticipants.forEach(statsManager::incrementPlayerWins);

        if (isSinglePlayerMode) {
            uiManager.broadcastTitle(Component.text("§a§lYou won!"), Component.empty());
        } else {
            uiManager.broadcastTitle(Component.text("§b§l" + roundParticipants.stream().map(Player::getName).collect(Collectors.joining(", "))),
                    Component.text("§e§lare the winners!"));
        }
        platformManager.platformToPattern(arenaManager.getStartingArena().pattern());
        platformManager.startFireworkShow(plugin);

        currentState = GameState.GAME_OVER;
        scheduleNextStateAfterDelay(GAME_END_DELAY_TICKS);
    }

    private void processGameStateUpdateDifficulty() {
        final List<Player> roundParticipants = currentRound.getParticipants();
        roundParticipants.forEach(statsManager::incrementPlayerRoundsSurvived);

        final Difficulty nextDifficulty = getNextDifficulty(currentRound.getDifficulty());
        currentRound = new Round(arenaManager.getRandomArena(), nextDifficulty, roundParticipants);

        logger.info("Increasing speed level to: {}", nextDifficulty.getDurationInSecondsLabel());
        if (logger.isInfoEnabled()) {
            logger.info("Participants left: {}", roundParticipants.stream().map(Player::getName).collect(Collectors.joining(", ")));
        }
        uiManager.updateScoreboardRoundParticipants(roundParticipants.size());
        uiManager.updateScoreboardRoundInfo(nextDifficulty.getCounter(), nextDifficulty.getDurationInSecondsLabel());

        currentState = GameState.PLATFORM_CHANGE;
        scheduleNextStateAfterDelay(SECONDS_0_TICKS);
    }

    private void cancelScheduledTasks() {
        if (currentGameTask != null) {
            currentGameTask.cancel();
            currentGameTask = null;
        }

        if (currentMusicTask != null) {
            currentMusicTask.cancel();
            currentMusicTask = null;
        }
    }
}
