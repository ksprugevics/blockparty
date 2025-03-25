package org.pine.blockparty.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.pine.blockparty.managers.GameManager;
import org.pine.blockparty.managers.PlayerManager;
import org.pine.blockparty.managers.StatsManager;
import org.pine.blockparty.managers.UiManager;
import org.pine.blockparty.model.PlayerStats;

public class PlayerEventListener implements Listener {

    private static final int FALL_THRESHOLD_Y = -5; // TODO: Configuration?

    private final GameManager gameManager;
    private final UiManager uiManager;
    private final PlayerManager playerManager;
    private final StatsManager statsManager;

    public PlayerEventListener(GameManager gameManager, UiManager uiManager, PlayerManager playerManager, StatsManager statsManager) {
        this.gameManager = gameManager;
        this.uiManager = uiManager;
        this.playerManager = playerManager;
        this.statsManager = statsManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerStats playerStats = statsManager.getPlayerStats(player);

        playerManager.initializePlayerOnJoin(player);
        uiManager.initializeUiForPlayer(player, gameManager.getCurrentRound(), playerStats);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        gameManager.handlePlayerLeaving(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (hasPlayerFallen(player.getLocation())) {
            gameManager.handlePlayerEliminated(player);
        }
    }

    private boolean hasPlayerFallen(Location playerLocation) {
        return playerLocation.getY() < FALL_THRESHOLD_Y;
    }
}
