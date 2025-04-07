package org.pine.blockparty;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.pine.blockparty.configuration.Command;
import org.pine.blockparty.configuration.Configuration;
import org.pine.blockparty.exceptions.ArenaLoadException;
import org.pine.blockparty.exceptions.StatsLoadException;
import org.pine.blockparty.exceptions.WorldNullException;
import org.pine.blockparty.listeners.PlayerEventListener;
import org.pine.blockparty.listeners.SpecialRoundEventListener;
import org.pine.blockparty.managers.ArenaManager;
import org.pine.blockparty.managers.CommandManager;
import org.pine.blockparty.managers.ConfigurationManager;
import org.pine.blockparty.managers.GameManager;
import org.pine.blockparty.managers.LobbyManager;
import org.pine.blockparty.managers.PlatformManager;
import org.pine.blockparty.managers.PlayerManager;
import org.pine.blockparty.managers.SoundManager;
import org.pine.blockparty.managers.StatsManager;
import org.pine.blockparty.managers.UiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class Blockparty extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger(Blockparty.class);

    private World gameWorld;
    private ConfigurationManager configurationManager;
    private GameManager gameManager;
    private ArenaManager arenaManager;
    private StatsManager statsManager;
    private PlatformManager platformManager;
    private PlayerManager playerManager;
    private CommandManager commandManager;
    private UiManager uiManager;
    private SoundManager soundManager;
    private LobbyManager lobbyManager;

    @Override
    public void onEnable() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        try {
            initializeConfigurationManager();
            initializeWorld();
            initializeArenaManager();
            initializeStatsManager();
            initializePlatformManager();
            initializePlayerManager();
            initializeSoundManager();
            initializeUiManager();
            initializeGameManager();
            initializeLobbyManager();
            initializeCommandManager();
            registerEvents(pluginManager);
            registerCommands();

            logger.info("Blockparty plugin loaded");
        } catch (Exception e) {
            logger.error("Failed to load Blockparty plugin", e);
        }
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.stopGame();
        }
        logger.info("Blockparty plugin disabled");
    }

    private void initializeConfigurationManager() {
        this.configurationManager = new ConfigurationManager(getConfig());
    }

    private void initializeWorld() {
        final World world = Bukkit.getWorld(configurationManager.getConfigurationValue(Configuration.WORLD_NAME));
        if (world == null) {
            throw new WorldNullException();
        }
        this.gameWorld = world;
    }

    private void initializeArenaManager() throws ArenaLoadException {
        this.arenaManager = new ArenaManager(configurationManager.getConfigurationValue(Configuration.ARENA_FILE_PATH));
    }

    private void initializeStatsManager() throws StatsLoadException {
        this.statsManager = new StatsManager(configurationManager.getConfigurationValue(Configuration.STATISTICS_FILE_PATH));
    }

    private void initializePlatformManager() {
        this.platformManager = new PlatformManager(gameWorld);
    }

    private void initializePlayerManager() {
        this.playerManager = new PlayerManager(gameWorld);
    }

    private void initializeSoundManager() {
        this.soundManager = new SoundManager(gameWorld);
    }

    private void initializeGameManager() {
        this.gameManager = new GameManager(gameWorld, arenaManager, uiManager, platformManager, playerManager,
                soundManager, statsManager, configurationManager, this);
    }

    private void initializeUiManager() {
        this.uiManager = new UiManager(gameWorld, soundManager);
    }

    private void initializeCommandManager() {
        this.commandManager = new CommandManager(gameManager, arenaManager, platformManager, uiManager, statsManager, lobbyManager, this);
    }

    private void initializeLobbyManager() {
        this.lobbyManager = new LobbyManager(gameWorld, gameManager, uiManager, configurationManager, this);
    }

    private void registerEvents(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerEventListener(gameManager, uiManager, playerManager, platformManager, lobbyManager), this);
        pluginManager.registerEvents(new SpecialRoundEventListener(), this);
    }

    private void registerCommands() {
        Arrays.stream(Command.values())
                .map(command -> getCommand(command.getTriggerKeyword()))
                .filter(Objects::nonNull)
                .forEach(command -> command.setExecutor(this.commandManager));
    }
}
