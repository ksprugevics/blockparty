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
import org.pine.blockparty.managers.PlayerManager;
import org.pine.blockparty.managers.UiManager;
import org.pine.blockparty.model.Round;


public class PlayerEvent implements Listener {

    private static final int FALL_THRESHOLD_Y = -5;

    private final GameManager gameManager;
    private final UiManager uiManager;
    private final PlayerManager playerManager;

    public PlayerEvent(GameManager gameManager, UiManager uiManager, PlayerManager playerManager) {
        this.gameManager = gameManager;
        this.uiManager = uiManager;
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // todo simplify
        final Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        playerManager.teleportPlayerToLobby(player);
        uiManager.showScoreboardToPlayer(player);
        final Round currentRound = gameManager.getCurrentRound();
        if (currentRound != null) {
            uiManager.updateScoreboardRoundInfo(currentRound.getParticipants().size(), currentRound.getDifficulty().getCounter(),
                    currentRound.getDifficulty().getDurationInSecondsLabel());
        }
        player.showBossBar(uiManager.getBossBar());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        gameManager.playerLeft(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location location = player.getLocation();
        if (location.getY() < FALL_THRESHOLD_Y) {
            gameManager.playerEliminated(player);
        }
    }
}
