package org.pine.blockparty;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.pine.blockparty.event.PlayerEvent;
import org.pine.blockparty.exceptions.LevelLoadException;
import org.pine.blockparty.managers.CommandManager;
import org.pine.blockparty.managers.GameManager;
import org.pine.blockparty.managers.LevelManager;
import org.pine.blockparty.managers.UiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Blockparty extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger(Blockparty.class);

    private GameManager gameManager;
    private LevelManager levelManager;
    private CommandManager commandManager;
    private UiManager uiManager;

    @Override
    public void onEnable() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        try {
            initializeLevelManager();
            initializeUiManager();
            initializeGameManager();
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

    private void initializeLevelManager() throws LevelLoadException {
        this.levelManager = new LevelManager();
    }

    private void initializeGameManager() {
        this.gameManager = new GameManager(this, levelManager, uiManager);
    }

    private void initializeUiManager() {
        this.uiManager = new UiManager();
    }

    private void registerEvents(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerEvent(gameManager, uiManager), this);
    }

    private void registerCommands() {
        this.commandManager = new CommandManager(gameManager, levelManager);

        getCommand("bpstart").setExecutor(commandManager);
        getCommand("bpstop").setExecutor(commandManager);
        getCommand("bplvlinfo").setExecutor(commandManager);
        getCommand("bplvl").setExecutor(commandManager);
    }
}