package org.pine.event;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.pine.managers.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.pine.managers.PlayerManager.teleportPlayerToLobby;

public class PlayerEvent implements Listener {

    private static final int FALL_THRESHOLD_Y = -15;
    private static final Logger logger = LoggerFactory.getLogger(PlayerEvent.class);
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
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        if (loc.getY() < FALL_THRESHOLD_Y) {
            gameManager.playerEliminated(player);
        }
    }
}
