package org.pine.blockparty;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.pine.blockparty.configuration.Command;
import org.pine.blockparty.configuration.Configuration;
import org.pine.blockparty.listeners.PlayerEventListener;
import org.pine.blockparty.exceptions.ArenaLoadException;
import org.pine.blockparty.exceptions.WorldNullException;
import org.pine.blockparty.managers.*;
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
    private PlatformManager platformManager;
    private PlayerManager playerManager;
    private CommandManager commandManager;
    private UiManager uiManager;
    private SoundManager soundManager;

    @Override
    public void onEnable() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        try {
            initializeConfigurationManager();
            initializeWorld();
            initializeArenaManager();
            initializePlatformManager();
            initializePlayerManager();
            initializeSoundManager();
            initializeUiManager();
            initializeGameManager();
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
        final World gameWorld = Bukkit.getWorld(configurationManager.getConfigurationValue(Configuration.WORLD_NAME));
        if (gameWorld == null) {
            throw new WorldNullException();
        }
        this.gameWorld = gameWorld;
    }

    private void initializeArenaManager() throws ArenaLoadException {
        this.arenaManager = new ArenaManager(configurationManager.getConfigurationValue(Configuration.ARENA_FILE_PATH));
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
        this.gameManager = new GameManager(gameWorld, arenaManager, uiManager, platformManager, playerManager, soundManager, this);
    }

    private void initializeUiManager() {
        this.uiManager = new UiManager(gameWorld, soundManager);
    }

    private void initializeCommandManager() {
        this.commandManager = new CommandManager(gameManager, arenaManager, platformManager, uiManager, this);
    }

    private void registerEvents(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerEventListener(gameManager, uiManager, playerManager), this);
    }

    private void registerCommands() {
        Arrays.stream(Command.values())
                .map(command -> getCommand(command.getTriggerKeyword()))
                .filter(Objects::nonNull)
                .forEach(command -> command.setExecutor(this.commandManager));
    }
}