package org.pine.blockparty.managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.pine.blockparty.model.Arena;

import java.util.List;
import java.util.stream.Collectors;

import static org.pine.blockparty.configuration.Command.SET_ARENA;
import static org.pine.blockparty.configuration.Command.mapFromBukkitCommand;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final GameManager gameManager;
    private final ArenaManager arenaManager;
    private final PlatformManager platformManager;
    private final UiManager uiManager;
    private final StatsManager statsManager;
    private final Plugin plugin;

    public CommandManager(GameManager gameManager, ArenaManager arenaManager, PlatformManager platformManager,
                          UiManager uiManager, StatsManager statsManager, Plugin plugin) {
        this.gameManager = gameManager;
        this.arenaManager = arenaManager;
        this.platformManager = platformManager;
        this.uiManager = uiManager;
        this.statsManager = statsManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You do not have the permission to use this command");
            return false;
        }

        return switch (mapFromBukkitCommand(command)) {
            case START_GAME -> handleStartCommand();
            case STOP_GAME -> handleStopCommand();
            case ARENA_INFO -> handleLevelInfoCommand(player);
            case SET_ARENA -> handleLevelCommand(player, args);
            case FIREWORK_SHOW -> handleFireworkShow();
            case STATS_SHOW -> handleStatsShow(player);
            case SPAWN_POWERUP -> handleSpawnPowerup();
            case HELP -> handleHelp(player);
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
        uiManager.sendMessageToPlayerInChat(player, arenaManager.getArenaInfo());
        return true;
    }

    private boolean handleLevelCommand(Player player, String[] args) {
        if (args.length == 0) {
            uiManager.sendMessageToPlayerInChat(player, "Please provide a level name");
            return false;
        }

        final Arena arena = arenaManager.getArenaByName(args[0]);
        if (arena == null) {
            uiManager.sendMessageToPlayerInChat(player, "Level not loaded");
            return false;
        }

        platformManager.platformToPattern(arena.pattern());
        return true;
    }

    private boolean handleFireworkShow() {
        platformManager.startFireworkShow(plugin);
        return true;
    }

    private boolean handleStatsShow(Player player) {
        uiManager.sendMessageToPlayerInChat(player, statsManager.getPlayerStats(player).toString());
        return true;
    }

    private boolean handleSpawnPowerup() {
        platformManager.spawnPowerupBlock();
        return true;
    }

    private boolean handleHelp(Player player) {
        final String helpMessage = "---How to play:---\n" +
                "Stand on the correct color block before the time runs out!\n" +
                "Watch out for power-ups and special rounds!\n" +
                "Be the last dancer standing to win!\n \n" +
                "---Commands---\n" +
                "/bpstats - See your statistics\n" +
                "/bphelp  - See this information";
        uiManager.sendMessageToPlayerInChat(player, helpMessage);
        return true;
    }
}
