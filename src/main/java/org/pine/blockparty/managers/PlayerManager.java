package org.pine.blockparty.managers;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.pine.blockparty.model.TeleportLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PlayerManager {

    private static final Logger logger = LoggerFactory.getLogger(PlayerManager.class);

    private final World gameWorld;

    public PlayerManager(World gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void initializePlayerOnJoin(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        teleportPlayerToLobby(player);
    }

    public void teleportPlayerToLobby(Player player) {
        teleportPlayer(TeleportLocation.LOBBY, player);
        logger.info("Teleported {} to lobby", player.getName());
    }

    public void teleportAllPlayersToStartingPlatform() {
        final List<Player> players = gameWorld.getPlayers();
        final List<TeleportLocation> startingLocations = TeleportLocation.getStartingLocations();

        for (int i = 0; i < players.size(); i++) {
            teleportPlayer(startingLocations.get(i % startingLocations.size()), players.get(i));
        }
    }

    public void teleportPlayersToPlatform(List<Player> players) {
        for (Player player : players) {
            teleportPlayer(TeleportLocation.PLATFORM_CENTER, player);
        }
    }

    public void teleportAllPlayersToLobby() {
        for (Player player : gameWorld.getPlayers()) {
            teleportPlayer(TeleportLocation.LOBBY, player);
        }
    }

    public void giveColorItemInHotbar(Material material) {
        final ItemStack item = new ItemStack(material);

        for (Player player : gameWorld.getPlayers()) {
            player.getInventory().setItem(4, item);
            player.updateInventory();
        }
    }

    public void removeAllItems() {

        for (Player player : gameWorld.getPlayers()) {
            player.getInventory().clear();
        }
    }

    private void teleportPlayer(TeleportLocation teleportLocation, Player player) {
        Location location = teleportLocation.getLocation();
        location.setWorld(gameWorld);
        player.teleport(location);
    }
}
