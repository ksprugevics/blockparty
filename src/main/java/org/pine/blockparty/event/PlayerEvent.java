package org.pine.blockparty.event;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.pine.blockparty.managers.GameManager;

import static org.pine.blockparty.managers.PlayerManager.teleportPlayerToLobby;

public class PlayerEvent implements Listener {

    private static final int FALL_THRESHOLD_Y = -5;
    private final GameManager gameManager;

    public PlayerEvent(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        teleportPlayerToLobby(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        gameManager.playerLeft(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location loc = player.getLocation();
        if (loc.getY() < FALL_THRESHOLD_Y) {
            gameManager.playerEliminated(player);
        }
    }
}
