package org.pine.blockparty.managers;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.pine.blockparty.model.TeleportLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class PlayerManager {

    private static final byte XBLOCK_HOTBAR_SLOT = 4;
    private static final Set<Material> WHITELISTED_ITEMS = Set.of(
            Material.ENDER_PEARL,
            Material.FISHING_ROD,
            Material.TOTEM_OF_UNDYING
    );
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

    public void giveAllPlayersXblockInHotbar(Material xBlockMaterial) {
        final ItemStack item = new ItemStack(xBlockMaterial);

        for (Player player : gameWorld.getPlayers()) {
            player.getInventory().setItem(XBLOCK_HOTBAR_SLOT, item);
            player.updateInventory();
        }
    }

    public void clearAllPlayerInventories() {
        for (Player player : gameWorld.getPlayers()) {
            player.getInventory().clear();
        }
    }

    public void clearAllPlayerInventoriesExceptWhitelisted() {
        for (Player player : gameWorld.getPlayers()) {
            final Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && !WHITELISTED_ITEMS.contains(item.getType())) {
                    inventory.setItem(i, null);
                }
            }
        }
    }

    public void removeItemFromPlayerInventory(Player player, Material itemToDelete) {
            final Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType().equals(itemToDelete)) {
                    inventory.setItem(i, null);
                    return;
                }
            }
    }

    public void teleportPlayer(TeleportLocation teleportLocation, Player player) {
        Location location = teleportLocation.getLocation();
        location.setWorld(gameWorld);
        player.teleport(location);
    }
}
