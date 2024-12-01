package org.pine.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PlayerManager {

    private static final World world = Bukkit.getWorld("world");
    private static final Location lobbyLocation = new Location(world, -11.5, 11.5, 16.5, 270f, 0f);
    private static final Location platformLocation = new Location(world, 16.5, 1, 16.5, 270f, 0f);

    private static final Logger logger = LoggerFactory.getLogger(PlayerManager.class);

    public static void teleportPlayerToLobby(Player player) {
        player.teleport(lobbyLocation);
        logger.info("Teleported {} to lobby", player.getName());
    }

    public static void teleportAllPlayersToPlatform() {
        if (world == null) {
            logger.error("Couldn't teleport player, world is null");
            return;
        }

        for (Player player : world.getPlayers()) {
            player.teleport(platformLocation);
        }
    }

    public static void teleportPlayersToPlatform(List<Player> players) {
        if (world == null) {
            logger.error("Couldn't teleport player, world is null");
            return;
        }

        for (Player player : players) {
            player.teleport(platformLocation);
        }
    }

    public static void teleportAllPlayersToLobby() {
        if (world == null) {
            logger.error("Couldn't teleport player, world is null");
            return;
        }

        for (Player player : world.getPlayers()) {
            player.teleport(lobbyLocation);
        }
    }
}
