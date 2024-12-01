package org.pine.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.Duration;

public class UiManager {

    private static final World world = Bukkit.getWorld("world");

    private static final Duration fadeIn = Duration.ofSeconds(1L);
    private static final Duration stay = Duration.ofSeconds(10L);
    private static final Duration fadeOut = Duration.ofSeconds(1L);

    public static void broadcastTitle(String msg) {
        if (world == null) {
            return;
        }

        for (Player player : world.getPlayers()) {
            player.showTitle(Title.title(Component.text(msg), Component.text(""), Title.Times.times(fadeIn, stay, fadeOut)));
        }
    }

    public static void sendMessageToPlayerInChat(Player player, String msg) {
        player.sendMessage(msg);
    }

    public static void broadcastInChat(String msg) {
        if (world == null) {
            return;
        }

        for (Player player : world.getPlayers()) {
            player.sendMessage(msg);
        }
    }

    public static void broadcastActionBar(Component textComponent) {
        if (world == null) {
            return;
        }

        for (Player player : world.getPlayers()) {
            player.sendActionBar(textComponent.decorate(TextDecoration.BOLD));
        }
    }
}
