package org.pine.blockparty.managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.pine.blockparty.model.Arena;

import java.util.List;
import java.util.stream.Collectors;

import static org.pine.blockparty.configuration.Command.*;
import static org.pine.blockparty.managers.PlatformManager.platformToPattern;
import static org.pine.blockparty.managers.UiManager.sendMessageToPlayerInChat;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final GameManager gameManager;
    private final ArenaManager arenaManager;

    public CommandManager(GameManager gameManager, ArenaManager arenaManager) {
        this.gameManager = gameManager;
        this.arenaManager = arenaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player) || !player.hasPermission("blockparty.admin")) {
            sender.sendMessage("You do not have the permission to use this command");
            return false;
        }

        return switch (mapFromBukkitCommand(command)) {
            case START_GAME -> handleStartCommand();
            case STOP_GAME -> handleStopCommand();
            case ARENA_INFO -> handleLevelInfoCommand(player);
            case SET_ARENA -> handleLevelCommand(player, args);
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (mapFromBukkitCommand(command).equals(SET_ARENA) && args.length == 1) {
            return arenaManager.getArenaList().stream().map(Arena::name)
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
        sendMessageToPlayerInChat(player, arenaManager.getArenaInfo());
        return true;
    }

    private boolean handleLevelCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("Please provide a level name");
            return false;
        }

        final Arena arena = arenaManager.getArenaByName(args[0]);
        if (arena == null) {
            sendMessageToPlayerInChat(player, "Level not loaded");
            return false;
        }

        platformToPattern(arena.pattern());
        return true;
    }
}
