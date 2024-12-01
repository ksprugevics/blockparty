package org.pine;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.pine.event.PlayerEvent;
import org.pine.exceptions.LevelLoadException;
import org.pine.managers.GameManager;
import org.pine.managers.LevelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.pine.managers.UiManager.sendMessageToPlayerInChat;

public class Blockparty extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger(Blockparty.class);
    private GameManager gameManager;
    private LevelManager levelManager;

    @Override
    public void onEnable() {
        final PluginManager manager = Bukkit.getServer().getPluginManager();
        initializeLevelManager(manager);
        this.gameManager = new GameManager(this, levelManager);
        manager.registerEvents(new PlayerEvent(this.gameManager), this);

        logger.info("Blockparty plugin loaded");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && sender.isOp()) {   // todo better OP handling
            switch (command.getName()) {
                case "bpstart" -> gameManager.startGame();
                case "bplvlinfo" -> sendMessageToPlayerInChat((Player) sender, levelManager.getLevelInfo());
            }

            return true;
        }

        return false;
    }

    private void initializeLevelManager(PluginManager manager) {
        try {
            this.levelManager = new LevelManager();
        } catch (LevelLoadException e) {
            logger.error("Error loading levels: ", e);
            manager.disablePlugin(this);
        }
    }
}