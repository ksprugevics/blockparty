package org.pine;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class UiUtils {

    private static final World world = Bukkit.getWorld("world");

    private static final int fadeIn = 20;  // Time for fade-in
    private static final int stay = 60;    // Time to stay on screen
    private static final int fadeOut = 20; // Time for fade-out

    public static void broadcastMessage(String msg) {
        for (Player player : world.getPlayers()) {
            player.sendTitle(msg, "", fadeIn, stay, fadeOut);
        }
    }

    public static void sendMessageToPlayerInChat(Player player, String msg) {
        player.sendMessage(msg);
    }

    public static void sendMessageToAllPlayersInChat(String msg) {
        for (Player player : world.getPlayers()) {
            player.sendMessage(msg);
        }
    }
}
