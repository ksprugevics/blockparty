package org.pine.blockparty.managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.pine.blockparty.model.Arena;

import java.util.List;
import java.util.stream.Collectors;

import static org.pine.blockparty.managers.PlatformManager.platformToPattern;
import static org.pine.blockparty.managers.UiManager.sendMessageToPlayerInChat;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final GameManager gameManager;
    private final LevelManager levelManager;

    public CommandManager(GameManager gameManager, LevelManager levelManager) {
        this.gameManager = gameManager;
        this.levelManager = levelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player) || !player.hasPermission("blockparty.admin")) {
            sender.sendMessage("You do not have the permission to use this command");
            return false;
        }

        return switch (command.getName()) {
            case "bpstart" -> handleStartCommand();
            case "bpstop" -> handleStopCommand();
            case "bplvlinfo" -> handleLevelInfoCommand(player);
            case "bplvl" -> handleLevelCommand(player, args);
            default -> false;
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("bplvl") && args.length == 1) {
            return levelManager.getLevelList().stream().map(Arena::name)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private boolean handleStartCommand() {
        gameManager.startGame();
        return true;
    }

    private boolean handleStopCommand() {
        gameManager.stopGame();
        return true;
    }

    private boolean handleLevelInfoCommand(Player player) {
        sendMessageToPlayerInChat(player, levelManager.getLevelInfo());
        return true;
    }

    private boolean handleLevelCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("Please provide a level name");
            return false;
        }

        final Arena arena = levelManager.getLevelByName(args[0]);
        if (arena == null) {
            sendMessageToPlayerInChat(player, "Level not loaded");
            return false;
        }

        platformToPattern(arena.pattern());
        return true;
    }
}
