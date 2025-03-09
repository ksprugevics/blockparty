package org.pine.blockparty;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.pine.blockparty.configuration.Command;
import org.pine.blockparty.configuration.Configuration;
import org.pine.blockparty.event.PlayerEvent;
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
    private CommandManager commandManager;
    private UiManager uiManager;

    @Override
    public void onEnable() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        try {
            initializeConfigurationManager();
            initializeWorld();
            initializeLevelManager();
            initializeUiManager();
            initializeGameManager();
            initializeCommandManager();
            registerEvents(pluginManager);
            registerCommands();

            logger.info("Blockparty plugin loaded");
        } catch (Exception e) {
            logger.error("Failed to load blockparty", e);
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
        World gameWorld = Bukkit.getWorld(configurationManager.getConfigurationValue(Configuration.WORLD_NAME));
        if (gameWorld == null) {
            throw new WorldNullException();
        }
        this.gameWorld = gameWorld;
    }

    private void initializeLevelManager() throws ArenaLoadException {
        this.arenaManager = new ArenaManager(configurationManager.getConfigurationValue(Configuration.ARENA_FILE_PATH));
    }

    private void initializeGameManager() {
        this.gameManager = new GameManager(this, arenaManager, uiManager);
    }

    private void initializeUiManager() {
        this.uiManager = new UiManager();
    }

    private void initializeCommandManager() {
        this.commandManager = new CommandManager(gameManager, arenaManager);
    }

    private void registerEvents(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerEvent(gameManager, uiManager), this);
    }

    private void registerCommands() {
        Arrays.stream(Command.values())
                .map(command -> getCommand(command.getTriggerKeyword()))
                .filter(Objects::nonNull)
                .forEach(command -> command.setExecutor(this.commandManager));
    }
}