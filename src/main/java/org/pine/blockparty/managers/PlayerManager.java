package org.pine.blockparty.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PlayerManager {

    private static final World world = Bukkit.getWorld("world");
    private static final Location lobbyLocation = new Location(world, -11.5, 11.5, 16.5, 270f, 0f);
    private static final Location platformLocation = new Location(world, 16.5, 1, 16.5, 270f, 0f);

    // todo move somewhere?
    public static final List<Location> startingLocations = List.of(
        new Location(world, 3, 1, 4, 270f, 0f),
        new Location(world, 3, 1, 8, 270f, 0f),
        new Location(world, 3, 1, 12, 270f, 0f),
        new Location(world, 3, 1, 16, 270f, 0f),
        new Location(world, 3, 1, 20, 270f, 0f),
        new Location(world, 3, 1, 24, 270f, 0f),
        new Location(world, 3, 1, 28, 270f, 0f),
        new Location(world, 28, 1, 28, 90f, 0f),
        new Location(world, 28, 1, 24, 90f, 0f),
        new Location(world, 28, 1, 20, 90f, 0f),
        new Location(world, 28, 1, 16, 90f, 0f),
        new Location(world, 28, 1, 12, 90f, 0f),
        new Location(world, 28, 1, 8, 90f, 0f),
        new Location(world, 28, 1, 4, 90f, 0f)
    );

    private static final Logger logger = LoggerFactory.getLogger(PlayerManager.class);

    public static void teleportPlayerToLobby(Player player) {
        player.teleport(lobbyLocation);
        logger.info("Teleported {} to lobby", player.getName());
    }

    public static void teleportAllPlayersToStartingPlatform() {
        if (world == null) {
            logger.error("Couldn't teleport player, world is null");
            return;
        }

        List<Player> players = world.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).teleport(startingLocations.get(i % startingLocations.size()));
        }
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

    public static void giveColorItemInHotbar(Material material) {
        if (world == null) {
            return;
        }
        final ItemStack item = new ItemStack(material);

        for (Player player : world.getPlayers()) {
            player.getInventory().setItem(4, item);
            player.updateInventory();
        }
    }

    public static void removeAllItems() {
        if (world == null) {
            return;
        }

        for (Player player : world.getPlayers()) {
            player.getInventory().clear();
        }
    }
}
